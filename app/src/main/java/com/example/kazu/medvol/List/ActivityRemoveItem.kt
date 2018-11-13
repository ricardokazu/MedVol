package com.example.kazu.medvol.List

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import com.example.kazu.medvol.Models.Item_of_List
import com.example.kazu.medvol.Models.ListRemoveRow
import com.example.kazu.medvol.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_remove_item.*
import android.util.SparseBooleanArray
import android.widget.Toast

class ActivityRemoveItem : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()
    // sparse boolean array for checking the state of the items
    private val itemStateArray = SparseBooleanArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remove_item)

        recycler_view_remove_list.adapter = adapter
        recycler_view_remove_list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener{item, _->
            val builder_dialog = AlertDialog.Builder(this)
            val listItem = item as ListRemoveRow
            builder_dialog.setTitle("Remove Item")
            builder_dialog.setMessage("Do you wish to delete '" + listItem.item_oL.name + "'.")

            // Set a positive button and its click listener on alert dialog
            builder_dialog.setPositiveButton("YES"){_, _ ->
                // Do something when user press the positive button
                Toast.makeText(applicationContext,"Item has been deleted.",Toast.LENGTH_SHORT).show()
                deleteItem (listItem)

                val intent = Intent (this, ActivityList::class.java)
                startActivity(intent)
                finish()
            }
            // Display a negative button on alert dialog
            builder_dialog.setNegativeButton("No"){_,_ ->
                Toast.makeText(applicationContext,"Action cancelled.",Toast.LENGTH_SHORT).show()
            }
            // Display a neutral button on alert dialog
//            builder_dialog.setNeutralButton("Cancel"){_,_ ->
//                Toast.makeText(applicationContext,"You cancelled the dialog.",Toast.LENGTH_SHORT).show()
//            }
            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder_dialog.create()

            // Display the alert dialog on app interface
            dialog.show()
        }

        listenForItens()
    }

//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        when(item?.itemId){
//            R.id.menu_remove_menu ->{
//                // check witch are selected
//                // remove the thing in the firebase
//
//                val intent = Intent (this, ActivityList::class.java)
//                startActivity(intent)
//                finish()
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.remove_menu, menu)
//        return true
//    }

    val itemListMap = HashMap<String, Item_of_List>()

    private fun RefreshRecyclerViewItens(){
        adapter.clear()
        itemListMap.values.forEach{
            adapter.add(ListRemoveRow(it))
        }
    }

    private fun listenForItens (){
        val uid = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseDatabase.getInstance().getReference("/itens/$uid")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val item = p0.getValue(Item_of_List::class.java) ?: return
                itemListMap [p0.key!!] = item
                RefreshRecyclerViewItens ()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val item = p0.getValue(Item_of_List::class.java) ?: return
                itemListMap [p0.key!!] = item
                RefreshRecyclerViewItens ()
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })
    }
    private fun deleteItem (ItemDelete: ListRemoveRow){
        val uid = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseDatabase.getInstance().getReference("/itens/$uid")
        ref.child(ItemDelete.item_oL.item_key).setValue(null)
    }
}
