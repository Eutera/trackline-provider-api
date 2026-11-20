package io.github.eutera.trackline.provider.api

/**
 * Portable identity of the exact recording represented by a QR card.
 *
 * @property provider Stable [MusicProvider.id] for the provider that owns the recording.
 * @property id Provider-specific recording ID matching `[A-Za-z0-9_-]{1,64}`.
 */
public data class TrackRef(public val provider: String, public val id: String)

/**
 * Catalog metadata for one recording.
 *
 * @property ref Identity used to select the exact recording for playback.
 * @property title Display title of the recording.
 * @property artists Display names of the credited recording artists.
 * @property releaseYear Year associated with the recording, or null when unknown.
 */
public data class TrackSnapshot(
    public val ref: TrackRef,
    public val title: String,
    public val artists: List<String>,
    public val releaseYear: Int? = null,
)
