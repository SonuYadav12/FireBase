package com.vision.firebase

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.vision.firebase.databinding.ActivitySignUpBinding

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var isPasswordVisible = false
    private var isConfirmVisible = false
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toggle password visibility when the eye icon is touched
        binding.passwordToggle.setOnTouchListener { _, event ->
            handlePasswordToggle(event, binding.password)
            true
        }

        // Toggle confirm password visibility when the eye icon is touched
        binding.confirmPasswordToggle.setOnTouchListener { _, event ->
            handlePasswordToggle(event, binding.confirm)
            true
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        binding.google.setOnClickListener {
            val signInClient = googleSignInClient.signInIntent
            launcher.launch(signInClient)
        }

        // The below code is for sign up using the email and password
        binding.signup.setOnClickListener {
            val email = binding.email.text.toString()
            val pass = binding.password.text.toString()
            val confirm = binding.confirm.text.toString()

            if (email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_LONG).show()
            } else if (pass.length < 6) {
                Toast.makeText(this, "Password cannot be less than 6 characters", Toast.LENGTH_LONG)
                    .show()
            } else if (pass != confirm) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show()
            } else {
                // Call the method to create the user account with email and password
                createUserWithEmailAndPassword(email, pass)
            }
        }
    }

    private fun handlePasswordToggle(event: MotionEvent, editText: EditText) {
        val toggleIcon: Int
        val transformationMethod: android.text.method.TransformationMethod

        if (event.action == MotionEvent.ACTION_DOWN) {
            // Show password characters
            transformationMethod = HideReturnsTransformationMethod.getInstance()
            toggleIcon = R.drawable.eyeseen
        } else if (event.action == MotionEvent.ACTION_UP) {
            // Hide password characters
            transformationMethod = PasswordTransformationMethod.getInstance()
            toggleIcon = R.drawable.eyehide
        } else {
            return
        }

        editText.transformationMethod = transformationMethod

        // Set the appropriate eye icon for the corresponding password field
        if (editText == binding.password) {
            binding.passwordToggle.setImageResource(toggleIcon)
            isPasswordVisible = !isPasswordVisible
        } else if (editText == binding.confirm) {
            binding.confirmPasswordToggle.setImageResource(toggleIcon)
            isConfirmVisible = !isConfirmVisible
        }
    }


    private fun createUserWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val userRef = database.getReference("users").child(userId)
                        userRef.child("email").setValue(email)
                        userRef.child("password").setValue(password)
                        userRef.child("registrationDate").setValue(ServerValue.TIMESTAMP)

                    }
                    Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Login::class.java))
                } else {

                    Toast.makeText(this, "Sign up failed. Please try again.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }


    private var launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    val account: GoogleSignInAccount? = task.result
                    val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                    signInWithGoogle(credential)
                }
            } else {
                Toast.makeText(this, "Failed Try Again", Toast.LENGTH_LONG).show()
            }
        }

    private fun signInWithGoogle(credential: AuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    if (user != null) {
                        val userId = user.uid
                        val email = user.email
                        // You can save user information to Firebase Realtime Database here
                        val userRef = database.getReference("users").child(userId)
                        userRef.child("email").setValue(email)
                        userRef.child("registrationDate").setValue(ServerValue.TIMESTAMP)

                        Toast.makeText(this, "Sign in with Google successful!", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(this, Home::class.java))
                    }
                } else {
                    // If sign in fails, display a message to the user
                    Toast.makeText(
                        this,
                        "Sign in with Google failed. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
