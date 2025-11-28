package orion.app.twitter

import android.app.Activity
import android.content.Intent
import android.content.Intent.*
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import orion.app.twitter.data.network.TokenManager
import orion.app.twitter.ui.auth.LoginActivity
import orion.app.twitter.ui.auth.WelcomeActivity
import orion.app.twitter.ui.createtweet.CreateTweetActivity
import orion.app.twitter.ui.main.MainViewModel
import orion.app.twitter.ui.main.TweetAdapter
import orion.app.twitter.ui.main.MainViewModelFactory
import orion.app.twitter.ui.search.SearchActivity

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var tweetAdapter: TweetAdapter
    private lateinit var toolbarProfileImage: ShapeableImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private val createTweetLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.fetchTweetsAndUsers()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                Color.BLACK
            )
            ,navigationBarStyle = SystemBarStyle.dark(
                Color.BLACK
            )
        )
        setContentView(R.layout.activity_main)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val mainContent = findViewById<View>(R.id.main)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        ViewCompat.setOnApplyWindowInsetsListener(mainContent) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        val headerView = navigationView.getHeaderView(0)
        ViewCompat.setOnApplyWindowInsetsListener(headerView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                v.paddingLeft,
                systemBars.top + v.paddingTop,
                v.paddingRight,
                v.paddingBottom
            )
            insets
        }

        toolbarProfileImage = findViewById(R.id.toolbar_profile_image)

        toolbarProfileImage.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    val currentUserId = TokenManager(this).getUserId()
                    if (currentUserId != null) {
                        val intent = Intent(this, orion.app.twitter.ui.profile.ProfileActivity::class.java)
                        intent.putExtra("USER_ID", currentUserId)
                        startActivity(intent)
                    }
                }
                R.id.nav_logout -> {
                    showLogoutDialog()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val factory = MainViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        toolbarProfileImage = findViewById(R.id.toolbar_profile_image)
        toolbarProfileImage.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val tweetsRecyclerView: RecyclerView = findViewById(R.id.tweets_recycler_view)
        val currentUserId = TokenManager(this).getUserId()

        tweetAdapter = TweetAdapter(
            mutableListOf(),
            currentUserId,
            onLikeClicked = { tweetId -> viewModel.toggleLikeStatus(tweetId) },
            onRetweetClicked = { tweetId -> viewModel.toggleRetweetStatus(tweetId) },
            onDeleteClicked = { tweetId -> viewModel.deleteTweet(tweetId) }
        )
        tweetsRecyclerView.adapter = tweetAdapter
        tweetsRecyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.tweets.observe(this) { tweets ->
            tweets?.let { tweetAdapter.updateTweets(it) }
        }

        viewModel.currentUser.observe(this) { user ->
            user?.let {
                toolbarProfileImage.load(it.profileImageUrl) {
                    placeholder(R.drawable.ic_profile)
                    error(R.drawable.ic_profile)
                }

                val headerView = navigationView.getHeaderView(0)
                val headerImage = headerView.findViewById<ImageView>(R.id.header_profile_image)
                val headerName = headerView.findViewById<TextView>(R.id.header_display_name)
                val headerUsername = headerView.findViewById<TextView>(R.id.header_username)
                val headerFollowing = headerView.findViewById<TextView>(R.id.header_following_count)
                val headerFollower = headerView.findViewById<TextView>(R.id.header_follower_count)

                headerName.text = it.displayName
                headerUsername.text = "@${it.username}"
                headerFollowing.text = it.followingCount.toString()
                headerFollower.text = it.followerCount.toString()

                headerImage.load(it.profileImageUrl) {
                    placeholder(R.drawable.ic_profile)
                    error(R.drawable.ic_profile)
                }
            }
        }

        val fab: FloatingActionButton = findViewById(R.id.fab_new_tweet)
        fab.setOnClickListener {
            val intent = Intent(this, CreateTweetActivity::class.java)
            createTweetLauncher.launch(intent)
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    findViewById<RecyclerView>(R.id.tweets_recycler_view).smoothScrollToPosition(0)
                    viewModel.fetchTweetsAndUsers()
                    true
                }

                R.id.navigation_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    false
                }

                R.id.navigation_notifications -> {
                    false
                }

                R.id.navigation_messages -> {
                    false
                }

                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchTweetsAndUsers()
        val currentUserId = TokenManager(this).getUserId()
        if (currentUserId != null) {
            viewModel.fetchCurrentUser(currentUserId)
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Log out of X?")
            .setMessage("You can always log back in at any time.")
            .setPositiveButton("Log out") { _, _ ->
                TokenManager(this).clearToken()

                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}