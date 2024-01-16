package com.example.crud_videojuegos

import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class EditGame : AppCompatActivity(), CoroutineScope {

    lateinit var titleEdit : TextInputEditText
    lateinit var platformEdit : TextInputEditText
    lateinit var ratingLayout : RatingBar
    lateinit var createButton : ImageView
    lateinit var coverImage : ImageView
    private lateinit var modificar: ImageView
    private lateinit var volver: ImageView


    private var url_cover: Uri? = null
    private lateinit var db_ref: DatabaseReference
    private lateinit var st_ref: StorageReference
    private  lateinit var  pojo_game : Game
    private lateinit var games_list: MutableList<Game>



    private lateinit var job: Job


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_game)


        val this_activity = this
        job = Job()

        pojo_game = intent.getParcelableExtra<Game>("game")!!


        titleEdit = findViewById(R.id.nameEdit)
        platformEdit = findViewById(R.id.platformEdit)
        ratingLayout = findViewById(R.id.ratingLayout)
        coverImage = findViewById(R.id.cover)
        modificar = findViewById(R.id.edit)
        volver = findViewById(R.id.back)

        titleEdit.setText(pojo_game.title)
        platformEdit.setText(pojo_game.platform)
        ratingLayout.rating = pojo_game.rating.toString().toInt().toFloat()

        Glide.with(applicationContext)
            .load(pojo_game.cover)
            .apply(Utilities.opcionesGlide(applicationContext))
            .transition(Utilities.transicion)
            .into(coverImage)

        db_ref = FirebaseDatabase.getInstance().getReference()
        st_ref = FirebaseStorage.getInstance().getReference()

        games_list = Utilities.getGames(db_ref)

        modificar.setOnClickListener {

            if (titleEdit.text.toString().trim().isEmpty() ||
                platformEdit.text.toString().trim().isEmpty() ||
                ratingLayout.rating.toString() == "0.0"
            ) {
                Toast.makeText(
                    applicationContext, "Faltan datos en el " +
                            "formularion", Toast.LENGTH_SHORT
                ).show()

            } else if (Utilities.howManyGames(games_list, titleEdit.text.toString().trim()) > 1) {
                Toast.makeText(applicationContext, "Ese Juego ya existe", Toast.LENGTH_SHORT)
                    .show()
            } else {

                //GlobalScope(Dispatchers.IO)
                var url_cover_firebase = String()
                launch {
                    if(url_cover == null){
                        url_cover_firebase = pojo_game.cover!!
                    }else{
                        url_cover_firebase =
                            Utilities.saveCover(st_ref, pojo_game.id!!, url_cover!!)
                    }


                    Utilities.writeGame(
                        db_ref,
                        pojo_game.id!!,
                        titleEdit.text.toString().trim(),
                        platformEdit.text.toString().trim(),
                        ratingLayout.rating.toString().toDouble().toInt(),
                        pojo_game.date!!,
                        url_cover_firebase
                    )
                    Utilities.toastCoroutine(
                        this_activity,
                        applicationContext,
                        "Juego modificado con exito"
                    )
                    val activity = Intent(applicationContext, MainActivity::class.java)
                    startActivity(activity)
                }


            }




        }

        volver.setOnClickListener {
            val activity = Intent(applicationContext, ListGames::class.java)
            startActivity(activity)
        }

        coverImage.setOnClickListener {
            gallery_access.launch("image/*")
        }

    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private val gallery_access = registerForActivityResult(ActivityResultContracts.GetContent())
    {uri: Uri? ->
        if(uri!=null){
            url_cover = uri
            coverImage.setImageURI(uri)
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
}