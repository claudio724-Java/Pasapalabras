package com.example.pasapalabras

import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class rankingActivity : AppCompatActivity() {
    private lateinit var rankingRecyclerView: RecyclerView
    private lateinit var rankingAdapter: RankingAdapter
    private val rankingList = mutableListOf<PlayerRanking>()
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f // Factor de escala inicial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        rankingRecyclerView = findViewById(R.id.rankingRecyclerView)
        rankingRecyclerView.layoutManager = LinearLayoutManager(this)

        cargarRankingDesdeBD()

        rankingAdapter = RankingAdapter(rankingList)
        rankingRecyclerView.adapter = rankingAdapter

        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())

    }

    private fun cargarRankingDesdeBD() {
        val dbHelper = baseDeDatos(this, "PasapalabraDB", null, 1)
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery("SELECT email, puntuacion, nivel, fecha FROM Ranking ORDER BY puntuacion DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                val puntuacion = cursor.getInt(cursor.getColumnIndexOrThrow("puntuacion"))
                val nivel = cursor.getString(cursor.getColumnIndexOrThrow("nivel"))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))

                rankingList.add(PlayerRanking(email, puntuacion, nivel, fecha))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Pasa el evento al detector de gestos para detectar el zoom
        scaleGestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            // Obtener el factor de escala y aplicarlo al RecyclerView
            scaleFactor *= detector.scaleFactor
            scaleFactor = scaleFactor.coerceIn(0.5f, 3.0f) // Limitar el zoom entre 0.5x y 3x

            // Aplicar el escalado al RecyclerView
            rankingRecyclerView.scaleX = scaleFactor
            rankingRecyclerView.scaleY = scaleFactor
            return true
        }
    }

}
