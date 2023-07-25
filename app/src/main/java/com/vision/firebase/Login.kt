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
import com.google.firebase.auth.FirebaseUser
import com.vision.firebase.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth=FirebaseAuth.getInstance()


        binding.register.setOnClickListener {
            startActivity(Intent(this,SignUp::class.java))
            finish()
        }

        binding.signup.setOnClickListener {
            signInWithEmailAndPassword(binding.email.text.toString(),binding.password.text.toString())
        }


        binding.passwordToggle.setOnTouchListener { _, event ->
            handlePasswordToggle(event, binding.password)
            true
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
        }
    }


    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user: FirebaseUser? = auth.currentUser
                if (user != null) {
                    val userEmail = user.email
                    if (userEmail != null && userEmail.trim() == email.trim() && user.isEmailVerified) {
                        // Authentication successful, and the user's email and password match the provided credentials.
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, Home::class.java))
                        finish()
                    } else {
                        // Authentication successful, but the user's email or email verification status does not match the provided credentials.
                        if (!user.isEmailVerified) {
                            Toast.makeText(this, "Please verify your email address before logging in.", Toast.LENGTH_LONG).show()

                            user?.sendEmailVerification()
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Verification email sent successfully
                                        Toast.makeText(this, "Verification email sent.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // Failed to send verification email
                                        Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(this, "Incorrect email or password.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                // Authentication failed. Show an error message or handle the failure accordingly.
                Toast.makeText(this, "Authentication failed. Please try again.", Toast.LENGTH_LONG).show()
            }
        }
    }

}