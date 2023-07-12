package com.example.climago

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.climago.databinding.ActivityCadastroBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCadastroBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.btCadastrar.setOnClickListener { registerUser() }

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

    }

    private fun registerUser() {
        val username = binding.etNome.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etSenha.text.toString()

        if (username.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()

                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    Toast.makeText(this, "Registro bem-sucedido!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Erro ao atualizar perfil do usuário", Toast.LENGTH_SHORT).show()
                                }
                            }

                        navigateToLoginScreen()
                    } else {
                        Toast.makeText(this, "Erro ao registrar usuário", Toast.LENGTH_SHORT).show()
                    }
                }
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
