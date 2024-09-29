package com.example.attendence_application

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.attendence_application.databinding.ActivityManagementSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ManagementSignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManagementSignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagementSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.tvAlreadyuser.setOnClickListener {
            val intent = Intent(this, ManagementSignInActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignup.setOnClickListener {
            saveManagementData()
        }
    }

    private fun saveManagementData() {
        val instituteName = binding.etInstitudeName.text.toString()
        val instituteCode = binding.etInstitudeCode.text.toString()
        val institutePhoneNo = binding.etInstitudePhoneno.text.toString()
        val instituteEmail = binding.etInstitudeEmail.text.toString()
        val adminName = binding.etAdminName.text.toString()
        val adminEmail = binding.etAdminEmail.text.toString()
        val adminPhoneNo = binding.etAdminPhoneno.text.toString()
        val designation = binding.etAdminDesignation.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmpassword.text.toString()

        // Validate input fields
        if (instituteName.isEmpty() || instituteCode.isEmpty() ||
            institutePhoneNo.isEmpty() || instituteEmail.isEmpty() ||
            adminName.isEmpty() || adminEmail.isEmpty() ||
            adminPhoneNo.isEmpty() || designation.isEmpty() ||
            password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Empty Fields Are Not Allowed !!", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate password matching
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Create admin account in Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(adminEmail, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Admin created successfully in Firebase Auth
                    val adminUid = task.result?.user?.uid // Get the admin's Firebase UID

                    // Prepare management data to save in Firestore
                    val managementData = hashMapOf(
                        "instituteName" to instituteName,
                        "instituteCode" to instituteCode,
                        "institutePhoneNo" to institutePhoneNo,
                        "instituteEmail" to instituteEmail,
                        "adminName" to adminName,
                        "adminEmail" to adminEmail,
                        "adminPhoneNo" to adminPhoneNo,
                        "designation" to designation,
                        "adminUid" to adminUid // Store the admin's UID for reference
                    )

                    // Save management details in Firestore
                    firestore.collection("managements")
                        .add(managementData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Sign-up successful", Toast.LENGTH_SHORT).show()
                            // Redirect to Sign-In Activity
                            startActivity(Intent(this, SigninActivity::class.java))
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // If sign-up failed
                    Toast.makeText(this, "Failed to sign up: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
