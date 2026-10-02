package com.example.th2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private lateinit var tilUsername: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupListeners()
    }

    private fun initViews() {
        tilUsername = findViewById(R.id.tilUsername)
        tilPassword = findViewById(R.id.tilPassword)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInput(username, password)) {
                if (username == "admin" && password == "1234") {
                    val intent = Intent(this, TaskListActivity::class.java)
                    intent.putExtra("username", username)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<android.widget.TextView>(R.id.tvRegister).setOnClickListener {
            Toast.makeText(this, "Chức năng đăng ký đang được phát triển", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInput(username: String, password: String): Boolean {
        var isValid = true

        if (username.isEmpty()) {
            tilUsername.error = getString(R.string.error_empty_username)
            isValid = false
        } else {
            tilUsername.error = null
        }

        if (password.isEmpty()) {
            tilPassword.error = getString(R.string.error_empty_password)
            isValid = false
        } else {
            tilPassword.error = null
        }

        return isValid
    }
}