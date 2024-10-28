package com.example.ourbooktm

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ourbooktm.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lblGit.setOnClickListener {
            val url = binding.lblGit.text.toString()
            val open = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(open)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}