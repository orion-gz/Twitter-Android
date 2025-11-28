package orion.app.twitter.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.imageview.ShapeableImageView
import orion.app.twitter.R
import orion.app.twitter.data.model.User

class UserAdapter(
    private val users: MutableList<User>,
    private val onUserClicked: (String) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]

        holder.displayName.text = user.displayName
        holder.username.text = "@${user.username}"

        if (!user.bio.isNullOrBlank()) {
            holder.bio.visibility = View.VISIBLE
            holder.bio.text = user.bio
        } else {
            holder.bio.visibility = View.GONE
        }

        holder.profileImage.load(user.profileImageUrl) {
            placeholder(R.drawable.default_profile)
            error(R.drawable.default_profile)
        }

        holder.itemView.setOnClickListener {
            onUserClicked(user.userId)
        }
    }

    override fun getItemCount() = users.size

    fun updateUsers(newUsers: List<User>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ShapeableImageView = itemView.findViewById(R.id.profile_image)
        val displayName: TextView = itemView.findViewById(R.id.display_name)
        val username: TextView = itemView.findViewById(R.id.username)
        val bio: TextView = itemView.findViewById(R.id.bio)
    }
}