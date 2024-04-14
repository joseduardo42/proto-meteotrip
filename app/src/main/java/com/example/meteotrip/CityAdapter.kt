package com.example.meteotrip

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class City(val name: String, val days: String, val temperature: String)

class CityAdapter(private val cities: MutableList<City>) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    class CityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCityName: TextView = view.findViewById(R.id.tvCityName)
        val tvDays: TextView = view.findViewById(R.id.tvDays)
        val tvTemperature: TextView = view.findViewById(R.id.tvTemperature)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)
        return CityViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = cities[position]
        holder.tvCityName.text = city.name
        holder.tvDays.text = formatDate(city.days)
        holder.tvTemperature.text = "${city.temperature} °C"
    }

    override fun getItemCount(): Int = cities.size

    fun addCity(city: City) {
        cities.add(city)
        notifyItemInserted(cities.size - 1)
    }

    fun sortCitiesByDate() {
        cities.sortBy { it.days }
        notifyDataSetChanged()
    }
    private fun formatDate(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val parsedDate = inputFormat.parse(date)
        return outputFormat.format(parsedDate!!)
    }
}


