package com.example.pasapalabras

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainMenuActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var jugarButton: Button
    private lateinit var clasificacionButton: Button
    private lateinit var editarButton: Button
    private lateinit var perfilUsuarioButton: Button
    private lateinit var salirButton: Button
    private lateinit var iconButton: Button
    private lateinit var tts: TextToSpeech

    companion object {
        private const val SPEECH_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        jugarButton = findViewById(R.id.playButton)
        clasificacionButton = findViewById(R.id.rankingButton)
        editarButton = findViewById(R.id.editButton)
        perfilUsuarioButton = findViewById(R.id.perfilButton)
        salirButton = findViewById(R.id.salirButton)
        iconButton = findViewById(R.id.iconButton)

        var emailUsuario = intent.getStringExtra("email") ?: "default@example.com"

        tts = TextToSpeech(this, this)

        jugarButton.setOnClickListener {
            showDifficultyDialog()
        }

        clasificacionButton.setOnClickListener {
            val rankingIntent = Intent(this, rankingActivity::class.java)
            rankingIntent.putExtra("email", emailUsuario)
            startActivity(rankingIntent)
        }

        editarButton.setOnClickListener {
            val editarIntent = Intent(this, EditarActivity::class.java)
            editarIntent.putExtra("email", emailUsuario)
            startActivity(editarIntent)
        }

        perfilUsuarioButton.setOnClickListener {
            val intent = Intent(this, PerfilUsuarioActivity::class.java)
            intent.putExtra("email", emailUsuario)
            startActivity(intent)
        }

        salirButton.setOnClickListener {
            finishAffinity()
        }

        iconButton.setOnClickListener {
            readMenuOptions()
        }
    }

    private fun readMenuOptions() {
        val options = "Selecciona Jugar, Perfil de usuario, Ranking o Personalizar juego."

        tts.speak(options, TextToSpeech.QUEUE_FLUSH, null, "UTTERANCE_ID")

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
            }

            override fun onDone(utteranceId: String?) {
                if (utteranceId == "UTTERANCE_ID") {
                    runOnUiThread { startSpeechToText() }
                }
            }

            override fun onError(utteranceId: String?) {
                runOnUiThread {
                    Toast.makeText(this@MainMenuActivity, "Error al leer el menú.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }



    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Diga una opción: Jugar, Perfil, Ranking, Editar o Salir")
        }
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(this, "El reconocimiento de voz no está disponible", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val command = results?.get(0)?.lowercase(Locale.getDefault())

            when {
                command?.contains("jugar") == true -> jugarButton.performClick()
                command?.contains("perfil") == true -> perfilUsuarioButton.performClick()
                command?.contains("ranking") == true -> clasificacionButton.performClick()
                command?.contains("personalizar Juego") == true -> editarButton.performClick()

                else -> Toast.makeText(this, "No se reconoció la opción", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.getDefault()
        } else {
            Toast.makeText(this, "Error al inicializar Text-to-Speech", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }

    private fun showDifficultyDialog() {
        var emailUsuario = intent.getStringExtra("email") ?: "default@example.com"
        val difficulties = arrayOf("Fácil", "Medio", "Difícil", "Personalizado")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona la dificultad")
        builder.setItems(difficulties) { _, which ->
            val selectedDifficulty = difficulties[which]
            val intent = Intent(this, JugarActivity::class.java)
            intent.putExtra("dificultad", selectedDifficulty)
            intent.putExtra("email", emailUsuario)
            startActivity(intent)
        }
        builder.show()
    }
}

