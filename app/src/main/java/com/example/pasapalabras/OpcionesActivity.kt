package com.example.pasapalabras

import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream

class OpcionesActivity : AppCompatActivity() {

    private lateinit var nombreUsuarioEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var selectImageButton: Button
    private lateinit var profileImageView: ImageView
    private lateinit var saveButton: Button

    private var selectedImageUri: Uri? = null  // URI de la imagen seleccionada
    private lateinit var emailUsuario: String   // Almacenar el email del usuario

    companion object {
        const val IMAGE_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opciones)

        // Obtener el email desde el Intent
        emailUsuario = intent.getStringExtra("email") ?: "default@example.com" // Valor por defecto si no se pasa el email

        // Referencias a los elementos de la UI
        nombreUsuarioEditText = findViewById(R.id.nombreUsuario)
        passwordEditText = findViewById(R.id.password)
        confirmPasswordEditText = findViewById(R.id.confirmPassword)
        selectImageButton = findViewById(R.id.select_image_button)
        profileImageView = findViewById(R.id.profile_image)
        saveButton = findViewById(R.id.save_button)

        // Lógica para seleccionar una imagen de la galería
        selectImageButton.setOnClickListener {
            openImagePicker()
        }

        // Lógica para guardar los cambios en el perfil
        saveButton.setOnClickListener {
            val nombreUsuario = nombreUsuarioEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (nombreUsuario.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese todos los campos", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {
                updateUserProfile(nombreUsuario, password)
            }
        }

        // Cargar la imagen de perfil si existe
        loadProfileImage()
    }

    // Función para abrir el selector de imágenes
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    // Manejar el resultado de la selección de imagen
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

    private fun updateUserProfile(nombreUsuario: String, password: String) {
        val db = baseDeDatos(this, "PasapalabrasDB", null, 1).writableDatabase

        val values = ContentValues().apply {
            put("contraseña", password)

            selectedImageUri?.let { uri ->
                val inputStream = contentResolver.openInputStream(uri)
                val imageBytes = inputStream?.readBytes()
                put("foto", imageBytes) // Guardar la foto como un BLOB
            }
        }

        val rowsUpdated = db.update("Usuarios", values, "email = ?", arrayOf(emailUsuario))

        if (rowsUpdated > 0) {
            Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para cargar la imagen del perfil
    private fun loadProfileImage() {
        val db = baseDeDatos(this, "PasapalabrasDB", null, 1).readableDatabase

        val cursor = db.query(
            "Usuarios",
            arrayOf("foto"),
            "email = ?",
            arrayOf(emailUsuario),
            null, null, null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val fotoColumnIndex = cursor.getColumnIndex("foto")
            if (fotoColumnIndex >= 0) {
                val imageBytes = cursor.getBlob(fotoColumnIndex)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                profileImageView.setImageBitmap(bitmap)
            }
        } else {
            Toast.makeText(this, "No se encontró el perfil", Toast.LENGTH_SHORT).show()
        }

        cursor?.close()
    }
}
