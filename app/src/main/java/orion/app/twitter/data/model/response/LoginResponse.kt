package orion.app.twitter.data.model.response

import orion.app.twitter.data.model.User

data class LoginResponse(
    val user: User,
    val token: String
)
