package io.github.eutera.trackline.provider.api

import android.net.Uri
import io.github.eutera.trackline.provider.api.catalog.CatalogAuthorization
import io.github.eutera.trackline.provider.api.catalog.CatalogRepository
import io.github.eutera.trackline.provider.api.playback.PlaybackGateway
import kotlinx.coroutines.CoroutineScope

/**
 * Entry point for a provider's catalog, optional authorization, and playback.
 *
 * A host constructs the implementation through a public constructor taking an Android
 * [android.content.Context]. Use its application context for dependencies retained by the provider.
 */
public interface MusicProvider {
    /**
     * Provider ID matching `[a-z][a-z0-9-]{0,31}`. Hosts persist it and print it in QR cards, so it
     * must never change.
     */
    public val id: String

    /** Name shown when the host identifies this provider to the user. */
    public val displayName: String

    /** Music credits or a provider notice for the host to display, or null when absent. */
    public val musicCredits: MusicCredits?
        get() = null

    /** Link the host can open to install an external player, or null when unavailable. */
    public val installationUri: Uri?
        get() = null

    /** Playlist catalog exposed by this provider. */
    public val catalog: CatalogRepository

    /** Catalog access or setup when required, or null when import needs no setup. */
    public val authorization: CatalogAuthorization?
        get() = null

    /** Authorization to offer when an explicit playback connection requires user access. */
    public val playbackAuthorization: ProviderAuthorization?
        get() = null

    /**
     * Checks a reference against local rules, including provider identity and recording ID format.
     * This check does no network or playback work.
     */
    public fun accepts(reference: TrackRef): Boolean

    /**
     * Creates a playback gateway owned by the host's playback session. The supplied [scope] runs on
     * the main dispatcher and is cancelled when the session ends.
     */
    public fun createPlaybackGateway(scope: CoroutineScope): PlaybackGateway
}

/**
 * Music attribution and notices supplied by a provider.
 *
 * @property tracks Individual credits for bundled recordings, or an empty list for a notice alone.
 * @property notice Provider notice, additional attribution, or changes made to bundled recordings.
 */
public data class MusicCredits(public val tracks: List<MusicCredit>, public val notice: String)

/**
 * Attribution and license information for one bundled recording.
 *
 * @property title Display title of the recording.
 * @property attribution Credit text for the recording and its creator.
 * @property artistUrl Link to the credited artist.
 * @property sourceUrl Link to the original recording.
 * @property licenseName Display name of the recording's license.
 * @property licenseUrl Link to the license terms.
 */
public data class MusicCredit(
    public val title: String,
    public val attribution: String,
    public val artistUrl: String,
    public val sourceUrl: String,
    public val licenseName: String,
    public val licenseUrl: String,
)
