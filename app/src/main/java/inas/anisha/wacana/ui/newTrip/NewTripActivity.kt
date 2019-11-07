package inas.anisha.wacana.ui.newTrip

import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.applandeo.materialcalendarview.CalendarView.RANGE_PICKER
import com.applandeo.materialcalendarview.builders.DatePickerBuilder
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener
import inas.anisha.wacana.R
import inas.anisha.wacana.databinding.ActivityNewTripBinding
import inas.anisha.wacana.workers.NotificationWorker
import java.text.SimpleDateFormat
import java.time.Duration
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
            scheduleNotification()
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
            .setDate(Calendar.getInstance()) // Initial date as Calendar object
            .setMinimumDate(Calendar.getInstance().also {
                it.add(
                    Calendar.DATE,
                    -1
                )
            }) // Minimum available date

        datePickerDialog.build().show()
    }

    private fun scheduleNotification() {
        val inputData = Data.Builder().apply {
            putString(DESTINATION, viewModel.destination)
        }.build()

        viewModel.startDate.value?.let {
            val notificationWork = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                .setInitialDelay(calculateDelay(it))
                .setInputData(inputData)
                .addTag(TRIP_NOTIFICATION)
                .build()

            WorkManager.getInstance(this).enqueue(notificationWork)
        }

    }

    private fun calculateDelay(date: Calendar): Duration {
        val dueDate = Calendar.getInstance()
        date.let {
            dueDate.apply {
                set(Calendar.YEAR, it.get(Calendar.YEAR))
                set(Calendar.MONTH, it.get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, it.get(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, 12)
            }
        }
        return Duration.ofMillis(dueDate.timeInMillis - System.currentTimeMillis())
    }
}
