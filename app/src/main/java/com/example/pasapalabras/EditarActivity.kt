package com.example.pasapalabras

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EditarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar)

        val scrollViewContainer: LinearLayout = findViewById(R.id.dynamicContainer)
        val saveButton: Button = findViewById(R.id.saveButton)

        for (letra in 'A'..'Z') {
            val letterBlock = View.inflate(this, R.layout.letter_block, null)

            val letterTextView: TextView = letterBlock.findViewById(R.id.letterTextView)
            val spinner: Spinner = letterBlock.findViewById(R.id.typeSpinner)
            val preguntaEditText: EditText = letterBlock.findViewById(R.id.questionEditText)
            val respuestaEditText: EditText = letterBlock.findViewById(R.id.answerEditText)

            letterTextView.text = letra.toString()

            val spinnerAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                listOf("Empieza", "Contiene", "Termina")
            )
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = spinnerAdapter

            scrollViewContainer.addView(letterBlock)
        }

        saveButton.setOnClickListener {
            guardarPreguntas(scrollViewContainer)
        }
    }

    private fun guardarPreguntas(container: LinearLayout) {
        val preguntas = mutableListOf<Pregunta>()

        for (i in 0 until container.childCount) {
            val letterBlock = container.getChildAt(i)
            val letterTextView: TextView = letterBlock.findViewById(R.id.letterTextView)
            val spinner: Spinner = letterBlock.findViewById(R.id.typeSpinner)
            val preguntaEditText: EditText = letterBlock.findViewById(R.id.questionEditText)
            val respuestaEditText: EditText = letterBlock.findViewById(R.id.answerEditText)

            val letra = letterTextView.text.toString()
            val tipo = spinner.selectedItem.toString()
            val preguntaTexto = preguntaEditText.text.toString()
            val respuestaTexto = respuestaEditText.text.toString()

            if (preguntaTexto.isNotEmpty() && respuestaTexto.isNotEmpty()) {
                // Crear correctamente la instancia de Pregunta con los valores adecuados
                preguntas.add(Pregunta(letra, tipo, preguntaTexto, respuestaTexto))
            }
        }

        guardarEnBD(preguntas)
        Toast.makeText(this, "Preguntas guardadas correctamente.", Toast.LENGTH_SHORT).show()
    }

    private fun guardarEnBD(preguntas: List<Pregunta>) {
        val dbHelper = baseDeDatos(this, "PasapalabraDB", null, 1)
        val db = dbHelper.writableDatabase

        db.beginTransaction()
        try {
            preguntas.forEach { pregunta ->
                val letra = pregunta.textoPregunta.substringAfter("con ").substringBefore(":").trim()
                db.delete(
                    "Preguntas",
                    "letra = ? AND nivel = ?",
                    arrayOf(letra, "Personalizado")
                )
            }

            preguntas.forEach { pregunta ->
                val values = ContentValues().apply {
                    put("letra", pregunta.textoPregunta.substringAfter("con ").substringBefore(":"))
                    put("tipo", pregunta.textoPregunta.substringBefore(" con "))
                    put("pregunta", pregunta.textoPregunta.substringAfter(": ").trim())
                    put("respuesta", pregunta.respuesta)
                    put("nivel", "Personalizado") // Nivel fijo
                }
                db.insert("Preguntas", null, values)
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

}