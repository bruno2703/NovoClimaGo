package com.example.climago

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class DeleteClass {
    fun DeleteId(docId: String) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("cidades").document(docId)

        docRef.delete()
            .addOnSuccessListener { Log.d("delet", "Documento deletado com sucesso!") }
            .addOnFailureListener { e -> Log.w("delet", "Erro deletando documento", e) }
    }
}