package service

import model.Patient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.Body

abstract class FHIRServiceImpl(
    private val retrofit: Retrofit
) : FHIRService {

    override suspend fun getPatient(patientId: String): Response<Patient> {
        return retrofit.create(FHIRService::class.java).getPatient(patientId)
    }

    override suspend fun createPatient(patient: Patient): Response<Patient> {
        return retrofit.create(FHIRService::class.java).createPatient(patient)
    }

    override suspend fun getAllPatients(): Response<List<Patient>> {
        return retrofit.create(FHIRService::class.java).getAllPatients()
    }

    override suspend fun updatePatient(patientId: String, patient: Patient): Response<Patient> {
        return retrofit.create(FHIRService::class.java).updatePatient(patientId, patient)
    }

    override suspend fun deletePatient(patientId: String): Response<Unit> {
        return retrofit.create(FHIRService::class.java).deletePatient(patientId)
    }
}