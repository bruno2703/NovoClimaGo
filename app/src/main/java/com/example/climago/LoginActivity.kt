package com.example.climago

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.climago.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedPreferences = getSharedPreferences("MY_APP", Context.MODE_PRIVATE)

        binding.BotaoLogin.setOnClickListener { validateLogin() }
        binding.botaoCadastro.setOnClickListener { navigateToRegisterScreen() }
    }

    private fun validateLogin() {
        val username = binding.campoUsuario.text.toString()
        val password = binding.campoSenha.text.toString()

        val savedUsername = sharedPreferences.getString("USERNAME", null)
        val savedPassword = sharedPreferences.getString("PASSWORD", null)

        if (username == savedUsername && password == savedPassword) {
            Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
            navigateToInicialScreen()
        } else {
            Toast.makeText(this, "Usuário ou senha inválidos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToRegisterScreen() {
        Intent(this, CadastroActivity::class.java).also {
            startActivity(it)
        }
    }

    private fun navigateToInicialScreen() {
        Intent(this, InicialActivity::class.java).also {
            startActivity(it)
        }
    }
}
