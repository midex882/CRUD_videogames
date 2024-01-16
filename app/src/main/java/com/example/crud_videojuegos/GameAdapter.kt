package com.example.crud_videojuegos

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.util.Util
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class GameAdapter(private val game_list: MutableList<Game>): RecyclerView.Adapter<GameAdapter.GameViewHolder>(), Filterable {
    private lateinit var contexto: Context
    private var lista_filtrada = game_list


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameAdapter.GameViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.juego,parent,false)
        contexto = parent.context
        return GameViewHolder(vista_item)
    }

    override fun onBindViewHolder(holder: GameAdapter.GameViewHolder, position: Int) {
        val item_actual = lista_filtrada[position]
        holder.title.text = item_actual.title
        holder.platform.text = item_actual.platform
        holder.rating.rating = item_actual.rating!!.toInt().toFloat()
        holder.date.text = Utilities.getDate(item_actual.date!!.toLong()).toString()

        val URL:String? = when(item_actual.cover){
            ""-> null
            else -> item_actual.cover
        }

        Glide.with(contexto)
            .load(URL)
            .apply(Utilities.opcionesGlide(contexto))
            .transition(Utilities.transicion)
            .into(holder.cover)

        holder.edit.setOnClickListener {
            val activity = Intent(contexto,EditGame::class.java)
            activity.putExtra("game", item_actual)
            contexto.startActivity(activity)
        }

        holder.delete.setOnClickListener {
            val  db_ref = FirebaseDatabase.getInstance().getReference()
            val sto_ref = FirebaseStorage.getInstance().getReference()
            lista_filtrada.remove(item_actual)
            sto_ref.child("FG").child("cover").child(item_actual.id!!).delete()
            db_ref.child("FG").child("game")
                .child(item_actual.id!!).removeValue()

            Toast.makeText(contexto,"Juego DESTRUIDO", Toast.LENGTH_SHORT).show()
        }




    }

    override fun getItemCount(): Int = lista_filtrada.size

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val cover: ImageView = itemView.findViewById(R.id.picture)
        val title: TextView = itemView.findViewById(R.id.titleLayout)
        val platform: TextView = itemView.findViewById(R.id.platformLayout)
        val date: TextView = itemView.findViewById(R.id.date)
        val rating: RatingBar = itemView.findViewById(R.id.ratingLayout)
        val edit: ImageView = itemView.findViewById(R.id.edit)
        val delete: ImageView = itemView.findViewById(R.id.delete)
    }

    override fun getFilter(): Filter {
        return  object : Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val busqueda = p0.toString().lowercase()
                if (busqueda.isEmpty()){
                    lista_filtrada = game_list
                }else {
                    lista_filtrada = (game_list.filter {
                        it.title.toString().lowercase().contains(busqueda)
                    }) as MutableList<Game>
                }

                val filterResults = FilterResults()
                filterResults.values = lista_filtrada
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }

        }
    }


}