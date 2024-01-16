package com.example.crud_videojuegos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class CreateGame : AppCompatActivity(), CoroutineScope {

    val this_activity = this
    var job = Job()

    override val coroutineContext: CoroutineContext
        get() {
            return Dispatchers.IO + job
        }

    var title = ""
    var platform = ""
    var rating = 0
    var url_cover : Uri? = null
    var creation : Int? = null
    lateinit var db_ref: DatabaseReference
    lateinit var st_ref: StorageReference
    lateinit var games_list : List<Game>
    lateinit var cover_ImageView : ImageView
    lateinit var titleEdit : TextInputEditText
    lateinit var platformEdit : TextInputEditText
    lateinit var ratingLayout : RatingBar
    lateinit var createButton : ImageView

    private val gallery_access = registerForActivityResult(ActivityResultContracts.GetContent())
    {uri: Uri? ->
        if(uri!=null){
            url_cover = uri
            cover_ImageView.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_game)

        db_ref = FirebaseDatabase.getInstance().getReference()
        st_ref = FirebaseStorage.getInstance().getReference()

        titleEdit = findViewById(R.id.nameEdit)
        platformEdit = findViewById(R.id.platformEdit)
        ratingLayout = findViewById(R.id.ratingLayout)
        createButton = findViewById(R.id.create)

        games_list = Utilities.getGames(db_ref)

        cover_ImageView = findViewById(R.id.cover)
        cover_ImageView.setOnClickListener {
            gallery_access.launch("image/*")
        }

        createButton.setOnClickListener {

            Log.v("rating", ratingLayout.rating.toString())

            if (titleEdit.getText().toString().trim().isEmpty() ||
                platformEdit.getText().toString().isEmpty() ||
                ratingLayout.rating.toString().equals("0.0")
            ) {
                Toast.makeText(
                    applicationContext, "Todos los campos son necesarios", Toast.LENGTH_SHORT
                ).show()

            } else if (url_cover == null) {
                Toast.makeText(
                    applicationContext, "Por favor, selecciona una carátula", Toast.LENGTH_SHORT
                ).show()
            } else if (Utilities.gameExists(games_list, title.trim())) {
                Toast.makeText(applicationContext, "Ese juego ya existe", Toast.LENGTH_SHORT)
                    .show()
            } else {

                Log.v("status", "Datos del formulario procesados")

                title = titleEdit.getText().toString()
                platform = platformEdit.getText().toString()
                rating = ratingLayout.rating.toString().toDouble().toInt()

                Log.v("title", title)
                Log.v("platform", platform)
                Log.v("rating", rating.toString())


                var new_game: Game?=null
                val id = db_ref.child("FG").child("game").push().key

                Log.v("status", "id del juego creada: $id")

//                HE DEJADO ESTO AQUÍ PARA TENERLO, PERO NO FUNCIONA BIEN

//                st_ref.child("FG").child("game").child(id!!).putFile(url_cover!!).addOnSuccessListener {
//                        uploadTask->
//                    Log.v("status", "descargando url" )
//                        st_ref.child("FG").child("cover").child(id).downloadUrl.addOnSuccessListener {
//                                uri: Uri?->
//
//                                Log.v("status", "url_descargada")
//
//                                new_game = Game(id,title,rating,platform,creation,uri.toString())
//
//                                Log.v("new game", new_game.toString())
//                                Log.v("status", "juego creado")
//
//                                db_ref.child("FG").child("game").child(id).setValue(new_game)
//
//                                Log.v("status", "Juego subido")
//
//                                Toast.makeText(applicationContext,"Juego creado con éxito",Toast.LENGTH_SHORT).show()
//                            }
//
//                    }



                launch {

                    Log.v("status", "lanzando corrutina")

                    var id_cover: String? = db_ref.child("FG").child("game").push().key

                    Log.v("status", "obtenida id de la caratula: $id_cover")

                    val firebase_cover_url = Utilities.saveCover(st_ref, id_cover!!, url_cover!!)

                    Log.v("status", "obtenida url de la caratula: $firebase_cover_url")

                    Log.v("db_ref", db_ref.toString())
                    Log.v("id_cover", id_cover)
                    Log.v("title", title.trim())
                    Log.v("rating", rating.toString())
                    Log.v(platform, platform)
                    Log.v("creation", creation.toString())
                    Log.v("firebase_cover_url", firebase_cover_url)

                    creation = System.currentTimeMillis().toInt()

                    Log.v("actual date", creation!!.toString())

//                    Log.v("actual date", Utilities.getDate(creation!!.toLong())!!)

                    Utilities.writeGame(db_ref, id_cover!!, title.trim(), platform, rating, creation!!, firebase_cover_url)

                    Utilities.toastCoroutine(this_activity, applicationContext, "Juego creado con éxito")

//                    val activity = Intent(applicationContext, MainActivity::class.java)
//                    startActivity(activity)
                }

            }
        }
    }
}