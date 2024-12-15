package com.example.pasapalabras

import android.service.notification.NotificationListenerService
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RankingAdapter(private val rankingList: MutableList<PlayerRanking>) :
RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ranking, parent, false)
        return RankingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val ranking = rankingList[position]
        holder.emailTextView.text = ranking.email
        holder.puntuacionTextView.text = "Puntuación: ${ranking.puntuacion}"
        holder.nivelTextView.text = "Nivel: ${ranking.nivel}"
        holder.fechaTextView.text = "Fecha: ${ranking.fecha}"
    }

    override fun getItemCount(): Int {
        return rankingList.size
    }

    inner class RankingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)
        val puntuacionTextView: TextView = itemView.findViewById(R.id.puntuacionTextView)
        val nivelTextView: TextView = itemView.findViewById(R.id.nivelTextView)
        val fechaTextView: TextView = itemView.findViewById(R.id.fechaTextView)
    }
}