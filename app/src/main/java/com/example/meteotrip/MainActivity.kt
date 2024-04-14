package com.example.meteotrip

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity(), AddCityDialogFragment.AddCityDialogListener {
    private lateinit var adapter: CityAdapter
    private val citiesList = mutableListOf<City>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // RecyclerView and Adapter
        val recyclerView: RecyclerView = findViewById(R.id.cityRecyclerView)
        adapter = CityAdapter(citiesList) // Passing instance to listener
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Add divider
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)

        // FAB and callback definition
        val fab: FloatingActionButton = findViewById(R.id.fab_add_city)
        fab.setOnClickListener {
            // Show Dialog
            val dialog = AddCityDialogFragment()
            dialog.show(supportFragmentManager, "com.example.meteotrip.AddCityDialogFragment")
        }

        // Initialize touches to item delete with swap
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val city = citiesList[position]

                // Remove city
                citiesList.removeAt(position)
                adapter.notifyItemRemoved(position)

                // Snackbar to undo the delete
                val snackbar = Snackbar.make(findViewById(android.R.id.content), "Cidade removida", Snackbar.LENGTH_LONG)
                snackbar.setAction("Desfazer") {
                    // re-add city if the user want undo
                    citiesList.add(position, city)
                    adapter.notifyItemInserted(position)
                }
                snackbar.show()
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onDialogPositiveClick(cityName: String, selectedDate: String, temperature: String) {
        // Add city to the
        val city = City(cityName, selectedDate, temperature) // Supondo que você tem um construtor de City que aceita nome e dias
        citiesList.add(city)
        // reorder cities by date
        adapter.sortCitiesByDate()
    }

}