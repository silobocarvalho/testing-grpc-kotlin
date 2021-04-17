package br.com.zup.car

import br.com.zup.CarRequest
import br.com.zup.CarResponse
import br.com.zup.CarsGrpcServiceGrpc
import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class CarsEndpoint(@Inject val carRepository: CarRepository) : CarsGrpcServiceGrpc.CarsGrpcServiceImplBase() {

    override fun addCar(request: CarRequest, responseObserver: StreamObserver<CarResponse>) {

        if(carRepository.existsByLicense(request.license)){
            responseObserver.onError(Status.ALREADY_EXISTS
                .withDescription("Car plate already registered")
                .asRuntimeException())
            return
        }

        val car = Car(model = request.model, license = request.license)

        try{
            carRepository.save(car)
        }catch (e: ConstraintViolationException){
            //If some information about car is null
            responseObserver.onError(Status.INVALID_ARGUMENT
                . withDescription("Invalid car information")
                .asRuntimeException())
            return
        }

        responseObserver.onNext(CarResponse.newBuilder().setCarId(car.id!!).build())
        responseObserver.onCompleted()

    }
}