package com.example.kazu.medvol.List

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.kazu.medvol.Models.Item_of_List
import com.example.kazu.medvol.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_calibrate.*

class ActivityCalibrate : AppCompatActivity() {


    private var itemOfList = Item_of_List()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calibrate)

        itemOfList = intent.getParcelableExtra<Item_of_List>(ActivityList.USER_KEY)
        supportActionBar?.title = itemOfList.name
        listenForLastData(itemOfList.item_key)

        Calibrate_Name.text = itemOfList.name.toEditable()
        Calibrate_device.text = itemOfList.device.toEditable()
        Calibrate_id.text = itemOfList.id.toEditable()
    }
    private fun listenForLastData( itemid: String){
        val userid = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseDatabase.getInstance().getReference("itens/$userid/$itemid")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                println("aqui")
                val itemlist = p0.getValue (Item_of_List::class.java) ?: return
                println("aqui1")
                val calibration = itemlist.calibration_factor.toString()
                println("aqui2")
                val raw = itemlist.raw_vale.toString() + "g"

                Calibrate_CF_value.text = calibration
                Calibrate_RM_value.text = raw
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.item_info_edit_menu ->{
                val builder_dialog = AlertDialog.Builder(this)
                builder_dialog.setTitle("Confirm Calibration")
                builder_dialog.setMessage("Are you satisfied with the changes?")

                // Set a positive button and its click listener on alert dialog
                builder_dialog.setPositiveButton("YES"){_, _ ->
                    // Do something when user press the positive button
                    Toast.makeText(applicationContext,"Item has been deleted.", Toast.LENGTH_SHORT).show()
                    val userid = FirebaseAuth.getInstance().uid?:""
                    val ref = FirebaseDatabase.getInstance().getReference("itens/$userid/${itemOfList.item_key}")
                    ref.child("update").setValue("no")

                    val intent = Intent(this, ActivityList::class.java)
                    startActivity(intent)
                }
                // Display a negative button on alert dialog
                builder_dialog.setNegativeButton("No"){_,_ ->
                    Toast.makeText(applicationContext,"Action cancelled.", Toast.LENGTH_SHORT).show()
                }
                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = builder_dialog.create()

                // Display the alert dialog on app interface
                dialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.calibrate_menu, menu)
        return true
    }
}
