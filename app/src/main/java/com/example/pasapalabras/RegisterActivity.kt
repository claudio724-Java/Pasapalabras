package com.example.pasapalabras

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream

class RegisterActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var nombreUsuarioEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var selectImageButton: Button
    private lateinit var profileImageView: ImageView
    private lateinit var registerButton: Button

    private var selectedImageUri: Uri? = null  // URI de la imagen seleccionada

    companion object {
        private const val IMAGE_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        emailEditText = findViewById(R.id.email)
        nombreUsuarioEditText = findViewById(R.id.nombreUsuario)
        passwordEditText = findViewById(R.id.password)
        confirmPasswordEditText = findViewById(R.id.confirmPassword)
        selectImageButton = findViewById(R.id.select_image_button)
        profileImageView = findViewById(R.id.profile_image)
        registerButton = findViewById(R.id.register_button)

        selectImageButton.setOnClickListener {
            openImagePicker()
        }

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val nombreUsuario = nombreUsuarioEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (email.isEmpty() || nombreUsuario.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese todos los campos", Toast.LENGTH_SHORT).show()
            } else if (!isValidEmail(email)) {
                Toast.makeText(this, "Correo electrónico no válido", Toast.LENGTH_SHORT).show()
            } else if (!isValidPassword(password)) {
                Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres, una mayúscula y un carácter especial", Toast.LENGTH_LONG).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(email, nombreUsuario, password)
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == IMAGE_REQUEST_CODE) {
            selectedImageUri = data?.data
            selectedImageUri?.let { uri ->
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                profileImageView.setImageBitmap(bitmap)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordRegex = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>-_*]).{8,}$"
        return password.matches(passwordRegex.toRegex())
    }

    private fun registerUser(email: String, nombreUsuario: String, password: String) {
        val db = baseDeDatos(this, "PasapalabrasDB", null, 1).writableDatabase

        val values = ContentValues().apply {
            put("email", email)
            put("nombreUsuario", nombreUsuario)
            put("contraseña", password)

            selectedImageUri?.let { uri ->
                val inputStream = contentResolver.openInputStream(uri)
                val imageBytes = inputStream?.readBytes()
                put("foto", imageBytes) // Guardar la foto como un BLOB
            }
        }

        val newRowId = db.insert("Usuarios", null, values)
        if (newRowId != -1L) {
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
            // Redirigir a la pantalla de inicio de sesión o donde sea necesario
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
        }
    }
}
