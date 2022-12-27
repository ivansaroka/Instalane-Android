package com.infinix.instalane.data

class SingletonNotification private constructor() {

    var onNotificationReceived : (() -> Unit?)? =null

    companion object {
        val instance = SingletonNotification()
    }
}