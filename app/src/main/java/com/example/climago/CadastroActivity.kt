package com.example.climago

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.climago.databinding.ActivityCadastroBinding

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCadastroBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedPreferences = getSharedPreferences("MY_APP", Context.MODE_PRIVATE)

        binding.btCadastrar.setOnClickListener { registerUser() }
    }

    private fun registerUser() {
        val username = binding.etNome.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etSenha.text.toString()

        if (username.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
            sharedPreferences.edit().apply {
                putString("USERNAME", username)
                putString("EMAIL", email)
                putString("PASSWORD", password)
                apply()
            }
            Toast.makeText(this, "Registro bem-sucedido!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToLoginScreen() {
        Intent(this, LoginActivity::class.java).also {
            startActivity(it)
        }
    }

}
