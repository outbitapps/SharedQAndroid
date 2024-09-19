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
    val state: Int,
    val timestamp: Double
)

@Serializable
enum class PlayPauseState {
    PLAY, PAUSE;
    companion object {
        fun fromInt(int: Int): PlayPauseState {
            if (int == 0) {
                return PLAY
            } else {
                return PAUSE
            }
        }
    }
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
    val type: Int,
    val data: String, // Consider a more specific type if possible
    val sentAt: Double
)
@Serializable
enum class WSMessageType {
    GROUP_UPDATE, NEXT_SONG, GO_BACK, PLAY, PAUSE, TIMESTAMP_UPDATE, PLAYBACK_STARTED, SEEK_TO, ADD_TO_QUEUE, UNKNOWN;
    companion object  {
        fun fromInt(int: Int): WSMessageType {
            return when (int) {
                0 -> WSMessageType.GROUP_UPDATE
                1 -> WSMessageType.NEXT_SONG
                2 -> WSMessageType.GO_BACK
                3 -> WSMessageType.PLAY
                4 -> WSMessageType.PAUSE
                5 -> WSMessageType.TIMESTAMP_UPDATE
                6 -> WSMessageType.PLAYBACK_STARTED
                7 -> WSMessageType.SEEK_TO
                8 -> WSMessageType.ADD_TO_QUEUE
                else -> WSMessageType.UNKNOWN
            }
        }
    }
}
@Serializable
data class WSPlaybackStartedMessage(
    val startedAt: Double
)
@Serializable
data class WSTimestampUpdate(
    val timestamp: Double,
    val sentAt: Double
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