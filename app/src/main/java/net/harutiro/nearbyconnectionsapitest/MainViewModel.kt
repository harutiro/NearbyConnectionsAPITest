package net.harutiro.nearbyconnectionsapitest

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import net.harutiro.nearbyconnectionsapitest.NearbyRepositoryCallback

class MainViewModel(activity: Activity) : ViewModel(), NearbyRepositoryCallback {
    private val repository = NearbyRepository(activity, this)

    var connectState: String by mutableStateOf("")
        private set
    var receivedData: String by mutableStateOf("")
        private set

    fun startAdvertise() = repository.startAdvertise()
    fun startDiscovery() = repository.startDiscovery()
    fun sendData(text: String) = repository.sendData(text)

    override fun onConnectionStateChanged(state: String) {
        connectState = state
    }
    override fun onDataReceived(data: String) {
        receivedData = data
    }
} 