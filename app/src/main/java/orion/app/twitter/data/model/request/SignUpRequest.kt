package orion.app.twitter.data.model.request

data class SignUpRequest(
    val username: String,
    val displayName: String,
    val email: String,
    val password: String
)
