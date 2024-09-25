package com.paytondeveloper.sharedqandroid.musicservices

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResourceCollectionResponse(
    val next: String,
    val data: List<Resource>
)

@Serializable
class Resource(
    val id: String,
    val type: String,
    var href: String,
    val relationships: Map<String, ResourceRelationship>
)



@Serializable
class ResourceRelationship(
    val href: String,
    val next: String,
    val data: Resource,
)

@Serializable
class SongsResponse(
    val data: List<Song>
)

@Serializable
class Song(
    val id: String,
    val type: String,
    val href: String,
    val attributes: SongAttributes
)

@Serializable
class SongAttributes(
    val albumName: String,
    val artistName: String,
    val artwork: Artwork,
    val durationInMillis: Int,
    val isrc: String,
    var name: String,
    val url: String
)

@Serializable
data class Artwork(
    var url: String
)