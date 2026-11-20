package io.github.eutera.trackline.provider.api.catalog

import io.github.eutera.trackline.provider.api.TrackSnapshot

/** One playlist entry, represented by a playable recording or a reason it was skipped. */
public sealed interface CatalogItem {
    /**
     * A recording available to import.
     *
     * @property snapshot Catalog metadata and the exact playback identity.
     */
    public data class Track(public val snapshot: TrackSnapshot) : CatalogItem

    /**
     * A playlist entry that cannot be imported.
     *
     * @property reason Why the entry was skipped.
     */
    public data class Skipped(public val reason: SkipReason) : CatalogItem
}

/**
 * One page of entries in playlist order.
 *
 * @property items Entries read from this page, including any skipped entries.
 * @property next Opaque continuation cursor, or null at the end of the playlist.
 * @property revision Opaque playlist revision, or null when the provider cannot report one.
 * @property playlistName Playlist name when available, or null when unknown or omitted.
 */
public data class CatalogPage(
    public val items: List<CatalogItem>,
    public val next: String?,
    public val revision: String?,
    public val playlistName: String? = null,
)

/** Reason an individual entry cannot be imported. */
public enum class SkipReason {
    /** The service did not supply the entry or the required metadata. */
    MISSING,

    /** The entry is a local file. */
    LOCAL_FILE,

    /** The entry is a media type this adapter does not import. */
    UNSUPPORTED_MEDIA,

    /** The service reports that the recording cannot be played. */
    UNPLAYABLE,

    /** The recording ID cannot be represented by a valid provider reference. */
    INVALID_REFERENCE,
}

/** Catalog failure categories used by hosts to present feedback and decide whether to retry. */
public enum class ProviderError {
    /** Catalog authorization is missing, has expired, or was rejected. */
    AUTHORIZATION,

    /** The service denies access to the requested catalog resource. */
    PERMISSION,

    /** A network connection to the service could not be used. */
    OFFLINE,

    /** The service is throttling requests. */
    RATE_LIMIT,

    /** The requested resource is unavailable or its response cannot be interpreted. */
    UNAVAILABLE,

    /** The provider has lost a connection required for the catalog operation. */
    DISCONNECTED,

    /** The requested catalog input or operation is unsupported. */
    UNSUPPORTED,

    /** A transient failure prevented a consistent catalog result. */
    TEMPORARY,
}

/**
 * Typed catalog failure. Hosts use [category] for user-facing feedback.
 *
 * @property category Reason the operation failed.
 * @property retryAfterMs When rate limited, the service's minimum delay before a retry in
 *   milliseconds, or null if no delay was supplied.
 */
public class ProviderException(
    public val category: ProviderError,
    public val retryAfterMs: Long? = null,
) : Exception(category.name)
