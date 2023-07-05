package com.example.climago

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.climago.FragmentsNavBar.SocialFragment
import com.example.climago.FragmentsNavBar.ConfigFragment
import com.example.climago.FragmentsNavBar.LocaisFragment
import com.example.climago.FragmentsNavBar.TelaInicialFragment
import com.example.climago.databinding.ActivityInicialBinding


class InicialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val telainicialfragment = TelaInicialFragment()
        val configfragment = ConfigFragment()
        val socialfragment = SocialFragment()
        val locaisfragment = LocaisFragment()

        setCurrentFragment(telainicialfragment)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.miHome -> setCurrentFragment(telainicialfragment)
                R.id.miConfig -> setCurrentFragment(configfragment)
                R.id.miSocial -> setCurrentFragment(socialfragment)
                R.id.miLocais -> setCurrentFragment(locaisfragment)
            }
            true
        }


    }

    private fun setCurrentFragment(fragment : Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }

}