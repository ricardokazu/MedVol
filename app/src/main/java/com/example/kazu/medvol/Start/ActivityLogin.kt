package com.example.kazu.medvol.Start

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.kazu.medvol.Menus.ActivityMainMenu
import com.example.kazu.medvol.ActivityTrash
import com.example.kazu.medvol.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class ActivityLogin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Login_Button.setOnClickListener {
            performLogin()
        }

        Login_to_Register.setOnClickListener{
            val intent = Intent(this, ActivityRegister::class.java)
            startActivity(intent)
        }

        Login_Forgot.setOnClickListener{
            val intent = Intent(this, ActivityTrash::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin(){
        val email = Login_Email.text.toString()
        val password = Login_Password.text.toString()

        Log.d("ActivityLogin", "Email: " + email)
        Log.d("ActivityLogin", "Password: "+password)

        if (email.isEmpty()){
            Toast.makeText(this,"Please enter an email.", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.isEmpty()) {
            Toast.makeText(this,"Please enter a password.", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener{
                    if (!it.isSuccessful)return@addOnCompleteListener
                    val intent = Intent (this, ActivityMainMenu::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener{
                    Toast.makeText(this,"Failed to Login: ${it.message}",Toast.LENGTH_SHORT).show()
                }


    }
}
