package orion.app.twitter.ui.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.imageview.ShapeableImageView
import orion.app.twitter.R
import orion.app.twitter.data.model.TweetWithUser
import orion.app.twitter.ui.detail.TweetDetailActivity
import orion.app.twitter.ui.profile.ProfileActivity

class TweetAdapter(
    private val tweets: MutableList<TweetWithUser>,
    private val currentUserId: String?,
    private val onLikeClicked: (String) -> Unit,
    private val onRetweetClicked: (String) -> Unit,
    private val onDeleteClicked: (String) -> Unit
) : RecyclerView.Adapter<TweetAdapter.TweetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tweet_item, parent, false)
        return TweetViewHolder(view)
    }

    override fun onBindViewHolder(holder: TweetViewHolder, position: Int) {
        val tweetWithUser = tweets[position]
        val tweet = tweetWithUser.tweet
        val user = tweetWithUser.user

        holder.displayName.text = user.displayName
        holder.username.text = "@${user.username}"
        holder.tweetContent.text = tweet.content

        holder.profileImage.load(user.profileImageUrl) {
            placeholder(R.drawable.default_profile)
            error(R.drawable.default_profile)
        }

        holder.likeCount.text = tweet.likeCount.toString()
        val likeIcon = if (tweet.isLiked) R.drawable.ic_liked else R.drawable.ic_like
        holder.likeButton.setImageResource(likeIcon)

        if (tweet.isLiked) {
            holder.likeButton.setImageResource(R.drawable.ic_liked)
            holder.likeButton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.like_red))
            holder.likeCount.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.like_red))
        } else {
            holder.likeButton.setImageResource(R.drawable.ic_like)
            holder.likeButton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.light_gray))
            holder.likeCount.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_gray))
        }

        if (tweet.isRetweeted) {
            val xBlue = ContextCompat.getColor(holder.itemView.context, R.color.x_blue)
            holder.retweetButton.setColorFilter(xBlue)
            holder.retweetCount.setTextColor(xBlue)
        } else {
            val lightGray = ContextCompat.getColor(holder.itemView.context, R.color.light_gray)
            holder.retweetButton.setColorFilter(lightGray)
            holder.retweetCount.setTextColor(lightGray)
        }
        holder.retweetCount.text = tweet.retweetCount.toString()

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TweetDetailActivity::class.java).apply {
                putExtra("TWEET_ID", tweet.tweetId)
            }
            context.startActivity(intent)
        }

        holder.profileImage.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ProfileActivity::class.java).apply {
                putExtra("USER_ID", user.userId)
            }
            context.startActivity(intent)
        }

        holder.likeButton.setOnClickListener { onLikeClicked(tweet.tweetId) }
        holder.retweetButton.setOnClickListener { onRetweetClicked(tweet.tweetId) }

        holder.replyButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TweetDetailActivity::class.java).apply {
                putExtra("TWEET_ID", tweet.tweetId)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = tweets.size

    fun updateTweets(newTweets: List<TweetWithUser>) {
        tweets.clear()
        tweets.addAll(newTweets)
        notifyDataSetChanged()
    }

    class TweetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ShapeableImageView = itemView.findViewById(R.id.profile_image)
        val displayName: TextView = itemView.findViewById(R.id.display_name)
        val username: TextView = itemView.findViewById(R.id.username)
        val tweetContent: TextView = itemView.findViewById(R.id.tweet_content)
        val replyButton: ImageButton = itemView.findViewById(R.id.reply_button)
        val retweetButton: ImageButton = itemView.findViewById(R.id.retweet_button)
        val retweetCount: TextView = itemView.findViewById(R.id.retweet_count)
        val likeButton: ImageButton = itemView.findViewById(R.id.like_button)
        val likeCount: TextView = itemView.findViewById(R.id.like_count)
    }
}