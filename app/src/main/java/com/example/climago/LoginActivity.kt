package com.example.climago

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.climago.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.BotaoLogin.setOnClickListener { validateLogin() }
        binding.botaoCadastro.setOnClickListener { navigateToRegisterScreen() }

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

    }

    private fun validateLogin() {
        val email = binding.campoUsuario.text.toString()
        val password = binding.campoSenha.text.toString()

        if (email.isNotBlank() && password.isNotBlank()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
                        navigateToInicialScreen()
                    } else {
                        Toast.makeText(this, "Usuário ou senha inválidos", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
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
