package br.com.fiap.lifehealth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private lateinit var emailLogin: EditText
    private lateinit var passwordLogin: EditText
    private lateinit var buttonLogin: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_login)

        //Firebase Auth
        auth = Firebase.auth

        emailLogin = findViewById(R.id.emailLogin)
        passwordLogin = findViewById(R.id.passwordLogin)
        buttonLogin = findViewById(R.id.buttonLogin)

        buttonLogin.setOnClickListener {
            val email = emailLogin.text.toString()
            val password = passwordLogin.text.toString()
            loginUser(email, password)
        }

    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(baseContext, "E-mail ou senha incorreto!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}