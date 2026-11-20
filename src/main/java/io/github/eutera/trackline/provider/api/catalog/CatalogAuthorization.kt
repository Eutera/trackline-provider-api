package io.github.eutera.trackline.provider.api.catalog

import io.github.eutera.trackline.provider.api.ProviderAuthorization
import kotlinx.coroutines.flow.StateFlow

/** Authorization or setup for a catalog that requires user access. */
public interface CatalogAuthorization : ProviderAuthorization {
    /** Whether the host can import from this catalog. */
    public val authorized: StateFlow<Boolean>

    /**
     * Ends the host's catalog session and clears any temporary credentials and pending requests.
     */
    public fun disconnect()
}
