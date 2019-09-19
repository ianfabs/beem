package com.fabs.beem.atv

data class Resource(
    val label: String,
    val uri: String? = null,
    val owner: String,
    val parent: String? = null,
    val created: String? = null
)