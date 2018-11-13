package com.example.kazu.medvol.Menus

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.kazu.medvol.List.ActivityNewItem
import com.example.kazu.medvol.List.ActivityRemoveItem
import com.example.kazu.medvol.R
import kotlinx.android.synthetic.main.activity_edit_menu.*

class ActivityEditMenu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_menu)

        edit_add_button.setOnClickListener{
            val intent = Intent(this, ActivityNewItem::class.java)
            startActivity(intent)
        }
        edit_remove_button.setOnClickListener{
            val intent = Intent(this, ActivityRemoveItem::class.java)
            startActivity(intent)
        }
    }
}
