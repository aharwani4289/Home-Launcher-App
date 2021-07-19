package com.ajayh.homelauncherapp.sdk.app.model

import android.content.Intent

typealias Action = () -> Unit

abstract class IntentAction(private val intent: Intent) : Action {

    private val intentUri by lazy(LazyThreadSafetyMode.NONE) {
        intent.toUri(0)
    }

    override fun toString(): String {
        return intentUri
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IntentAction

        if (intentUri != other.intentUri) return false

        return true
    }

    override fun hashCode(): Int {
        return intentUri.hashCode()
    }
}