package br.com.zup.car

import br.com.zup.CarRequest
import br.com.zup.CarsGrpcServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.AbstractBlockingStub
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jdk.net.SocketFlow
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class CarsEndpointTest(
    val grpcClient: CarsGrpcServiceGrpc.CarsGrpcServiceBlockingStub,
    val carRepository : CarRepository
) {
    @Test
    fun `should add new car`(){

        carRepository.deleteAll()

        val response = grpcClient.addCar(CarRequest.newBuilder()
            .setModel("Renegade")
            .setLicense("nux-2513").build()
        )

        with(response){
            assertNotNull(this.carId)
            assertTrue(carRepository.existsById(this.carId))

        }

    }

    @Test
    fun `should not add a car with same license`(){
        val carFromDb = carRepository.save(Car(model = "Renegade", license = "nux-2513"))

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.addCar(CarRequest.newBuilder()
                .setModel("Renegade")
                .setLicense("nux-2513")
                .build())
        }

        with(error){
            assertEquals(Status.ALREADY_EXISTS.code, this.status.code)
        }
    }

    @Test
    fun `should not add a car with license or model null or empty`(){
        carRepository.deleteAll()

        val errorEmpty = assertThrows<StatusRuntimeException> {
            grpcClient.addCar(CarRequest.newBuilder()
                .setModel("")
                .setLicense("")
                .build())
        }

        val errorNull = assertThrows<NullPointerException> {
            grpcClient.addCar(CarRequest.newBuilder()
                .setModel(null)
                .setLicense(null)
                .build())
        }

        with(errorEmpty){
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
        }

        with(errorNull){
            assertTrue(true)
        }

    }

    @Factory
    class Clients{
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): CarsGrpcServiceGrpc.CarsGrpcServiceBlockingStub{
            return CarsGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}