package net.harutiro.nearbyconnectionsapitest

import android.app.Activity
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

interface NearbyRepositoryCallback {
    fun onConnectionStateChanged(state: String)
    fun onDataReceived(data: String)
}

class NearbyRepository(
    private val activity: Activity,
    private val callback: NearbyRepositoryCallback,
    private val nickName: String = "harutiro",
    private val serviceId: String = "net.harutiro.nearbyconnectionsapitest",
    private val strategy: Strategy = Strategy.P2P_STAR
) {
    private var remoteEndpointId: String? = null
    private val TAG = "NearbyRepository"

    fun startAdvertise() {
        Nearby.getConnectionsClient(activity)
            .startAdvertising(
                nickName,
                serviceId,
                connectionLifecycleCallback,
                AdvertisingOptions.Builder().setStrategy(strategy).build()
            )
            .addOnSuccessListener {
                callback.onConnectionStateChanged("広告開始")
            }
            .addOnFailureListener {
                callback.onConnectionStateChanged("広告失敗")
            }
    }

    fun startDiscovery() {
        Nearby.getConnectionsClient(activity)
            .startDiscovery(
                serviceId,
                endpointDiscoveryCallback,
                DiscoveryOptions.Builder().setStrategy(strategy).build()
            )
            .addOnSuccessListener {
                callback.onConnectionStateChanged("発見開始")
            }
            .addOnFailureListener {
                callback.onConnectionStateChanged("発見失敗")
            }
    }

    fun sendData(text: String) {
        val data = text.toByteArray()
        val payload = Payload.fromBytes(data)
        remoteEndpointId?.let {
            Nearby.getConnectionsClient(activity)
                .sendPayload(it, payload)
            callback.onConnectionStateChanged("データ送信")
        }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, discoveredEndpointInfo: DiscoveredEndpointInfo) {
            Nearby.getConnectionsClient(activity)
                .requestConnection(nickName, endpointId, connectionLifecycleCallback)
        }
        override fun onEndpointLost(endpointId: String) {}
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            Nearby.getConnectionsClient(activity)
                .acceptConnection(endpointId, payloadCallback)
        }
        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    remoteEndpointId = endpointId
                    callback.onConnectionStateChanged("接続成功")
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    remoteEndpointId = null
                    callback.onConnectionStateChanged("接続拒否")
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    remoteEndpointId = null
                    callback.onConnectionStateChanged("接続エラー")
                }
            }
        }
        override fun onDisconnected(endpointId: String) {
            remoteEndpointId = null
            callback.onConnectionStateChanged("切断")
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                val data = payload.asBytes()?.toString(Charsets.UTF_8) ?: ""
                callback.onDataReceived(data)
            }
        }
        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {}
    }
} 