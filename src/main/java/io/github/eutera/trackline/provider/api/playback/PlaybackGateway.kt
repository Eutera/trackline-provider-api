package io.github.eutera.trackline.provider.api.playback

import io.github.eutera.trackline.provider.api.TrackRef

/** Commands for the currently selected recording. */
public enum class PlaybackCommand {
    /** Starts or resumes the selected recording. */
    PLAY,

    /** Pauses playback. */
    PAUSE,
}

/** Failure reported while connecting, selecting a recording, or controlling playback. */
public enum class PlaybackFailure {
    /** The provider's external player is missing. */
    MISSING_APP,

    /** The user must sign in to the provider. */
    LOG_IN,

    /** The user must grant playback access or complete provider setup. */
    AUTHORIZATION,

    /** Required playback configuration is missing or invalid. */
    CONFIGURATION,

    /** Playback could not use a network connection. */
    OFFLINE,

    /** The playback connection is absent or was lost. */
    DISCONNECTED,

    /** Playback requires a provider or player update. */
    UPDATE_REQUIRED,

    /** The operation exceeded its time limit. */
    TIMEOUT,

    /** The provider could not classify the failure. */
    UNKNOWN,
}

/**
 * State observed from the underlying player.
 *
 * @property paused Whether the underlying player is paused.
 * @property reference Identity of the observed recording, or null when unknown or idle.
 * @property buffering Whether playback is waiting for media. It does not confirm active playback.
 */
public data class PlaybackSnapshot(
    public val paused: Boolean,
    public val reference: TrackRef? = null,
    public val buffering: Boolean = false,
)

/**
 * Connects to and controls a provider's player. Call all methods and deliver every callback on the
 * main thread. Hosts confirm playback from matching [Listener.onPlayback] observations. A
 * successful command acknowledgment alone does not confirm playback.
 */
public interface PlaybackGateway {
    /** Maximum time in milliseconds allowed for a [select] acknowledgment. */
    public val selectionTimeoutMs: Long
        get() = 10_000

    /** Whether the provider has the configuration required to connect for playback. */
    public val configured: Boolean
        get() = true

    /** Receives connection and player events for one gateway connection. */
    public interface Listener {
        /** Reports that the gateway can receive playback commands. */
        public fun onConnected()

        /**
         * Reports observed player state independently of command acknowledgments. Report buffering
         * via [PlaybackSnapshot.buffering].
         */
        public fun onPlayback(snapshot: PlaybackSnapshot)

        /** Reports an asynchronous connection or player failure. */
        public fun onFailure(failure: PlaybackFailure)
    }

    /**
     * Connects the gateway silently and sends its events to [listener]. Report
     * [PlaybackFailure.AUTHORIZATION] when user consent or setup is required.
     */
    public fun connect(listener: Listener)

    /**
     * Releases the connection and cancels pending work. After return, suppress all listener and
     * completion callbacks from the disconnected session.
     */
    public fun disconnect()

    /**
     * Requests playback of the exact [reference]. Within [selectionTimeoutMs], call [completed]
     * with null for acknowledgment or a failure, unless the request is superseded or disconnected.
     * Report the observed recording and its playback state separately to the listener.
     */
    public fun select(reference: TrackRef, completed: (PlaybackFailure?) -> Unit)

    /**
     * Requests a change to playback. Call [completed] with null for acknowledgment or a failure. A
     * connected idle player acknowledges [PlaybackCommand.PAUSE] without starting playback.
     */
    public fun command(command: PlaybackCommand, completed: (PlaybackFailure?) -> Unit)
}
