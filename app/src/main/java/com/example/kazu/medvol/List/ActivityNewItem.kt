package com.example.kazu.medvol.List

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.kazu.medvol.Models.Item_of_List
import com.example.kazu.medvol.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_new_item.*
import java.text.DateFormat
import java.util.*

class ActivityNewItem : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_item)

        supportActionBar?.title = "Add New Item"

        Item_next_button.setOnClickListener{
            saveItemToDatabase()
            val intent = Intent(this, ActivityList::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun saveItemToDatabase(){
        val uid = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseDatabase.getInstance().getReference("/itens/$uid").push()

        val name = Item_Name.text.toString()
        val id = Item_ID_info.text.toString()
        val device = Item_Device_info.text.toString()
        val calibration_factor_string = Item_Calibration_Factor.text.toString()
        val calibration_factor = java.lang.Float.parseFloat(calibration_factor_string)
        val item_key = ref.key?:""

        if (name.isEmpty()||id.isEmpty()||device.isEmpty()){
            Toast.makeText(this,"Failed to add Item to Database.",Toast.LENGTH_SHORT).show()
            return
        }
        val timeZone = TimeZone.getTimeZone("America/Sao_Paulo")
        val calendar = GregorianCalendar()
        calendar.timeZone = timeZone

        val yourmilliseconds = calendar.getTimeInMillis()
        val resultdate = DateFormat.getDateTimeInstance().format(Date(yourmilliseconds))

        val item = Item_of_List(name, id, device, resultdate, calibration_factor, item_key, 0f, "no", 0f, 0f)

        ref.setValue(item)
                .addOnSuccessListener {
                    Toast.makeText(this,"New Item Created Successfully!!",Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this,"Failed to add Item to Database.",Toast.LENGTH_SHORT).show()
                }

    }
}
