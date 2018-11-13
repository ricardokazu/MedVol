package com.example.kazu.medvol.Start

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.kazu.medvol.Models.User
import com.example.kazu.medvol.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class ActivityRegister : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        Register_Button.setOnClickListener{
            val access_code = Register_Access_Code.text.toString()
            if (access_code == "ita_ele_18") performRegister()
            else{
                Toast.makeText(this,"Please enter a valid access code.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }
    }

    private fun performRegister(){
        val email = Register_Email.text.toString()
        val password = Register_Password.text.toString()
        val password_confirm = Register_Password_Confirm.text.toString()

        Log.d("ActivityLogin", "Email: " + email)
        Log.d("ActivityLogin", "Password: "+password)
        Log.d("ActivityLogin", "Password confirmed: "+password_confirm)

        if (email.isEmpty()){
            Toast.makeText(this,"Please enter an email.", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.isEmpty()) {
            Toast.makeText(this,"Please enter a password.", Toast.LENGTH_SHORT).show()
            return
        }
        if (password_confirm.isEmpty()) {
            Toast.makeText(this,"Please confirm the password.", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != password_confirm){
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener() {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    saveUserToDatabase()
                }
                .addOnFailureListener{
                    Toast.makeText(this,"Failed to Create a New User: ${it.message}",Toast.LENGTH_SHORT).show()
                }
    }

    private fun saveUserToDatabase(){
        val uid = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User (uid, Register_Email.text.toString() )
        Log.d("ActivityRegister", "Trying to upload: " + uid + " and " + Register_Email.text.toString())

        ref.setValue(user)
                .addOnSuccessListener {
                    Toast.makeText(this,"New User Created Successfully!!",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ActivityLogin::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener{
                    Toast.makeText(this,"Failed to add User to Database.",Toast.LENGTH_SHORT).show()
                }

    }
}