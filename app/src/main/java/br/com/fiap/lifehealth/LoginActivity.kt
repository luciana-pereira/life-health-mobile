package br.com.fiap.lifehealth

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private lateinit var emailLogin: EditText
    private lateinit var passwordLogin: EditText
    private lateinit var buttonLogin: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var linkRegister: TextView
    private lateinit var presentationText: TextView

    private lateinit var buttonDecrease: Button
    private lateinit var buttonIncrease: Button
    private var textSize = 20f
    private val textSizeStep = 2f


    @SuppressLint("MissingInflatedId")
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

        linkRegister = findViewById(R.id.linkRegister)

        linkRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        presentationText = findViewById(R.id.presentationText)
        buttonIncrease = findViewById(R.id.buttonIncreaseTextSize)
        buttonDecrease = findViewById(R.id.buttonDecreaseTextSize)

        buttonIncrease.setOnClickListener {
            textSize += textSizeStep
            adjustTextSize()
        }

        buttonDecrease.setOnClickListener {
            textSize -= textSizeStep
            adjustTextSize()
        }

    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                task ->
                if (task.isSuccessful) {
                    //val user = auth.currentUser
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(baseContext, "E-mail ou senha incorreto!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun adjustTextSize() {
        if (textSize in 20f..28f) {
            presentationText.textSize = textSize
            emailLogin.textSize = textSize
            passwordLogin.textSize = textSize
            buttonLogin.textSize = textSize
            linkRegister.textSize = textSize
            // buttonIncrease.textSize = textSize
            // buttonDecrease.textSize = textSize
        } else {
            Toast.makeText(baseContext, "Opa! Este e o limite maximo para aumentar/diminuir  o tamanho da letra para visualização.", Toast.LENGTH_SHORT).show()
        }

    }

}