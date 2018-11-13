package com.example.kazu.medvol.List

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.kazu.medvol.ActivityTrash
import com.example.kazu.medvol.Models.Item_of_List
import com.example.kazu.medvol.Models.ListRow
import com.example.kazu.medvol.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.item_list.view.*

class ActivityList : AppCompatActivity() {

    companion object {
        val USER_KEY = "USER_KEY"
    }

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        recycler_view_list.adapter = adapter
        recycler_view_list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener{item, view->
            val listItem = item as ListRow
            val intent = Intent (view.context, ActivityItemInfo::class.java)
            intent.putExtra(USER_KEY,  listItem.item_oL)
            startActivity(intent)
        }

        listenForItens()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.edit_menu_add ->{
                val intent = Intent (this, ActivityNewItem::class.java)
                startActivity(intent)
            }
            R.id.edit_menu_remove ->{
                val intent = Intent (this, ActivityRemoveItem::class.java)
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    val itemListMap = HashMap<String, Item_of_List>()

    private fun RefreshRecyclerViewItens(){
        adapter.clear()
        itemListMap.values.forEach{
            adapter.add(ListRow(it))
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
}

