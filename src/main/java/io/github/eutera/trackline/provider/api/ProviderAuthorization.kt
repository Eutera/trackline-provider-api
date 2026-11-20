package io.github.eutera.trackline.provider.api

import android.content.Intent

/** A provider-owned authorization or access management flow launched by the host. */
public interface ProviderAuthorization {
    /** Whether the flow has the configuration it needs to run. */
    public val configured: Boolean
        get() = true

    /**
     * Creates an intent for an Android activity that returns a result. This can be a provider
     * activity or a system picker. The host calls this on the main thread after an explicit user
     * action and only when [configured] is true.
     */
    public fun createIntent(): Intent

    /**
     * Handles the Android activity result, including cancellation. Return true when access is ready
     * for the requested operation. The data intent can include URI grants and selected files. The
     * host can deliver a result after recreation, so restore any state needed to validate it or
     * reject it safely. Move blocking work off the main thread and propagate coroutine
     * cancellation.
     */
    public suspend fun complete(resultCode: Int, data: Intent?): Boolean
}
