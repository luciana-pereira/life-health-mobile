package service

import model.Patient
import retrofit2.Response
import retrofit2.http.*

interface FHIRService {
    @GET("patient/{id}")
    suspend fun getPatient(@Path("id") patientId: String): Response<Patient>

    @POST("patient")
    suspend fun createPatient(@Body patient: Patient): Response<Patient>

    @GET("patient")
    suspend fun getAllPatients(): Response<List<Patient>>

    @PUT("patient/{id}")
    suspend fun updatePatient(@Path("id") patientId: String, @Body patient: Patient): Response<Patient>

    @DELETE("patient/{id}")
    suspend fun deletePatient(@Path("id") patientId: String): Response<Unit>
}
