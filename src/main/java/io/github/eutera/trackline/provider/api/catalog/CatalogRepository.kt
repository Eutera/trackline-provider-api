package io.github.eutera.trackline.provider.api.catalog

/** Validates user input and reads playlist entries for one provider. */
public interface CatalogRepository {
    /**
     * Checks playlist input using local rules, without checking access or contacting the service.
     */
    public fun acceptsPlaylistInput(playlist: String): Boolean = playlist.isNotBlank()

    /**
     * Reads the first page for a null [cursor]. For subsequent pages, pass [CatalogPage.next]
     * unchanged with the same [playlist] input.
     *
     * When available, [CatalogPage.revision] identifies the playlist contents for the page. It
     * stays equal across pages from the same revision. A change between non-null revisions means
     * the host must treat the import as inconsistent. Return null if the service has no revision.
     *
     * Safe to call from the main thread. Move blocking work off that thread and propagate
     * [kotlinx.coroutines.CancellationException] unchanged.
     *
     * @throws ProviderException for catalog failures, including invalid input, network errors,
     *   access failures, and malformed responses.
     */
    public suspend fun page(playlist: String, cursor: String?): CatalogPage
}
