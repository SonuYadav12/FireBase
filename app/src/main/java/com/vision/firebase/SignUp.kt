package com.vision.firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.vision.firebase.databinding.ActivitySignUpBinding

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var isPasswordVisible = false
    private var isConfirmVisible = false

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
        database= FirebaseDatabase.getInstance()

        binding.google.setOnClickListener {
            // Implement Google Sign In functionality if needed
        }

        // The below code is for sign up using the email and password
        binding.signup.setOnClickListener {
            val email = binding.email.text.toString()
            val pass = binding.password.text.toString()
            val confirm = binding.confirm.text.toString()

            if (email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_LONG).show()
            } else if (pass.length < 6) {
                Toast.makeText(this, "Password cannot be less than 6 characters", Toast.LENGTH_LONG).show()
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
                    startActivity(Intent(this,Login::class.java))
                } else {

                    Toast.makeText(this, "Sign up failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
