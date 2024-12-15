package com.example.pasapalabras

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pasapalabras.OpcionesActivity.Companion.IMAGE_REQUEST_CODE
import java.io.InputStream

class PerfilUsuarioActivity : AppCompatActivity() {


    private lateinit var perfilImage: ImageView
    private lateinit var emailUsuario: TextView
    private lateinit var mejorResultado: TextView
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var selectImageButton: Button
    private lateinit var profileImageView: ImageView
    private lateinit var saveButton: Button
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_usuario)

        perfilImage = findViewById(R.id.perfilImage)
        emailUsuario = findViewById(R.id.emailUsuario)
        mejorResultado = findViewById(R.id.mejorResultado)
        passwordEditText = findViewById(R.id.password)
        confirmPasswordEditText = findViewById(R.id.confirmPassword)
        selectImageButton = findViewById(R.id.select_image_button)
        profileImageView = findViewById(R.id.profile_image)
        saveButton = findViewById(R.id.save_button)



        val email = intent.getStringExtra("email")
        if (email.isNullOrEmpty()) {
            Toast.makeText(this, "No se proporcionó un email", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        emailUsuario.text=email
        cargarDatosUsuario(email)

        saveButton.setOnClickListener{
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if ( password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese todos los campos", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {
                updateUserProfile( password)
            }
        }

        selectImageButton.setOnClickListener {
            openImagePicker()
        }

    }


    private fun cargarDatosUsuario(email: String) {
        val dbHelper = baseDeDatos(this, "PasapalabraDB", null, 1)
        val db = dbHelper.readableDatabase

        try {

            obtenerFotoUsuario(db, email)


            obtenerMejorResultadoRanking(db, email)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
        } finally {
            db.close()
        }
    }

    private fun obtenerFotoUsuario(db: SQLiteDatabase, email: String) {
        val emailUsuario = email
        if (emailUsuario.isNullOrEmpty()) {
            Toast.makeText(this, "Email no proporcionado", Toast.LENGTH_SHORT).show()
            return
        }

        val db = baseDeDatos(this, "PasapalabrasDB", null, 1).readableDatabase
        val cursor = db.query(
            "Usuarios",
            arrayOf("foto"),
            "email = ?",
            arrayOf(emailUsuario),
            null, null, null
        )

        cursor.use {
            if (it != null && it.moveToFirst()) {
                val fotoColumnIndex = it.getColumnIndex("foto")
                if (fotoColumnIndex >= 0) {
                    val imageBytes = it.getBlob(fotoColumnIndex)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    perfilImage.setImageBitmap(bitmap)
                } else {
                    Toast.makeText(this, "No se encontró foto en la base de datos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No se encontró el perfil con ese email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun obtenerMejorResultadoRanking(db: SQLiteDatabase, email: String) {
        val rankingCursor = db.rawQuery(
            "SELECT MAX(puntuacion) AS mejorPuntuacion FROM Ranking WHERE email = ?",
            arrayOf(email)
        )

        rankingCursor.use {
            if (it.moveToFirst()) {
                val mejorResultadoDb = it.getInt(it.getColumnIndexOrThrow("mejorPuntuacion"))
                mejorResultado.text = "Mejor resultado en el ranking: $mejorResultadoDb"
            } else {
                mejorResultado.text = "Mejor resultado en el ranking: Sin datos"
            }
        }
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

    private fun updateUserProfile( password: String) {
        val db = baseDeDatos(this, "PasapalabrasDB", null, 1).writableDatabase

        val values = ContentValues().apply {
            put("contraseña", password)

            selectedImageUri?.let { uri ->
                val inputStream = contentResolver.openInputStream(uri)
                val imageBytes = inputStream?.readBytes()
                put("foto", imageBytes) // Guardar la foto como un BLOB
            }
        }

        val rowsUpdated = db.update("Usuarios", values, "email = ?", arrayOf(emailUsuario.text.toString()))

        if (rowsUpdated > 0) {
            Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
        }
    }
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }


}


