package com.cinemateca.navigation

import kotlinx.serialization.Serializable

@Serializable
data class TrailerDetailsRoute(
    val movieId: String,
    val trailerId: String,
    val resourceType: String,
)
