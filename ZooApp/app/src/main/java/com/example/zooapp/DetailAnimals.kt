package com.example.zooapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_detail_animals.*

class DetailAnimals : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_animals)

        val bundle:Bundle = intent.extras
        val name = bundle.getString("name")
        val des = bundle.getString("des")
        val image = bundle.getInt("image")
        ivAnimalDetail.setImageResource(image)
        tvNameDetail.text=name
        tvDescDetail.text=des
    }
}
