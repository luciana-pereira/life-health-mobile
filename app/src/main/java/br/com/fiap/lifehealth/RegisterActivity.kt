package br.com.fiap.lifehealth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RegisterActivity : AppCompatActivity() {
    private lateinit var spinnerSpecialty: Spinner
    private lateinit var auth: FirebaseAuth
    private lateinit var emailRegister: EditText
    private lateinit var passwordRegister: EditText
    private lateinit var buttonRegister: Button
    private lateinit var linkLogin: TextView
    private lateinit var radioButtonPatient: RadioButton
    private lateinit var radioButtonDoctor: RadioButton
    private lateinit var spinnerMedicalSpecialty: RelativeLayout
    private lateinit var editTextCRM: EditText
    private lateinit var textMedicalSpecialty: TextView
    private lateinit var userName: EditText
    private lateinit var surname: EditText
    private lateinit var radioButtonFemale: RadioButton
    private lateinit var radioButtonMale: RadioButton
    private lateinit var checkBoxPrivacyPolicy: CheckBox
    private lateinit var dateOfBirth: EditText

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initializeComponents()
        setupSpinner()
        setupTextWatchers()
        setupListeners()
        setupFirebase()
        disableButton()
        validateFields()
    }

    private fun initializeComponents() {
        spinnerSpecialty = findViewById(R.id.spinnerSpecialty)
        emailRegister = findViewById(R.id.emailRegister)
        passwordRegister = findViewById(R.id.passwordRegister)
        buttonRegister = findViewById(R.id.buttonRegister)
        spinnerMedicalSpecialty = findViewById(R.id.spinnerMedicalSpecialty)
        textMedicalSpecialty = findViewById(R.id.textMedicalSpecialty)
        editTextCRM = findViewById(R.id.editTextCRM)
        radioButtonPatient = findViewById(R.id.radioButtonPatient)
        radioButtonDoctor = findViewById(R.id.radioButtonDoctor)
        linkLogin = findViewById(R.id.linkLogin)
        userName = findViewById(R.id.editTextName)
        surname = findViewById(R.id.editTextSobrenome)
        radioButtonFemale = findViewById(R.id.radioButtonFemale)
        radioButtonMale = findViewById(R.id.radioButtonMale)
        checkBoxPrivacyPolicy = findViewById(R.id.checkBoxPrivacyPolicy)
        dateOfBirth = findViewById(R.id.editTextDate)
    }

    private fun setupSpinner() {
        val specialties = listOf("Cardiologia Geral", "Clínica Médica Geral", "Gastroenterologia Geral", "Ginecologia Clínica", "Pediatria Geral", "Pneumologia Geral", "Dermatologia Geral", "Ortopedia Geral")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, specialties)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSpecialty.adapter = adapter

        spinnerSpecialty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                validateFields()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                Toast.makeText(baseContext, "Selecione sua especialidade médica, para concluir o cadastro.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupTextWatchers() {
        emailRegister.addTextChangedListener(watcher)
        passwordRegister.addTextChangedListener(watcher)
        editTextCRM.addTextChangedListener(watcher)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupListeners() {
        radioButtonPatient.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hideDoctorFields()
            }
            validateFields()
        }

        radioButtonDoctor.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showDoctorFields()
            }
            validateFields()
        }

        buttonRegister.setOnClickListener {
            val email = emailRegister.text.toString()
            val password = passwordRegister.text.toString()
            registerUser(email, password)
        }

        linkLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupFirebase() {
        auth = FirebaseAuth.getInstance()
    }

    private fun disableButton() {
        buttonRegister.isEnabled = false
    }

    private fun showDoctorFields() {
        spinnerMedicalSpecialty.visibility = View.VISIBLE
        editTextCRM.visibility = View.VISIBLE
        textMedicalSpecialty.visibility = View.VISIBLE
    }

    private fun hideDoctorFields() {
        spinnerMedicalSpecialty.visibility = View.GONE
        editTextCRM.visibility = View.GONE
        textMedicalSpecialty.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun validateFields() {
        val isPatientSelected = radioButtonPatient.isChecked
        val isDoctorSelected = radioButtonDoctor.isChecked
        val isEmailValid = emailRegister.text.isNotEmpty()
        val isPasswordValid = passwordRegister.text.isNotEmpty()
        val isPasswordValidContent = passwordRegister.text.toString()
        val isSpinnerItemSelected = if (isDoctorSelected) spinnerSpecialty.selectedItemPosition != AdapterView.INVALID_POSITION else true
        val isCRMValid = if (isDoctorSelected) editTextCRM.text.isNotEmpty() else true
        val isUserName = userName.text.isNotEmpty()
        val isSurname = surname.text.isNotEmpty()
        val isFemale = radioButtonFemale.isChecked
        val isMale = radioButtonMale.isChecked
        val currentDate = LocalDate.now()
        val isDateOfBirthText = dateOfBirth.text.toString()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val isDateOfBirthDate = LocalDate.parse(isDateOfBirthText, formatter)
        val isPrivacyPolicy = checkBoxPrivacyPolicy.isChecked

        if (isPatientSelected) {
            if (
                isUserName &&
                isSurname &&
                isEmailValid &&
                (isPasswordValid && isPasswordValidContent.length >= 6) &&
                (isFemale || isMale) &&
                (isDateOfBirthDate.isEqual(currentDate)) &&
                isPrivacyPolicy
            ) {
                buttonRegister.isEnabled = true
                buttonRegister.backgroundTintList = resources.getColorStateList(R.color.colorEnabledButton)
            } else {
                buttonRegister.isEnabled = false
                buttonRegister.backgroundTintList = resources.getColorStateList(R.color.colorDisabledButton)
            }
        } else {
            if (
                isUserName &&
                isSurname &&
                isEmailValid &&
                (isPasswordValid && isPasswordValidContent.length >= 6) &&
                isSpinnerItemSelected &&
                isCRMValid &&
                (isFemale || isMale) &&
                (isDateOfBirthDate.isEqual(currentDate)) &&
                isPrivacyPolicy
            ) {
                buttonRegister.isEnabled = true
                buttonRegister.backgroundTintList = resources.getColorStateList(R.color.colorEnabledButton)
            } else {
                buttonRegister.isEnabled = false
                buttonRegister.backgroundTintList = resources.getColorStateList(R.color.colorDisabledButton)
            }
        }
    }

    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        @RequiresApi(Build.VERSION_CODES.O)
        override fun afterTextChanged(editable: Editable) {
            validateFields()
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(baseContext, "Algo deu errado na criação da conta, tente novamente.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}
