# Trackline Provider API

The Trackline Provider API is the Android library shared by the Trackline app and
its music providers. It defines playlist import, optional access management, and
playback for exact recordings. It contains contracts and models. The app and
concrete providers supply the implementations.

## Build and verify

Use the checked-in Gradle wrapper and the JDK specified by `toolchainVersion`
in the [Gradle daemon configuration](gradle/gradle-daemon-jvm.properties). Install
the Android SDK platform declared by `compileSdk` in the
[library build file](build.gradle.kts). The same file declares the minimum
supported device API level in `minSdk`. Point `JAVA_HOME` at the JDK. Set
`ANDROID_HOME` to the SDK location or set `sdk.dir` in an ignored
`local.properties`. The library uses Android types such as `Uri` and `Intent`.

Run the verification command from the root of this repository.

```sh
./gradlew check spotlessCheck lintRelease assembleDebug assembleRelease
```

This checks formatting, Android lint and compilation. Tests in the app and
provider repositories exercise behavior against the contract.

## Local publication

After verification, publish the release AAR, sources JAR, Maven POM and Gradle
module metadata to Maven local.

```sh
./gradlew publishToMavenLocal
```

The destination is normally `~/.m2/repository`. The [library build file](build.gradle.kts)
defines the version published by this checkout. Consumer builds use `mavenLocal()`
and declare the API as an ordinary dependency. Replace `<api-version>` with the
version supported by the host you target.

```kotlin
dependencies {
    api("io.github.eutera.trackline:trackline-provider-api:<api-version>")
}
```

Use `api` in a provider library that exposes the contract through its public
types. Consumers need the artifact and its dependency metadata, which includes
the coroutines API dependency. This build publishes to Maven local. You can pass
`-PsourceRepositoryUrl=<repository-url>` to record a source URL in the generated POM.

For the Trackline app, check the `provider-api` entry in `gradle/libs.versions.toml`
in the app checkout. It records the API version that build accepts. Coordinate
a host update before a provider requires an unsupported API version. API and
provider versions are independent. Preserve the meaning of IDs on existing
printed cards across releases.

## Implement a provider

The KDoc on these types defines the detailed contract.

| Entry point | Implementation responsibilities |
| --- | --- |
| [MusicProvider](src/main/java/io/github/eutera/trackline/provider/api/MusicProvider.kt) | Expose a public `Context` constructor, retain application context, and implement local reference validation. Supply the catalog, a playback gateway factory and any optional access flows or music credits. |
| [TrackRef and TrackSnapshot](src/main/java/io/github/eutera/trackline/provider/api/Track.kt) | Identify exact recordings with stable provider and track IDs. The host saves the provider ID in Settings and prints both IDs in QR cards. Leave an unknown release year null. |
| [CatalogRepository](src/main/java/io/github/eutera/trackline/provider/api/catalog/CatalogRepository.kt) and [catalog models](src/main/java/io/github/eutera/trackline/provider/api/catalog/Catalog.kt) | Validate input locally and load pages with main-safe, cancellable work. Start with a null cursor. Pass the opaque next cursor with the same input until the provider returns null. Return a revision when available. Report expected failures as `ProviderException` and propagate coroutine cancellation unchanged. |
| [ProviderAuthorization](src/main/java/io/github/eutera/trackline/provider/api/ProviderAuthorization.kt) and [CatalogAuthorization](src/main/java/io/github/eutera/trackline/provider/api/catalog/CatalogAuthorization.kt) | Provide the intent and handle the activity result. Catalog access also exposes import readiness and provides session cleanup. |
| [PlaybackGateway](src/main/java/io/github/eutera/trackline/provider/api/playback/PlaybackGateway.kt) | Use the main thread for calls and callbacks. Connect silently, select exact recordings and report observations separately from acknowledgments. Release pending work and suppress its callbacks after disconnect. |

The host cancels the supplied main-dispatcher scope when the session ends.
Implement gateway disconnect to release the engine and cancel outstanding work.

For a working example, see the separate `trackline-provider-demo` repository. The
`trackline-app` repository's `docs/PROVIDERS.md` covers adapter registration and
packaging. Its `docs/QR_FORMAT.md` documents printed-card compatibility.

## Provider authorization

The default catalog and playback authorization properties are null. Supply
`CatalogAuthorization` when importing needs access or setup, and update its
readiness as access changes. The user can reopen the flow to manage access.

When silent playback connection requires user access, report
`PlaybackFailure.AUTHORIZATION`. After the user's explicit **Connect** action,
the host can launch the provider's playback authorization and retry the silent
connection if the provider accepts the result. Automatic reconnects remain silent.

Use an explicit intent for a provider-owned activity and keep it private unless
another app needs to reach it. A provider can also return a system picker intent
and handle the URI grants in the result. The provider owns any browser redirects,
consent SDK calls or credentials.

## Contributing and license

See [repository guidelines](AGENTS.md) for contract changes and verification.
Project-owned code is licensed under [Apache-2.0](LICENSE). Third-party materials
retain their own licenses, and use of third-party services requires any applicable permissions.
