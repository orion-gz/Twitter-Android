package orion.app.twitter.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import orion.app.twitter.MainActivity
import orion.app.twitter.R
import orion.app.twitter.data.network.TokenManager

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager = TokenManager(this)
        if (tokenManager.getToken() != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val factory = AuthViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        val usernameInput = findViewById<TextInputEditText>(R.id.username_input)
        val passwordInput = findViewById<TextInputEditText>(R.id.password_input)
        val loginButton = findViewById<Button>(R.id.login_button)
        val closeButton = findViewById<ImageButton>(R.id.close_button)

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.login(username, password)
            }
        }

        closeButton.setOnClickListener {
            finish()
        }
        viewModel.authenticationState.observe(this) { state ->
            when (state) {
                AuthenticationState.AUTHENTICATED -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                AuthenticationState.AUTHENTICATION_FAILED -> {
                    Toast.makeText(this, "Login failed. Check your info.", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
}