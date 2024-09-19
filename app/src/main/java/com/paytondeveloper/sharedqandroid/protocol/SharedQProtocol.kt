package com.paytondeveloper.sharedqandroid.protocol

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Date
import java.util.UUID

@Serializable
data class SQGroup(
    val id: String,
    val name: String,
    val defaultPermissions: SQDefaultPermissions,
    val members: List<SQGroupMember> = emptyList(),
    val connectedMembers: List<SQUser> = emptyList(),
    val publicGroup: Boolean,
    val askToJoin: Boolean,
    val wsURL: String? = null,
    val currentlyPlaying: SQSong? = null,
    val previewQueue: List<SQQueueItem> = emptyList(),
    val playbackState: SQPlaybackState? = null,
    val groupURL: String? = null,
    val joinRequests: List<SQUser> = emptyList()
)
@Serializable
data class SQPlaybackState(
    val id: String = UUID.randomUUID().toString(),
    val state: PlayPauseState,
    val timestamp: Double
)

@Serializable
enum class PlayPauseState {
    PLAY, PAUSE
}
@Serializable
data class SQSong(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val artist: String,
    val albumArt: String? = null,
    val colors: List<String> = emptyList(),
    val textColor: String? = null,
    val duration: Double
)

@Serializable
data class SQDefaultPermissions(
    val id: String = UUID.randomUUID().toString(),
    val membersCanControlPlayback: Boolean,
    val membersCanAddToQueue: Boolean
)

@Serializable
data class SQUser(
    val id: String,
    val username: String,
    val email: String? = null,
    val groups: List<SQGroup> = listOf()
)

@Serializable
data class SQQueueItem(
    val id: String = UUID.randomUUID().toString(),
    val song: SQSong,
    val addedBy: String
)

@Serializable
data class SQGroupMember(
    val id: String,
    val user: SQUser,
    val canControlPlayback: Boolean,
    val canAddToQueue: Boolean,
    @Serializable(DateSerializer::class)
    val lastConnected: Date? = null,
    val isOwner: Boolean
) {
    constructor(
        id: String = UUID.randomUUID().toString(),
        user: SQUser,
        isOwner: Boolean,
        lastConnected: Date? = null,
        defaultPermissions: SQDefaultPermissions
    ) : this(
        id,
        user,
        defaultPermissions.membersCanControlPlayback,
        defaultPermissions.membersCanAddToQueue,
        lastConnected,
        isOwner
    )
}

@Serializable
data class JoinGroupRequest(
    val myUID: String,
    val groupID: String
)

@Serializable
data class WSMessage(
    val type: WSMessageType,
    val data: ByteArray, // Consider a more specific type if possible
    @Serializable(DateSerializer::class)
    val sentAt: Date
)
@Serializable
enum class WSMessageType {
    GROUP_UPDATE, NEXT_SONG, GO_BACK, PLAY, PAUSE, TIMESTAMP_UPDATE, PLAYBACK_STARTED, SEEK_TO, ADD_TO_QUEUE
}
@Serializable
data class WSPlaybackStartedMessage(
    @Serializable(DateSerializer::class)
    val startedAt: Date
)
@Serializable
data class WSTimestampUpdate(
    val timestamp: Double,
    @Serializable(DateSerializer::class)
    val sentAt: Date
)
@Serializable
data class FetchUserRequest(
    val uid: String
)
@Serializable
data class FetchGroupRequest(
    val myUID: String,
    val groupID: String
)
@Serializable
data class UpdateGroupRequest(
    val myUID: String,
    val group: SQGroup
)
@Serializable
data class AddGroupRequest(
    val myUID: String
)
@Serializable
data class NewSession(
    val token: String,
    val user: SQUser
)
@Serializable
data class UserSignup(
    val email: String,
    val username: String,
    val password: String
)

object DateSerializer : KSerializer<Date> {
    override val descriptor = PrimitiveSerialDescriptor("date", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: Date) = encoder.encodeLong(value.time)
    override fun deserialize(decoder: Decoder): Date = Date(decoder.decodeLong())
}