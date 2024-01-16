package com.example.crud_videojuegos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.SpinnerAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ListGames : AppCompatActivity() {



    private lateinit var volver: ImageView
    private lateinit var spinnerLayout : Spinner
    private lateinit var recycler: RecyclerView
    private  lateinit var lista:MutableList<Game>
    private lateinit var adaptador: GameAdapter
    private lateinit var db_ref: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_games)


        volver = findViewById(R.id.back)

        lista = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().getReference()

        db_ref.child("FG")
            .child("game")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    lista.clear()
                    snapshot.children.forEach{hijo: DataSnapshot?
                        ->
                        val pojo_game = hijo?.getValue(Game::class.java)
                        lista.add(pojo_game!!)
                    }
                    recycler.adapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }

            })

        adaptador = GameAdapter(lista)
        recycler = findViewById(R.id.recyclerView)
        recycler.adapter = adaptador
        recycler.layoutManager = LinearLayoutManager(applicationContext)
        recycler.setHasFixedSize(true)
        spinnerLayout = findViewById(R.id.spinnerOptions)

        spinnerLayout.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                val selectedOption = parentView?.getItemAtPosition(position).toString()

                when (selectedOption) {
                    "a-b" -> {
                        lista.sortBy { it.title }
                        Toast.makeText(applicationContext, "Option 'a-b' selected", Toast.LENGTH_SHORT).show()
                    }
                    "por valoración" -> {
                        lista.sortBy { it.rating }
                        Toast.makeText(applicationContext, "Option 'por valoracion' selected", Toast.LENGTH_SHORT).show()
                    }
                    // Add more cases as needed

                }
                recycler.adapter?.notifyDataSetChanged()

            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Handle case where nothing is selected
            }
        }


        volver.setOnClickListener {
            val activity = Intent(applicationContext, MainActivity::class.java)
            startActivity(activity)
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu,menu)
//        val item = menu?.findItem(R.id.search)
//        val searchView = item?.actionView as SearchView
//
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                adaptador.filter.filter((newText))
//                return true
//            }
//        })
//
//
//
////        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
////            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
////                return  true
////            }
////
////            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
////                adaptador.filter.filter("")
////                return true
////            }
////
////        })
//
//        return super.onCreateOptionsMenu(menu)
//    }








}