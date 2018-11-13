package com.example.kazu.medvol.List

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import com.example.kazu.medvol.Models.DataPiece
import com.example.kazu.medvol.Models.Item_of_List
import com.example.kazu.medvol.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_item_info.*


class ActivityItemInfo : AppCompatActivity() {
    private var itemOfList = Item_of_List()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_info)

        itemOfList = intent.getParcelableExtra<Item_of_List>(ActivityList.USER_KEY)
        supportActionBar?.title = itemOfList.name

        Info_Name.text = itemOfList.name.toEditable()
        Info_device.text = itemOfList.device.toEditable()
        Info_id.text = itemOfList.id.toEditable()

        listenForLastData(itemOfList.item_key)

        Info_graphics_button.setOnClickListener {
            val intent = Intent(this, ActivityGraph::class.java)
            intent.putExtra(ActivityList.USER_KEY, itemOfList)
            startActivity(intent)
        }
    }

    private fun listenForLastData( itemid: String){
        val userid = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseDatabase.getInstance().getReference("logs/$userid/$itemid")
        ref.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val dataPiece = p0.getValue (DataPiece::class.java) ?: return
                val rateValue = dataPiece.rate.toString() + "g/10s"
                val massValue = dataPiece.mass.toString() + "g"

                Info_Rate_Value.text = rateValue
                Info_Mass_value.text = massValue
            }
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })

    }

    private fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.item_info_calibrate ->{
                val userid = FirebaseAuth.getInstance().uid?:""
                val ref = FirebaseDatabase.getInstance().getReference("itens/$userid/${itemOfList.item_key}")
                ref.child("update").setValue("yes")

                val intent = Intent(this, ActivityCalibrate::class.java)
                intent.putExtra(ActivityList.USER_KEY, itemOfList)
                startActivity(intent)
            }
            R.id.item_info_edit_menu->{
                val intent = Intent(this, ActivityCalibrate::class.java)
                intent.putExtra(ActivityList.USER_KEY, itemOfList)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_info_edit_menu, menu)
        return true
    }


}
