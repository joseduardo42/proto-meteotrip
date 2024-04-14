package com.example.meteotrip

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddCityDialogFragment : DialogFragment() {

    interface AddCityDialogListener {
        fun onDialogPositiveClick(cityName: String, selectedDate: String, temperature: String)
    }

    private var listener: AddCityDialogListener? = null
    private lateinit var cityNameEditText: EditText
    private lateinit var dateEditText: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity)
            val inflater = activity.layoutInflater
            val view = inflater.inflate(R.layout.dialog_add_city, null)
            cityNameEditText = view.findViewById(R.id.et_city_name)
            dateEditText = view.findViewById(R.id.et_date)

            dateEditText.setOnClickListener { showDatePickerDialog() }

            builder.setView(view)
                .setPositiveButton(R.string.add) { _, _ ->
                    val cityName = cityNameEditText.text.toString()
                    val selectedDate = dateEditText.text.toString()
                    fetchWeatherAndNotify(cityName, selectedDate)
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, monthOfYear, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(selectedYear, monthOfYear, dayOfMonth)
            }
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateEditText.setText(dateFormat.format(selectedDate.time))
        }, year, month, day).show()
    }

    private fun fetchWeatherAndNotify(cityName: String, selectedDate: String) {
        CoroutineScope(Dispatchers.IO).launch {

            val response = RetrofitClient.webservice.getCityWeatherAsString(cityName, "9cb8c9000f8e66e4d7c30c8d7f786348", "metric")
            // Threat the response
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    val forecastJson = JsonParser.parseString(responseBody.string()).asJsonObject
                    val list = forecastJson.getAsJsonArray("list")

                    // Found weather to the date desired
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val selectedDateTime = dateFormat.parse("$selectedDate 12:00:00") // meio-dia do dia selecionado

                    var temperatureForSelectedDate: String? = null

                    for (forecastElement in list) {
                        val dateTimeString = forecastElement.asJsonObject.get("dt_txt").asString
                        val dateTime = dateFormat.parse(dateTimeString)
                        if (dateTime.equals(selectedDateTime)) {
                            val main = forecastElement.asJsonObject.getAsJsonObject("main")
                            val temp = main.get("temp").asString
                            temperatureForSelectedDate = temp
                            break
                        }
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        listener?.onDialogPositiveClick(cityName, selectedDate, temperatureForSelectedDate ?: "No data for selected date")
                    }
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    listener?.onDialogPositiveClick(cityName, selectedDate, "API error")
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as AddCityDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement AddCityDialogListener")
        }
    }
}
