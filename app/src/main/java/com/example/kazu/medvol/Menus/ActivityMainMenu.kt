package com.example.kazu.medvol.Menus

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.kazu.medvol.Extra.ActivityCredits
import com.example.kazu.medvol.Info.ActivityGeneralInfo
import com.example.kazu.medvol.Info.ActivityHelp
import com.example.kazu.medvol.List.ActivityList
import com.example.kazu.medvol.R
import com.example.kazu.medvol.Start.ActivityLogin
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main_menu.*

class ActivityMainMenu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        verifyUserLoggedIn()

        Menu_List.setOnClickListener{
            val intent = Intent(this, ActivityList::class.java)
            startActivity(intent)
        }
        Menu_Edit_Menu.setOnClickListener{
            val intent = Intent (this, ActivityEditMenu::class.java)
            startActivity(intent)
        }
        Menu_general_info.setOnClickListener{
            val intent = Intent (this, ActivityGeneralInfo::class.java)
            startActivity(intent)
        }
        Menu_credits_button.setOnClickListener{
            val intent = Intent (this, ActivityCredits::class.java)
            startActivity(intent)
        }
        Menu_help.setOnClickListener{
            val intent = Intent (this, ActivityHelp::class.java)
            startActivity(intent)
        }
    }

    private fun verifyUserLoggedIn (){
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null){
            val intent = Intent (this, ActivityLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.sign_out_option ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent (this, ActivityLogin::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
