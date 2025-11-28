package orion.app.twitter.util

import java.text.SimpleDateFormat
import java.util.Locale

object DateUtils {
    fun formatTimestamp(timestamp: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            val formatter = SimpleDateFormat("h:mm a · MMM d, yyyy", Locale.US)
            parser.parse(timestamp)?.let { formatter.format(it) } ?: timestamp
        } catch (e: Exception) {
            timestamp // Return original if parsing fails
        }
    }
}
