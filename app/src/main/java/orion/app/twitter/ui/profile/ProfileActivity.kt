package orion.app.twitter.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.imageview.ShapeableImageView
import orion.app.twitter.R
import orion.app.twitter.ui.main.TweetAdapter
import orion.app.twitter.util.DateUtils

class ProfileActivity : AppCompatActivity() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var tweetAdapter: TweetAdapter

    private var currentTab = 0
    private lateinit var profileImage: ShapeableImageView
    private lateinit var profileBanner: ImageView
    private lateinit var displayName: TextView
    private lateinit var username: TextView
    private lateinit var bio: TextView
    private lateinit var followButton: Button
    private lateinit var tweetsRecyclerView: RecyclerView

    private lateinit var followingCount: TextView
    private lateinit var followerCount: TextView

    private lateinit var tabTweets: LinearLayout
    private lateinit var tabReplies: LinearLayout
    private lateinit var tabLikes: LinearLayout
    private lateinit var textTweets: TextView
    private lateinit var textReplies: TextView
    private lateinit var textLikes: TextView
    private lateinit var indicatorTweets: View
    private lateinit var indicatorReplies: View
    private lateinit var indicatorLikes: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { finish() }

        val userId = intent.getStringExtra("USER_ID")
        if (userId == null) {
            finish()
            return
        }

        val factory = ProfileViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        bindViews()
        setupUI()
        observeViewModel()

        viewModel.fetchProfileData(userId)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadTabContent(currentTab)
    }
    private fun bindViews() {
        profileImage = findViewById(R.id.profile_image)
        profileBanner = findViewById(R.id.profile_banner)
        displayName = findViewById(R.id.display_name)
        username = findViewById(R.id.username)
        bio = findViewById(R.id.bio)
        followButton = findViewById(R.id.follow_button)
        tweetsRecyclerView = findViewById(R.id.user_tweets_recycler_view)
        followingCount = findViewById(R.id.following_count)
        followerCount = findViewById(R.id.follower_count)
        tabTweets = findViewById(R.id.tab_tweets)
        tabReplies = findViewById(R.id.tab_replies)
        tabLikes = findViewById(R.id.tab_likes)

        textTweets = findViewById(R.id.text_tweets)
        textReplies = findViewById(R.id.text_replies)
        textLikes = findViewById(R.id.text_likes)

        indicatorTweets = findViewById(R.id.indicator_tweets)
        indicatorReplies = findViewById(R.id.indicator_replies)
        indicatorLikes = findViewById(R.id.indicator_likes)
    }

    private fun setupUI() {
        val currentUserId = orion.app.twitter.data.network.TokenManager(this).getUserId()
        tweetAdapter = TweetAdapter(
            tweets = mutableListOf(),
            currentUserId = currentUserId, // [추가] 어댑터에 ID 전달
            onLikeClicked = { tweetId -> viewModel.toggleLikeStatus(tweetId) },
            onRetweetClicked = { tweetId -> viewModel.toggleRetweetStatus(tweetId) },
            onDeleteClicked = { tweetId -> viewModel.deleteTweet(tweetId) }
        )
        tweetsRecyclerView.adapter = tweetAdapter
        tweetsRecyclerView.layoutManager = LinearLayoutManager(this)

        followButton.setOnClickListener {
            viewModel.toggleFollowStatus()
        }

        tabTweets.setOnClickListener { selectTab(0) }
        tabReplies.setOnClickListener { selectTab(1) }
        tabLikes.setOnClickListener { selectTab(2) }
    }

    private fun selectTab(index: Int) {
        currentTab = index
        resetTabs()
        when (index) {
            0 -> {
                textTweets.setTextColor(Color.WHITE)
                indicatorTweets.visibility = View.VISIBLE
            }
            1 -> {
                textReplies.setTextColor(Color.WHITE)
                indicatorReplies.visibility = View.VISIBLE
            }
            2 -> {
                textLikes.setTextColor(Color.WHITE)
                indicatorLikes.visibility = View.VISIBLE
            }
        }

        viewModel.loadTabContent(index)
    }

    private fun resetTabs() {
        val grayColor = ContextCompat.getColor(this, R.color.light_gray)
        textTweets.setTextColor(grayColor)
        textReplies.setTextColor(grayColor)
        textLikes.setTextColor(grayColor)

        indicatorTweets.visibility = View.INVISIBLE
        indicatorReplies.visibility = View.INVISIBLE
        indicatorLikes.visibility = View.INVISIBLE
    }

    private fun observeViewModel() {
        viewModel.user.observe(this) { user ->
            user?.let {
                displayName.text = it.displayName
                username.text = "@${it.username}"
                bio.text = it.bio
                followButton.text = if (it.isFollowing) "Following" else "Follow"
                followingCount.text = it.followingCount.toString()
                followerCount.text = it.followerCount.toString()

                profileImage.load(it.profileImageUrl) {
                    placeholder(R.drawable.default_profile)
                    error(R.drawable.default_profile)
                }
                profileBanner.load(it.profileBannerUrl) {
                    placeholder(R.color.dark_gray)
                    error(R.color.dark_gray)
                }
            }
        }

        viewModel.tweets.observe(this) { tweets ->
            tweets?.let { tweetAdapter.updateTweets(it) }
        }
    }
}
