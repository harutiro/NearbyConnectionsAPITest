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
    var receivedDataList: List<Pair<String, String>> by mutableStateOf(emptyList())
        private set

    fun startAdvertise() = repository.startAdvertise()
    fun startDiscovery() = repository.startDiscovery()
    fun sendData(text: String) = repository.sendData(text)
    fun disconnectAll() = repository.disconnectAll()
    fun resetAll() {
        repository.resetAll()
        receivedDataList = emptyList()
    }

    override fun onConnectionStateChanged(state: String) {
        connectState = state
    }
    override fun onDataReceived(data: String, fromEndpointId: String) {
        receivedDataList = receivedDataList + (fromEndpointId to data)
    }
} 