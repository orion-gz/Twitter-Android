package orion.app.twitter.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton // [추가]
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import orion.app.twitter.R
import orion.app.twitter.ui.profile.ProfileActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var userAdapter: UserAdapter

    private lateinit var searchInput: EditText
    private lateinit var usersRecyclerView: RecyclerView
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val factory = SearchViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]

        bindViews()
        setupUI()
        observeViewModel()
        searchInput.requestFocus()
    }

    private fun bindViews() {
        searchInput = findViewById(R.id.search_input)
        usersRecyclerView = findViewById(R.id.users_recycler_view)
        backButton = findViewById(R.id.back_button)
    }

    private fun setupUI() {
        backButton.setOnClickListener { finish() }
        userAdapter = UserAdapter(mutableListOf()) { userId ->
            val intent = Intent(this, ProfileActivity::class.java).apply {
                putExtra("USER_ID", userId)
            }
            startActivity(intent)
        }

        usersRecyclerView.adapter = userAdapter
        usersRecyclerView.layoutManager = LinearLayoutManager(this)

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.isNotBlank()) {
                    viewModel.searchUsers(query)
                } else {
                    userAdapter.updateUsers(emptyList())
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun observeViewModel() {
        viewModel.users.observe(this) { users ->
            users?.let { userAdapter.updateUsers(it) }
        }
    }
}