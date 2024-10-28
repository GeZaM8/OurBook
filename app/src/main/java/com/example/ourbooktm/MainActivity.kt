package com.example.ourbooktm

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ourbooktm.database.DatabaseHelperOurBook
import com.example.ourbooktm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: DatabaseHelperOurBook
    private lateinit var personsAdapter: PersonsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelperOurBook(this)
        personsAdapter = PersonsAdapter(db.getAllUser())

        binding.rvPerson.layoutManager = LinearLayoutManager(this)
        binding.rvPerson.adapter = personsAdapter

        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, AddPerson::class.java)
            startActivity(intent)
        }

        binding.btnInfo.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        personsAdapter.refreshData(db.getAllUser())
    }
}