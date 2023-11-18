package br.com.fiap.lifehealth

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RegisterActivity : AppCompatActivity() {
    private lateinit var spinnerSpecialty: Spinner
    private lateinit var auth: FirebaseAuth
    private lateinit var emailRegister: EditText
    private lateinit var passwordRegister: EditText
    private lateinit var buttonRegister: Button
    private lateinit var linkLogin: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Logica para o campo de seleção de especialidade
        spinnerSpecialty = findViewById(R.id.spinnerSpecialty)
        val specialties = listOf("Cardiologia", "Dermatologia", "Pediatria", "Ortopedia", "Ginecologia")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, specialties)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSpecialty.adapter = adapter

        // Firebase
        auth = Firebase.auth

        emailRegister = findViewById(R.id.emailRegister)
        passwordRegister = findViewById(R.id.passwordRegister)
        buttonRegister = findViewById(R.id.buttonRegister)

        buttonRegister.setOnClickListener {
            val email = emailRegister.text.toString()
            val password = passwordRegister.text.toString()
            registerUser(email, password)
        }

        linkLogin = findViewById(R.id.linkLogin)

        linkLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(baseContext, "Erro: Algo de errado na criação da conta, tente novamente.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}