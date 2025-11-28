package orion.app.twitter.ui.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import orion.app.twitter.R

class SignUpActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        findViewById<ImageButton>(R.id.close_button).setOnClickListener {
            finish()
        }

        val factory = AuthViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        val usernameInput = findViewById<EditText>(R.id.username_input)
        val displayNameInput = findViewById<EditText>(R.id.display_name_input)
        val emailInput = findViewById<EditText>(R.id.email_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val signupButton = findViewById<Button>(R.id.signup_button)

        signupButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val displayName = displayNameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            viewModel.signUp(username, displayName, email, password)
        }

        viewModel.authenticationState.observe(this) { state ->
            when (state) {
                AuthenticationState.SIGNUP_SUCCESSFUL -> {
                    Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                    finish()
                }
                AuthenticationState.SIGNUP_FAILED -> {
                    Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
}
