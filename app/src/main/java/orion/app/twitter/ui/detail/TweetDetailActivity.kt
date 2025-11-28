package orion.app.twitter.ui.detail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View // [필수] View import
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu // [필수] PopupMenu import
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import orion.app.twitter.R
import orion.app.twitter.data.network.TokenManager // [추가]
import orion.app.twitter.ui.main.TweetAdapter
import java.text.SimpleDateFormat
import java.util.Locale

class TweetDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: TweetDetailViewModel
    private lateinit var replyAdapter: TweetAdapter

    private lateinit var profileImage: ShapeableImageView
    private lateinit var displayName: TextView
    private lateinit var username: TextView
    private lateinit var tweetContent: TextView
    private lateinit var tweetDate: TextView

    private lateinit var retweetCountText: TextView
    private lateinit var likeCountText: TextView

    private lateinit var replyButtonIcon: ImageButton
    private lateinit var retweetButtonIcon: ImageButton
    private lateinit var likeButtonIcon: ImageButton
    private lateinit var shareButtonIcon: ImageButton
    private lateinit var moreButton: ImageButton

    private lateinit var myProfileImage: ShapeableImageView
    private lateinit var replyInput: TextInputEditText
    private lateinit var replySendButton: Button
    private lateinit var repliesRecyclerView: RecyclerView

    private var currentUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tweet_detail)

        val tweetId = intent.getStringExtra("TWEET_ID")
        if (tweetId == null) {
            finish()
            return
        }

        currentUserId = TokenManager(this).getUserId()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { finish() }

        val factory = TweetDetailViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[TweetDetailViewModel::class.java]

        bindViews()
        setupUI()
        observeViewModel()

        viewModel.fetchTweetDetail(tweetId)
    }

    private fun bindViews() {
        profileImage = findViewById(R.id.profile_image)
        displayName = findViewById(R.id.display_name)
        username = findViewById(R.id.username)
        tweetContent = findViewById(R.id.tweet_content)
        tweetDate = findViewById(R.id.tweet_date)

        retweetCountText = findViewById(R.id.retweet_count_text)
        likeCountText = findViewById(R.id.like_count_text)

        replyButtonIcon = findViewById(R.id.reply_button_icon)
        retweetButtonIcon = findViewById(R.id.retweet_button_icon)
        likeButtonIcon = findViewById(R.id.like_button_icon)
        shareButtonIcon = findViewById(R.id.share_button_icon)
        moreButton = findViewById(R.id.more_button)

        myProfileImage = findViewById(R.id.my_profile_image)
        replyInput = findViewById(R.id.reply_input)
        replySendButton = findViewById(R.id.reply_send_button)
        repliesRecyclerView = findViewById(R.id.replies_recycler_view)
    }

    private fun setupUI() {
        replyAdapter = TweetAdapter(
            tweets = mutableListOf(),
            currentUserId = currentUserId,
            onLikeClicked = { tweetId -> viewModel.toggleLikeStatus(tweetId) },
            onRetweetClicked = { tweetId -> viewModel.toggleRetweetStatus(tweetId) },
            onDeleteClicked = { tweetId -> viewModel.deleteTweet(tweetId) }
        )
        repliesRecyclerView.adapter = replyAdapter
        repliesRecyclerView.layoutManager = LinearLayoutManager(this)

        replyInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                replySendButton.isEnabled = !s.isNullOrBlank()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        replySendButton.setOnClickListener {
            val content = replyInput.text.toString()
            if (content.isNotBlank()) {
                viewModel.postReply(content)
                replyInput.text?.clear()
            }
        }

        moreButton.setOnClickListener { view ->
            showMenu(view)
        }
        shareButtonIcon.setOnClickListener { Toast.makeText(this, "Share clicked", Toast.LENGTH_SHORT).show() }
    }

    private fun showMenu(view: View) {
        val tweetUser = viewModel.tweet.value?.user ?: return

        if (tweetUser.userId == currentUserId) {
            val popup = PopupMenu(this, view)
            popup.menu.add("Delete Tweet")

            popup.setOnMenuItemClickListener { menuItem ->
                if (menuItem.title == "Delete Tweet") {
                    val tweetId = viewModel.tweet.value?.tweet?.tweetId
                    if (tweetId != null) {
                        viewModel.deleteTweet(tweetId)
                        finish()
                    }
                }
                true
            }
            popup.show()
        } else {
            Toast.makeText(this, "No actions available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewModel.tweet.observe(this) { tweetWithUser ->
            tweetWithUser?.let {
                val tweet = it.tweet
                val user = it.user

                displayName.text = user.displayName
                username.text = "@${user.username}"
                tweetContent.text = tweet.content

                val dateFormat = SimpleDateFormat("h:mm a · MMM d, yyyy", Locale.getDefault())
                tweetDate.text = dateFormat.format(System.currentTimeMillis())

                profileImage.load(user.profileImageUrl) {
                    placeholder(R.drawable.default_profile)
                    error(R.drawable.default_profile)
                }

                retweetCountText.text = tweet.retweetCount.toString()
                likeCountText.text = tweet.likeCount.toString()

                if (tweet.isLiked) {
                    likeButtonIcon.setImageResource(R.drawable.ic_liked)
                    likeButtonIcon.setColorFilter(ContextCompat.getColor(this, R.color.like_red))
                    likeCountText.setTextColor(ContextCompat.getColor(this, R.color.like_red))
                } else {
                    likeButtonIcon.setImageResource(R.drawable.ic_like)
                    likeButtonIcon.setColorFilter(ContextCompat.getColor(this, R.color.light_gray))
                    likeCountText.setTextColor(ContextCompat.getColor(this, R.color.light_gray))
                }

                val likeIcon = if (tweet.isLiked) R.drawable.ic_liked else R.drawable.ic_like
                likeButtonIcon.setImageResource(likeIcon)
                likeButtonIcon.setOnClickListener { viewModel.toggleLikeStatus(tweet.tweetId) }

                if (tweet.isRetweeted) {
                    val xBlue = ContextCompat.getColor(this, R.color.x_blue)
                    retweetButtonIcon.setColorFilter(xBlue)
                    retweetCountText.setTextColor(xBlue)
                } else {
                    val lightGray = ContextCompat.getColor(this, R.color.light_gray)
                    retweetButtonIcon.setColorFilter(lightGray)
                    retweetCountText.setTextColor(lightGray)
                }

                val retweetColor = if (tweet.isRetweeted) R.color.x_blue else R.color.light_gray
                retweetButtonIcon.setColorFilter(ContextCompat.getColor(this, retweetColor))
                retweetButtonIcon.setOnClickListener { viewModel.toggleRetweetStatus(tweet.tweetId) }
            }
        }

        viewModel.replies.observe(this) { replies ->
            replies?.let { replyAdapter.updateTweets(it) }
        }

        myProfileImage.load("https://via.placeholder.com/150") {
            placeholder(R.drawable.default_profile)
            error(R.drawable.default_profile)
            transformations(coil.transform.CircleCropTransformation())
        }
    }
}