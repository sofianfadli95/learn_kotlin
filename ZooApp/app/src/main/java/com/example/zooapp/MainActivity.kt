package com.example.zooapp

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.animal_ticket.view.*

class MainActivity : AppCompatActivity() {

    var listOfAnimals = ArrayList<Animal>()
    var adapter:AnimalsAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // load animals
        listOfAnimals.add(Animal("Baboon","Babooooon",R.drawable.baboon, false))
        listOfAnimals.add(Animal("Bulldog","Bulldog",R.drawable.bulldog, false))
        listOfAnimals.add(Animal("Panda","Panda",R.drawable.panda, false))
        listOfAnimals.add(Animal("Swallow","Swallow",R.drawable.swallow_bird, false))
        listOfAnimals.add(Animal("Tiger","Tiger",R.drawable.white_tiger, true))
        listOfAnimals.add(Animal("Zebra","Zebra",R.drawable.zebra, false))

        adapter = AnimalsAdapter(this, listOfAnimals)
        tvListAnimal.adapter = adapter
    }

    fun delete(index:Int){
        listOfAnimals.removeAt(index)
        adapter!!.notifyDataSetChanged()
    }

    fun add(index:Int){
        listOfAnimals.add(index,listOfAnimals[index])
        adapter!!.notifyDataSetChanged()
    }

    inner class AnimalsAdapter:BaseAdapter {
        var listOfAnimals= ArrayList<Animal>()
        var context:Context?=null
        constructor(context:Context, listOfAnimals: ArrayList<Animal>):super(){
            this.listOfAnimals=listOfAnimals
            this.context=context
        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val animal = listOfAnimals[position]
            if (animal.isKiller == true) {
                var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                var myView = inflator.inflate(R.layout.animal_killer_ticket, null)
                myView.tvName.text = animal.name!!
                myView.tvDesc.text = animal.des!!
                myView.ivAnimal.setImageResource(animal.image!!)
                myView.ivAnimal.setOnClickListener {

                    val intent = Intent(context,DetailAnimals::class.java)
                    intent.putExtra("name", animal.name!!)
                    intent.putExtra("des", animal.des!!)
                    intent.putExtra("image", animal.image!!)
                    context!!.startActivity(intent)


                }
                return myView
            }else {
                var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                var myView = inflator.inflate(R.layout.animal_ticket, null)
                myView.tvName.text = animal.name!!
                myView.tvDesc.text = animal.des!!
                myView.ivAnimal.setImageResource(animal.image!!)
                myView.ivAnimal.setOnClickListener {

                    val intent = Intent(context,DetailAnimals::class.java)
                    intent.putExtra("name", animal.name!!)
                    intent.putExtra("des", animal.des!!)
                    intent.putExtra("image", animal.image!!)
                    context!!.startActivity(intent)

                }
                return myView
            }
        }

        override fun getItem(index: Int): Any {
            return listOfAnimals[index]
        }

        override fun getItemId(p0: Int): Long {
           return p0.toLong()
        }

        override fun getCount(): Int {

            return listOfAnimals.size
        }

    }
}
