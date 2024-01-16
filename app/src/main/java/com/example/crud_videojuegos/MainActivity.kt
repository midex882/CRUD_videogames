package com.example.crud_videojuegos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var create_button = findViewById<Button>(R.id.create)
        var list_button = findViewById<Button>(R.id.list)
        var logo = findViewById<ImageView>(R.id.logo)

        create_button.setOnClickListener {
            val create = Intent(applicationContext, CreateGame::class.java)
            startActivity(create)
        }

        list_button.setOnClickListener {
            val create = Intent(applicationContext, ListGames::class.java)
            startActivity(create)
        }

        logo.setOnClickListener {
            val create = Intent(applicationContext, Surprise::class.java)
            startActivity(create)
        }

    }
}