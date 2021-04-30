package com.example.latihanmemo

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_update.*
import java.text.SimpleDateFormat
import java.time.Month
import java.time.Year
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        closeKeyBoard()
        setupListOfDataIntoRecyclerView()

        inputbtn.setOnClickListener {
            addRecord()
            setupListOfDataIntoRecyclerView()
        }

        //**untuk menyembunyikan keyboard pertamakali terpilih
        etDate.inputType = InputType.TYPE_NULL
        etTime.inputType = InputType.TYPE_NULL

        //**aksi yang dijalankan ketika tanggal telah terpilih
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DATE, dayOfMonth)
                //**updateDateInView() apabila memakai private fun updateDateInView
                val myFormat = "dd/MM/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                etDate.setText(sdf.format(cal.time))
            }
        }
        //**aksi yang akan dijalankan ketika timepicker di pilih
        val timeSetListener = object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                cal.set(Calendar.HOUR, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                //**updateTimeInView() apabila memakai private fun update time in view
                val myFormat = "HH:mm"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                etTime.setText(sdf.format(cal.time))

            }
        }
        etDate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                closeKeyBoard()
                DatePickerDialog(this@MainActivity,
                        dateSetListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show()

            }
        })

        etTime.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                closeKeyBoard()
                TimePickerDialog(this@MainActivity,
                        timeSetListener,
                        cal.get(Calendar.HOUR),
                        cal.get(Calendar.MINUTE), true).show()
            }
        })
    }
    /** private fun updateDateInView(){
    val myFormat ="dd/MM/yyyy"
    val sdf = SimpleDateFormat(myFormat, Locale.US)
    etDate.setText(sdf.format(cal.time))
    }
     */

    /** private fun updateTimeInView(){
    val myFormat = "HH:mm"
    val sdf = SimpleDateFormat(myFormat,Locale.US)
    etTime.setText(sdf.format(cal.time))
    }*/
    private fun addRecord() {
        val date = etDate.text.toString()
        val time = etTime.text.toString()
        val dateTime = "${date}(${time})"
        val keterangan = keterangantxt.text.toString()
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        if (!date.isEmpty() && !time.isEmpty() && !keterangan.isEmpty()) {
            val status = databaseHandler.addActivity(modelclass(0, dateTime, keterangan))
            if (status > -1) {
                Toast.makeText(applicationContext, "Record Saved", Toast.LENGTH_LONG).show()
                etDate.text.clear()
                etTime.text.clear()
                keterangantxt.text.clear()
                closeKeyBoard()
            }
        } else {
            Toast.makeText(applicationContext, "Date Time Description cannot be blank", Toast.LENGTH_LONG).show()
        }
    }

    private fun getItemList(): ArrayList<modelclass> {
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val actList: ArrayList<modelclass> = databaseHandler.viewActivity()
        return actList
    }

    private fun setupListOfDataIntoRecyclerView() {
        if (getItemList().size > 0) {
            rvItem.visibility = View.VISIBLE
            norecordku.visibility = View.GONE

            rvItem.layoutManager = LinearLayoutManager(this)
            rvItem.adapter = ItemAdapter(this, getItemList())
        } else {
            rvItem.visibility = View.GONE
            norecordku.visibility = View.VISIBLE
        }
    }
    fun deleteRecordAlertDialog(modelclass: modelclass) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Delete Record")
        builder.setMessage("Are you sure?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //menampilkan tombol yes
        builder.setPositiveButton("Yes") { dialog: DialogInterface, which ->
            val databaseHandler :DatabaseHandler = DatabaseHandler(this)
            val status = databaseHandler.deleteActivity(modelclass(modelclass.id,"",""))

            if (status > -1){
                Toast.makeText(this, "Record Deleted Successfully", Toast.LENGTH_LONG).show()
                setupListOfDataIntoRecyclerView()
            }

            dialog.dismiss()
        }
        //menampilkan tombol no
        builder.setNegativeButton("No") { dialog: DialogInterface, which ->

            dialog.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        //memastikan user menekan tombol yes or no
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
    fun updateRecordDialog(modelclass: modelclass) {
        val updatedialog = Dialog(this, R.style.Theme_Dialog)
        updatedialog.setCancelable(false)
        updatedialog.setContentView(R.layout.dialog_update)

        updatedialog.etUpdateDate.setText(modelclass.calendar)
        updatedialog.etUpdateTime.setText(modelclass.keterangan)

        val datetime = (modelclass.calendar).split("(")
        val date = datetime[0]
        var time = datetime[0]

        //memecah date berdasarkan karakter
        val dateList = date.split("/")
        val year = dateList[2].toInt()
        val month = dateList[1].toInt() - 1
        val day = dateList[0].toInt()

        time = time.dropLast(1)
        val timeList = time.split(":")
        val hour = timeList[0].toInt()
        val minute = timeList[1].toInt()

        updatedialog.etUpdateDate.setText(date)
        updatedialog.etUpdateTime.setText(time)
        updatedialog.etUpdateKeterangan.setText(modelclass.keterangan)

        updatedialog.etUpdateDate.inputType = InputType.TYPE_NULL
        updatedialog.etUpdateTime.inputType = InputType.TYPE_NULL


        val UpdatedateSetListener =object : DatePickerDialog.OnDateSetListener   {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int){
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DATE, dayOfMonth)

                val myFormat = "dd/MM/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                etDate.setText(sdf.format(cal.time))

            }

        }

        val UpdatetimeSetListener = object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int){
            cal.set(Calendar.HOUR, hourOfDay)
            cal.set(Calendar.MINUTE, minute)

            val myFormat = "HH:mm"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            updatedialog.etUpdateTime.setText(sdf.format(cal.time))
        }

        }

        updatedialog.etUpdateDate!!.setOnClickListener {
            closeKeyBoard()
            DatePickerDialog(this, UpdatedateSetListener, year ,month, day).show()
        }
        updatedialog.etUpdateTime!!.setOnClickListener {
            closeKeyBoard()
            TimePickerDialog( this, UpdatetimeSetListener, hour, minute, true).show()
        }
        updatedialog.tvCancel.setOnClickListener {
            updatedialog.dismiss()
        }
        updatedialog.show()

    }


    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imn = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imn.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}




