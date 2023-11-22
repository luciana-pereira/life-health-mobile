package br.com.fiap.lifehealth

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.util.UUID

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var spinnerSpecialty: Spinner
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
    private lateinit var storageReference: StorageReference
    private lateinit var imageView: ImageView
    private lateinit var imageUrl: String
    private val PICK_IMAGE_REQUEST = 1
    private var filePath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        storageReference = FirebaseStorage.getInstance().reference
        imageView = findViewById(R.id.imageView)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val buttonChooseImage: Button = findViewById(R.id.buttonChooseImage)
        buttonChooseImage.setOnClickListener {
            showFileChooser()
        }

        initializeComponents()
        setupSpinner()
        setupTextWatchers()
        setupListeners()
        disableButton()
        validateFields()
    }

    private fun showFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Escolha uma imagem"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            var filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imageView.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage() {
        if (filePath != null) {
            val ref: StorageReference = storageReference.child("images/" + UUID.randomUUID().toString())
            val stream = ByteArrayOutputStream()
            val bitmap = (imageView.drawable).toBitmap()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray = stream.toByteArray()

            ref.putBytes(byteArray)
                .addOnSuccessListener { task ->
                    ref.downloadUrl
                        .addOnSuccessListener { downloadUri ->
                            val downloadUrl = downloadUri.toString()
                            saveDataToFirestore(downloadUrl)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Erro ao obter URL de download: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            e.printStackTrace()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Erro no upload da imagem: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    e.printStackTrace()
                }
        }
    }

    private fun saveDataToFirestore(imageUrl: String) {
        val user = if (radioButtonPatient.isChecked) {
            hashMapOf(
                "type" to "Patient",
                "imageUrl" to imageUrl,
                "name" to userName.text.toString(),
                "surname" to surname.text.toString(),
                "email" to emailRegister.text.toString(),
                "password" to passwordRegister.text.toString(),
                "gender" to if (radioButtonFemale.isChecked) "Feminino" else "Masculino",
                "dateOfBirth" to dateOfBirth.text.toString()
            )
        } else {
            hashMapOf(
                "type" to "Doctor",
                "imageUrl" to imageUrl,
                "name" to userName.text.toString(),
                "surname" to surname.text.toString(),
                "email" to emailRegister.text.toString(),
                "password" to passwordRegister.text.toString(),
                "MedicalSpecialty" to textMedicalSpecialty.text.toString(),
                "CRM" to editTextCRM.text.toString(),
                "gender" to if (radioButtonFemale.isChecked) "Feminino" else "Masculino",
                "dateOfBirth" to dateOfBirth.text.toString()
            )
        }

        val collection = if (radioButtonPatient.isChecked) "patient" else "doctor"
        val userId = auth.currentUser?.uid

        if (userId != null) {
            firestore.collection(collection).document(userId)
                .set(user)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot added")
                    Toast.makeText(
                        this,
                        "Cadastro realizado com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    Toast.makeText(
                        this,
                        "Erro ao realizar cadastro: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
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
        val specialties = listOf(
            "Cardiologia Geral",
            "Clínica Médica Geral",
            "Gastroenterologia Geral",
            "Ginecologia Clínica",
            "Pediatria Geral",
            "Pneumologia Geral",
            "Dermatologia Geral",
            "Ortopedia Geral"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, specialties)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSpecialty.adapter = adapter

        spinnerSpecialty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                validateFields()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                Toast.makeText(
                    baseContext, "Selecione sua especialidade médica, para concluir o cadastro.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupTextWatchers() {
        emailRegister.addTextChangedListener(watcher)
        passwordRegister.addTextChangedListener(watcher)
        editTextCRM.addTextChangedListener(watcher)
    }

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
            uploadImage()
            registerUser(email, password)
        }

        linkLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
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

    private fun validateFields() {
        val isPatientSelected = radioButtonPatient.isChecked
        val isDoctorSelected = radioButtonDoctor.isChecked
        val isEmailValid = emailRegister.text.isNotEmpty()
        val isPasswordValid = passwordRegister.text.isNotEmpty()
        val isPasswordValidContent = passwordRegister.text.toString()
        val isSpinnerItemSelected =
            if (isDoctorSelected) spinnerSpecialty.selectedItemPosition != AdapterView.INVALID_POSITION else true
        val isCRMValid = if (isDoctorSelected) editTextCRM.text.isNotEmpty() else true
        val isUserName = userName.text.isNotEmpty()
        val isSurname = surname.text.isNotEmpty()
        val isFemale = radioButtonFemale.isChecked
        val isMale = radioButtonMale.isChecked
        val isDateOfBirthDate = dateOfBirth.text.isNotEmpty()
        val isPrivacyPolicy = checkBoxPrivacyPolicy.isChecked

        if (!isDoctorSelected) {
            buttonRegister.isEnabled = isUserName &&
                    isSurname &&
                    isEmailValid &&
                    (isPasswordValid && isPasswordValidContent.length >= 6) &&
                    (isFemale || isMale) &&
                    isDateOfBirthDate &&
                    isPrivacyPolicy
        } else {
            buttonRegister.isEnabled = isUserName &&
                    isSurname &&
                    isEmailValid &&
                    (isPasswordValid && isPasswordValidContent.length >= 6) &&
                    isSpinnerItemSelected &&
                    isCRMValid &&
                    (isFemale || isMale) &&
                    isDateOfBirthDate &&
                    isPrivacyPolicy
        }
    }

    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun afterTextChanged(editable: Editable) {
            validateFields()
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val user = if (radioButtonPatient.isChecked) {
                            hashMapOf(
                                "type" to "Patient",
                                "imageUrl" to "",
                                "name" to userName.text.toString(),
                                "surname" to surname.text.toString(),
                                "email" to emailRegister.text.toString(),
                                "password" to passwordRegister.text.toString(),
                                "gender" to if (radioButtonFemale.isChecked) "Feminino" else "Masculino",
                                "dateOfBirth" to dateOfBirth.text.toString()
                            )
                        } else {
                            hashMapOf(
                                "type" to "Doctor",
                                "imageUrl" to "",
                                "name" to userName.text.toString(),
                                "surname" to surname.text.toString(),
                                "email" to emailRegister.text.toString(),
                                "password" to passwordRegister.text.toString(),
                                "MedicalSpecialty" to textMedicalSpecialty.text.toString(),
                                "CRM" to editTextCRM.text.toString(),
                                "gender" to if (radioButtonFemale.isChecked) "Feminino" else "Masculino",
                                "dateOfBirth" to dateOfBirth.text.toString()
                            )
                        }

                        val collection = if (radioButtonPatient.isChecked) "patient" else "doctor"

                        firestore.collection(collection).document(userId)
                            .set(user)
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot added")
                                Toast.makeText(
                                    this,
                                    "Cadastro realizado com sucesso!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                                Toast.makeText(
                                    this,
                                    "Erro ao realizar cadastro: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Log.e("SignUpActivity", "Erro ao criar usuário: ${task.exception?.message}")
                    }
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        baseContext, "Erro ao criar usuário: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}