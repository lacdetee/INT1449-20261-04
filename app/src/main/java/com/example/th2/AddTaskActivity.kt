package com.example.th2

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

class AddTaskActivity : AppCompatActivity() {

    private lateinit var tilTaskName: TextInputLayout
    private lateinit var tilDeadline: TextInputLayout
    private lateinit var etTaskName: TextInputEditText
    private lateinit var etDeadline: TextInputEditText
    private lateinit var etNote: TextInputEditText
    private lateinit var btnSave: MaterialButton
    private lateinit var btnBack: ImageButton

    private lateinit var dbHelper: TaskDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_task)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbar)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, systemBars.top, v.paddingRight, v.paddingBottom)
            insets
        }

        dbHelper = TaskDatabaseHelper(this)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        tilTaskName = findViewById(R.id.tilTaskName)
        tilDeadline = findViewById(R.id.tilDeadline)
        etTaskName = findViewById(R.id.etTaskName)
        etDeadline = findViewById(R.id.etDeadline)
        etNote = findViewById(R.id.etNote)
        btnSave = findViewById(R.id.btnSave)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        etDeadline.setOnClickListener {
            showDatePicker()
        }

        tilDeadline.setEndIconOnClickListener {
            showDatePicker()
        }

        btnSave.setOnClickListener {
            saveTask()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format(
                    "%02d/%02d/%04d",
                    selectedDay,
                    selectedMonth + 1,
                    selectedYear
                )
                etDeadline.setText(formattedDate)
                tilDeadline.error = null
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun saveTask() {
        val taskName = etTaskName.text.toString().trim()
        val deadline = etDeadline.text.toString().trim()
        val note = etNote.text.toString().trim()

        var isValid = true

        if (taskName.isEmpty()) {
            tilTaskName.error = getString(R.string.error_empty_task_name)
            isValid = false
        } else {
            tilTaskName.error = null
        }

        if (deadline.isEmpty()) {
            tilDeadline.error = getString(R.string.error_empty_deadline)
            isValid = false
        } else {
            tilDeadline.error = null
        }

        if (isValid) {
            val task = Task(
                title = taskName,
                deadline = deadline,
                note = note
            )
            dbHelper.addTask(task)
            Toast.makeText(this, "Đã lưu công việc", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }
    }
}
