package inas.anisha.wacana.ui.newTrip

import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.applandeo.materialcalendarview.CalendarView.RANGE_PICKER
import com.applandeo.materialcalendarview.builders.DatePickerBuilder
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener
import inas.anisha.wacana.R
import inas.anisha.wacana.databinding.ActivityNewTripBinding
import java.text.SimpleDateFormat
import java.util.*

class NewTripActivity : AppCompatActivity() {

    lateinit var viewModel: NewTripViewModel
    lateinit var binding: ActivityNewTripBinding

    companion object {
        const val DESTINATION = "DESTINATION"
        const val TRIP_NOTIFICATION = "TRIP_NOTIFICATION"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_trip, null)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add New Trip"

        viewModel = ViewModelProviders.of(this).get(NewTripViewModel::class.java)
        binding.viewModel = viewModel

    }

    override fun onStart() {
        super.onStart()
        binding.editTextStartDate.apply {
            inputType = InputType.TYPE_NULL
            setOnClickListener {
                showDatePicker()
            }
            setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    showDatePicker()
                    view.clearFocus()
                }
            }
        }

        binding.editTextEndDate.apply {
            inputType = InputType.TYPE_NULL
            setOnClickListener {
                showDatePicker()
            }
            setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    showDatePicker()
                    view.clearFocus()
                }
            }
        }

        binding.buttonAddTrip.setOnClickListener {
            viewModel.addTrip()
            finish()
        }

        viewModel.startDate.observe(this, androidx.lifecycle.Observer {
            binding.editTextStartDate.setText(SimpleDateFormat("dd MMM yyyy").format(it.time))
        })
        viewModel.endDate.observe(this, androidx.lifecycle.Observer {
            binding.editTextEndDate.setText(SimpleDateFormat("dd MMM yyyy").format(it.time))
        })
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerBuilder(
            this, OnSelectDateListener {
                viewModel.startDate.value = it[0]
                viewModel.endDate.value = it[it.size - 1]
            })
            .setPickerType(RANGE_PICKER)
            .setDate(Calendar.getInstance())
            .setMinimumDate(Calendar.getInstance().also {
                it.add(
                    Calendar.DATE,
                    -1
                )
            })

        datePickerDialog.build().show()
    }

}
