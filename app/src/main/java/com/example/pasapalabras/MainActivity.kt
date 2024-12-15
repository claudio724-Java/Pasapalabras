package com.example.pasapalabras

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.view.GestureDetector
import android.view.MotionEvent

class MainActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView
    private lateinit var gestureDetector: GestureDetector


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.login_button)
        registerLink = findViewById(R.id.register_link)

        // Configurar GestureDetector para el swipe
        gestureDetector = GestureDetector(this, GestureListener())

        // Establecer OnTouchListener para detectar gestos en el botón
        loginButton.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }

        registerLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        val db = baseDeDatos(this, "PasapalabrasDB", null, 1).readableDatabase

        val cursor = db.rawQuery("SELECT * FROM Usuarios WHERE email = ? AND contraseña = ?", arrayOf(email, password))

        if (cursor.moveToFirst()) {
            Toast.makeText(this, "Bienvenido, $email", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainMenuActivity::class.java)
            intent.putExtra("email" ,emailEditText.text.toString())
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Correo electrónico o contraseña incorrectos", Toast.LENGTH_SHORT).show()
        }

        cursor.close()
        db.close()
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val SWIPE_THRESHOLD = 100
            val SWIPE_VELOCITY_THRESHOLD = 100

            if (e1 != null && e2 != null) {
                val diffX = e2.x - e1.x
                val diffY = e2.y - e1.y

                if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Swipe hacia la derecha
                        onSwipeRight()
                    } else {
                        // Swipe hacia la izquierda
                        onSwipeLeft()
                    }
                }
            }
            return true
        }


        private fun onSwipeRight() {
            Toast.makeText(this@MainActivity, "Deslizar hacia la derecha", Toast.LENGTH_SHORT).show()
            loginButton.setBackgroundColor(resources.getColor(android.R.color.holo_green_light))
        }

        private fun onSwipeLeft() {
            Toast.makeText(this@MainActivity, "Deslizar hacia la izquierda", Toast.LENGTH_SHORT).show()
            loginButton.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
        }
    }
}