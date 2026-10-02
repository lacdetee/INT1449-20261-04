package com.example.th2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskListActivity : AppCompatActivity() {

    private lateinit var tvDisplayName: TextView
    private lateinit var tvUsername: TextView
    private lateinit var tvTodayCount: TextView
    private lateinit var tvUpcomingCount: TextView
    private lateinit var rvTodayTasks: RecyclerView
    private lateinit var rvUpcomingTasks: RecyclerView
    private lateinit var tvEmptyToday: TextView
    private lateinit var tvEmpty: TextView
    private lateinit var btnAddTask: FloatingActionButton
    private lateinit var btnLogout: ImageButton

    private lateinit var dbHelper: TaskDatabaseHelper
    private lateinit var todayAdapter: TaskAdapter
    private lateinit var upcomingAdapter: TaskAdapter

    private val addTaskLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            loadTasks()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById<View>(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = TaskDatabaseHelper(this)

        initViews()
        setupRecyclerViews()
        setupListeners()
        displayUserInfo()
        loadTasks()
    }

    private fun initViews() {
        tvDisplayName = findViewById(R.id.tvDisplayName)
        tvUsername = findViewById(R.id.tvUsername)
        tvTodayCount = findViewById(R.id.tvTodayCount)
        tvUpcomingCount = findViewById(R.id.tvUpcomingCount)
        rvTodayTasks = findViewById(R.id.rvTodayTasks)
        rvUpcomingTasks = findViewById(R.id.rvUpcomingTasks)
        tvEmptyToday = findViewById(R.id.tvEmptyToday)
        tvEmpty = findViewById(R.id.tvEmpty)
        btnAddTask = findViewById(R.id.btnAddTask)
        btnLogout = findViewById(R.id.btnLogout)
    }

    private fun setupRecyclerViews() {
        todayAdapter = TaskAdapter(mutableListOf()) { task, isChecked ->
            dbHelper.updateTaskStatus(task.id, isChecked)
            loadTasks()
        }
        rvTodayTasks.layoutManager = LinearLayoutManager(this)
        rvTodayTasks.adapter = todayAdapter

        upcomingAdapter = TaskAdapter(mutableListOf()) { task, isChecked ->
            dbHelper.updateTaskStatus(task.id, isChecked)
            loadTasks()
        }
        rvUpcomingTasks.layoutManager = LinearLayoutManager(this)
        rvUpcomingTasks.adapter = upcomingAdapter
    }

    private fun setupListeners() {
        btnAddTask.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            addTaskLauncher.launch(intent)
        }

        btnLogout.setOnClickListener {
            finish()
        }
    }

    private fun displayUserInfo() {
        val username = intent.getStringExtra("username") ?: "User"
        tvDisplayName.text = "Nguyễn Văn A"
        tvUsername.text = "@$username"
    }

    private fun loadTasks() {
        val allTasks = dbHelper.getAllTasks()
        val todayStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        val todayTasks = allTasks.filter { it.deadline == todayStr }
        val upcomingTasks = allTasks.filter { it.deadline != todayStr }

        // Update counters
        tvTodayCount.text = "${todayTasks.size} việc"
        tvUpcomingCount.text = "${upcomingTasks.size} việc"

        // Today section
        if (todayTasks.isEmpty()) {
            tvEmptyToday.visibility = View.VISIBLE
            rvTodayTasks.visibility = View.GONE
        } else {
            tvEmptyToday.visibility = View.GONE
            rvTodayTasks.visibility = View.VISIBLE
        }

        // Upcoming section
        if (upcomingTasks.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            rvUpcomingTasks.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            rvUpcomingTasks.visibility = View.VISIBLE
        }

        todayAdapter.updateTasks(todayTasks)
        upcomingAdapter.updateTasks(upcomingTasks)
    }
}
