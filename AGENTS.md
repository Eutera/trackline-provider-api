# Trackline Provider API

Project mode: proper

## Contract boundary

- Keep this independent Android library limited to provider-neutral contracts and
  their models. It may depend on Android and coroutines for the exposed API.
  Application wiring, concrete adapters, SDKs, Compose UI and persistence live
  in consumer repositories.
- Keep Kotlin explicit API mode enabled. Use English KDoc for public behavior,
  ownership, failure modes, cancellation and threading. Match package names to
  directories and use explicit imports.
- Preserve stable provider and recording IDs and the meaning of existing
  printed cards. Preserve opaque pagination cursors, typed failures, cancellation
  and main-thread playback callbacks. Keep selection acknowledgments distinct
  from observed playback. Keep catalog and playback authorization independent.

## Verification and releases

- Run the [README verification command](README.md#build-and-verify). For a
  behavioral or signature change, verify affected app and provider consumers
  against the resulting AAR. Cover the changed behavior in the repository that
  owns the implementation.
- Use [local Maven publication](README.md#local-publication) with the generated
  dependency metadata. Consumers use the versioned API artifact. Keep API source
  and copies of its compiled classes out of provider publications.
- Review changes to public signatures or semantics with consumers. Coordinate
  the app's supported API version when compatibility changes. Keep published
  versions immutable. Point setup and compatibility documentation to the build
  settings that define versions.
- Package the root `LICENSE` in the AAR and the sources JAR. Preserve applicable
  upstream notices for any third-party material included in a distribution.
