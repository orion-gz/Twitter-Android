package orion.app.twitter.ui.createtweet

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.google.android.material.imageview.ShapeableImageView
import orion.app.twitter.R

class CreateTweetActivity : AppCompatActivity() {

    private lateinit var viewModel: CreateTweetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_tweet)

        val factory = CreateTweetViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[CreateTweetViewModel::class.java]

        val cancelButton = findViewById<Button>(R.id.cancel_button)
        val postTweetButton = findViewById<Button>(R.id.post_tweet_button)
        val tweetContentInput = findViewById<EditText>(R.id.tweet_content_input)
        val profileImage = findViewById<ShapeableImageView>(R.id.profile_image)

        postTweetButton.isEnabled = false
        postTweetButton.alpha = 0.5f

        tweetContentInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val hasContent = !s.isNullOrBlank()
                postTweetButton.isEnabled = hasContent
                postTweetButton.alpha = if (hasContent) 1.0f else 0.5f
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cancelButton.setOnClickListener {
            finish()
        }

        postTweetButton.setOnClickListener {
            val content = tweetContentInput.text.toString()
            if (content.isNotBlank()) {
                viewModel.postTweet(content)
            }
        }

        profileImage.load("https://via.placeholder.com/150") {
            placeholder(R.drawable.default_profile)
            error(R.drawable.default_profile)
            transformations(coil.transform.CircleCropTransformation())
        }

        viewModel.tweetPosted.observe(this) { success ->
            if (success) {
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Failed to post tweet", Toast.LENGTH_SHORT).show()
            }
        }
    }
}