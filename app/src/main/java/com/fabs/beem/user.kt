package com.fabs.beem

import java.time.Instant
import java.time.format.DateTimeFormatter

data class User(
    var id: String? = "",
    var name: String? = "",
    var email: String?,
    var password: String? = "",
    var created: String? = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
) {
    fun toHashMap(): HashMap<String, String?> {
        return hashMapOf(
            "name" to name,
            "email" to email,
            "create" to created
        )
    }
    companion object {
        fun fromHashMap(map: HashMap<String, String>): User {
            return User(map["id"], map["name"], map["email"])
        }
    }
}