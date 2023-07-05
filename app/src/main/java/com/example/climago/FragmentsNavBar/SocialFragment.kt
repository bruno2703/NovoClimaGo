package com.example.climago.FragmentsNavBar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.climago.AdapterLocais
import com.example.climago.AdapterSeguidores
import com.example.climago.R
import com.example.climago.databinding.FragmentSocialBinding

data class User(val username: String, var isFollowing: Boolean)

class SocialFragment : Fragment(R.layout.fragment_social) {

    private lateinit var adapter: AdapterSeguidores
    private lateinit var binding: FragmentSocialBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSocialBinding.bind(view)



        val users = listOf(User("Alice", false), User("Bob", true), User("Charlie", false))
        adapter = AdapterSeguidores(users)

        initRecyclerView()
    }

    private fun initRecyclerView(){
        with(binding.RCListaSeguidores) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SocialFragment.adapter
        }
    }

}