package com.example.chatappassignment

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {
    private lateinit var googleSignInClient:GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef:DatabaseReference
    private lateinit var edtName: TextInputEditText
    private lateinit var edtEmail: TextInputEditText
    private lateinit var edtPassword: TextInputEditText
    private lateinit var btnSignup: Button
    private lateinit var btnGoogleSign: AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()
        val default_id:String="246983951260"
        val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(default_id).requestEmail().build()
        googleSignInClient=GoogleSignIn.getClient(this,gso)

        mAuth = FirebaseAuth.getInstance()
        edtName = findViewById(R.id.edt_user)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnSignup = findViewById(R.id.btn_signup)

        btnGoogleSign=findViewById(R.id.btn_googleSign)

        btnGoogleSign.setOnClickListener {
        val signInClient=googleSignInClient.signInIntent
            launcher.launch(signInClient)
        }

        btnSignup.setOnClickListener {
            val name=edtName.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            signup(name,email, password)
        }

    }
private val  launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
    result ->
    if (result.resultCode==Activity.RESULT_OK){
         val task=GoogleSignIn.getSignedInAccountFromIntent(result.data)
        if (task.isSuccessful){
            val account:GoogleSignInAccount?=task.result
            val credential=GoogleAuthProvider.getCredential(account?.idToken,null)
            mAuth.signInWithCredential(credential).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this,"done",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }else{
        Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
    }
}


    private fun signup(name:String,email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    addUserToDatabase(name,email,mAuth.currentUser?.uid!!)
                    val intent=Intent(this@SignUp, MainActivity::class.java)
                    finish()
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "some error Occurs", Toast.LENGTH_SHORT).show()
                }
            }


    }

    private fun addUserToDatabase(name: String, email: String, uid: String) {
mDbRef=FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").child(uid).setValue(User(name,email,uid))
    }
}