package com.example.crud_videojuegos

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar

class Utilities {
    companion object{


        fun gameExists(Games : List<Game>, nombre:String):Boolean{
            return Games.any{ it.title!!.lowercase()==nombre.lowercase()}
        }

        fun howManyGames(Games : List<Game>, nombre:String):Int{
            return Games.filter { it.title == nombre }.size
        }


        fun getGames(db_ref: DatabaseReference):MutableList<Game>{
            var lista = mutableListOf<Game>()

            db_ref.child("FG")
                .child("game")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach{hijo :DataSnapshot ->
                            val pojo_Game = hijo.getValue(Game::class.java)
                            lista.add(pojo_Game!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })

            return lista
        }

        fun writeGame(db_ref: DatabaseReference, id: String, title: String, platform: String, rating: Int, creation: Int, url_firebase: String)=
            db_ref.child("FG").child("game").child(id).setValue(Game(
                id,
                title,
                rating,
                platform,
                creation,
                url_firebase
            ))

        suspend fun saveCover(sto_ref: StorageReference, id:String, imagen: Uri):String{
            lateinit var firebase_cover_url: Uri

            firebase_cover_url = sto_ref.child("FG").child("cover").child(id)
                .putFile(imagen).await().storage.downloadUrl.await()

            return firebase_cover_url.toString()
        }

        fun toastCoroutine(activity: AppCompatActivity, context: Context, text:String){
            activity.runOnUiThread{
                Toast.makeText(
                    context,
                    text,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        fun animacion_carga(contexto: Context): CircularProgressDrawable {
            val animacion = CircularProgressDrawable(contexto)
            animacion.strokeWidth = 5f
            animacion.centerRadius = 30f
            animacion.start()
            return animacion
        }


        val transicion = DrawableTransitionOptions.withCrossFade(500)

        fun opcionesGlide(context: Context): RequestOptions {
            val options = RequestOptions()
                .placeholder(animacion_carga(context))
                .fallback(R.drawable.logo)
                .error(R.drawable.e404)
            return options
        }


        fun getDate(milliSeconds: Long): String? {
            val formatter = SimpleDateFormat("dd-mm-yyyy")
            val calendar: Calendar = Calendar.getInstance()
            calendar.setTimeInMillis(milliSeconds.toInt().toLong())
            return formatter.format(calendar.getTime())
        }


    }
}