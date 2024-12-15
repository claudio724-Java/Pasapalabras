package com.example.pasapalabras

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log


class JugarActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech  // Objeto TextToSpeech
    private lateinit var playerImage: ImageView
    private lateinit var preguntaTextView: TextView
    private lateinit var responderButton: Button
    private lateinit var contadorButton: Button
    private lateinit var timerText: TextView
    private var isTTSReady = false
    private val botones = mutableListOf<Button>()
    private var preguntas = mutableListOf<Pregunta>()
    private var preguntaActual = 0
    private var correctas = 0
    private var pasapalabras = 0
    private val SPEECH_REQUEST_CODE = 1
    private val preguntasRespondidas = mutableSetOf<Int>()
    private var countDownTimer: CountDownTimer? = null
    private val tiempoTotal: Long = 240000
    private var tiempoRestante: Long = tiempoTotal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jugar)

        tts = TextToSpeech(this, this)

        playerImage = findViewById(R.id.playerImage)
        preguntaTextView = findViewById(R.id.Pregunta)
        responderButton = findViewById(R.id.responderButton)
        contadorButton = findViewById(R.id.contador)
        timerText = findViewById(R.id.timerText)

        loadProfileImage()

        for (i in 'A'..'Z') {
            val id = resources.getIdentifier("letter$i", "id", packageName)
            botones.add(findViewById(id))
        }

        val dificultad = intent.getStringExtra("dificultad") ?: "Medio"

        preguntas = obtenerPreguntasDesdeDB(this, dificultad)
        if (preguntas.isEmpty()) {
            Toast.makeText(this, "No hay preguntas para el nivel $dificultad", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        mostrarPregunta()

        iniciarCronometro()

        responderButton.setOnClickListener { startSpeechToText() }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val langResult = tts.setLanguage(Locale.getDefault())
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Idioma no soportado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "TTS inicializado correctamente", Toast.LENGTH_SHORT).show()
                isTTSReady = true
                mostrarPregunta()
            }
        } else {
            Toast.makeText(this, "Error al inicializar TTS", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarPregunta() {
        if (!isTTSReady) {
            return  // Esperar hasta que TTS esté listo
        }

        if (preguntas.isEmpty()) {
            preguntaTextView.text = "No hay preguntas disponibles."
            return
        }

        while (preguntasRespondidas.contains(preguntaActual)) {
            preguntaActual = (preguntaActual + 1) % preguntas.size
        }

        botones[preguntaActual].setBackgroundColor(Color.parseColor("#FFA500"))

        val pregunta = preguntas[preguntaActual].textoPregunta
        preguntaTextView.text = pregunta

        if (pregunta.isNotBlank()) {
            Log.d("TTS", "Pregunta a leer: $pregunta")
            tts.speak(pregunta, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Log.e("TTS", "La pregunta está vacía o nula.")
        }
    }

    private fun procesarRespuesta(respuesta: String) {
        val pregunta = preguntas[preguntaActual]

        // Mostrar la respuesta al usuario de manera hablada
        when {
            respuesta.equals("pasapalabra", ignoreCase = true) -> {
                pasapalabras++
                preguntasRespondidas.add(preguntaActual)
                preguntaActual = (preguntaActual + 1) % preguntas.size
                tts.speak("Pasapalabra", TextToSpeech.QUEUE_FLUSH, null, null)  // Leer 'Pasapalabra'
            }
            respuesta.equals(pregunta.respuesta, ignoreCase = true) -> {
                correctas++
                contadorButton.setText(correctas.toString())
                botones[preguntaActual].setBackgroundColor(Color.GREEN)
                preguntasRespondidas.add(preguntaActual)
                preguntaActual = (preguntaActual + 1) % preguntas.size
                tts.speak("¡Respuesta correcta!", TextToSpeech.QUEUE_FLUSH, null, null)  // Leer 'Respuesta correcta'
            }
            else -> {
                botones[preguntaActual].setBackgroundColor(Color.RED)
                preguntasRespondidas.add(preguntaActual)
                preguntaActual = (preguntaActual + 1) % preguntas.size
                tts.speak("Respuesta incorrecta, la correcta era: ${pregunta.respuesta}", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }

        if (preguntasRespondidas.size == preguntas.size) {
            Toast.makeText(this, "Juego terminado", Toast.LENGTH_SHORT).show()
            terminarJuego()
        } else {
            mostrarPregunta()  // Mostrar la siguiente pregunta
        }
    }

    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Responde o di 'pasapalabra'")
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
            val resultado = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            resultado?.let { procesarRespuesta(it) }
        }
    }

    private fun obtenerPreguntasDesdeDB(context: Context, dificultad: String): MutableList<Pregunta> {
        val preguntas = mutableListOf<Pregunta>()
        val dbHelper = baseDeDatos(context, "PasapalabraDB", null, 1)
        val db: SQLiteDatabase = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT letra, tipo, pregunta, respuesta FROM Preguntas WHERE nivel = ?",
            arrayOf(dificultad)
        )

        if (cursor.moveToFirst()) {
            do {
                // Suponiendo que tienes estos valores obtenidos desde la base de datos
                val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
                val letra = cursor.getString(cursor.getColumnIndexOrThrow("letra"))
                val preguntaTexto = cursor.getString(cursor.getColumnIndexOrThrow("pregunta"))
                val respuesta = cursor.getString(cursor.getColumnIndexOrThrow("respuesta"))
                val textoPregunta = "$tipo con $letra: $preguntaTexto"
                preguntas.add(Pregunta("$letra", tipo,  textoPregunta, respuesta))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return preguntas
    }

    private fun loadProfileImage() {
        val emailUsuario = intent.getStringExtra("email")
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
                    playerImage.setImageBitmap(bitmap)
                } else {
                    Toast.makeText(this, "No se encontró foto en la base de datos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No se encontró el perfil con ese email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun iniciarCronometro() {
        countDownTimer = object : CountDownTimer(tiempoTotal, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val segundosRestantes = (millisUntilFinished / 1000).toInt()
                timerText.text = "Tiempo: $segundosRestantes"
            }

            override fun onFinish() {
                Toast.makeText(applicationContext, "Se acabó el tiempo", Toast.LENGTH_SHORT).show()
                terminarJuego()
            }
        }
        countDownTimer?.start()
    }

    private fun terminarJuego() {
        countDownTimer?.cancel()
        // Guardar el resultado en la base de datos o hacer otra acción
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val fechaHora = sdf.format(Date())
        val db = baseDeDatos(this, "PasapalabraDB", null, 1).writableDatabase

        val valores = ContentValues().apply {
            put("email", intent.getStringExtra("email"))
            put("fecha", fechaHora)
            put("correctas", correctas)
            put("pasapalabras", pasapalabras)
        }

        db.insert("Resultados", null, valores)
        db.close()

        // Terminar la actividad y volver al menú principal
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.stop()  // Detener el TTS
        tts.shutdown()  // Liberar recursos
    }
}

