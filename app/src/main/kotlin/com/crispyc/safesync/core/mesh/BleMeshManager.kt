package com.crispyc.safesync.core.mesh

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@SuppressLint("MissingPermission")
@Singleton
class BleMeshManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private val bleScanner = bluetoothAdapter?.bluetoothLeScanner
    private val bleAdvertiser = bluetoothAdapter?.bluetoothLeAdvertiser

    private val _incomingPackets = MutableSharedFlow<MeshPacketProtocol.MeshPacket>(extraBufferCapacity = 10)
    val incomingPackets = _incomingPackets.asSharedFlow()

    private var isScanning = false
    private var isAdvertising = false

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            result.scanRecord?.getServiceData(ParcelUuid(MeshPacketProtocol.SERVICE_UUID))?.let {
                try {
                    val packet = MeshPacketProtocol.MeshPacket.deserialize(it)
                    _incomingPackets.tryEmit(packet)
                } catch (e: Exception) {
                    Log.e("BleMeshManager", "Failed to deserialize packet: ${e.message}")
                }
            }
        }
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            super.onStartSuccess(settingsInEffect)
            isAdvertising = true
            Log.d("BleMeshManager", "BLE Advertising started successfully")
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            isAdvertising = false
            Log.e("BleMeshManager", "BLE Advertising failed with error code: $errorCode")
        }
    }

    fun startScanning() {
        if (isScanning) return
        val scanFilter = ScanFilter.Builder().setServiceUuid(ParcelUuid(MeshPacketProtocol.SERVICE_UUID)).build()
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        bleScanner?.startScan(listOf(scanFilter), settings, scanCallback)
        isScanning = true
    }

    fun stopScanning() {
        if (!isScanning) return
        bleScanner?.stopScan(scanCallback)
        isScanning = false
    }

    fun broadcastPacket(packet: MeshPacketProtocol.MeshPacket) {
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setConnectable(false)
            .setTimeout(0) // Advertise until stopped
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .build()
        
        val data = AdvertiseData.Builder()
            .addServiceUuid(ParcelUuid(MeshPacketProtocol.SERVICE_UUID))
            .addServiceData(ParcelUuid(MeshPacketProtocol.SERVICE_UUID), packet.serialize())
            .build()
            
        bleAdvertiser?.stopAdvertising(advertiseCallback)
        bleAdvertiser?.startAdvertising(settings, data, advertiseCallback)
    }

    fun stopAdvertising() {
        bleAdvertiser?.stopAdvertising(advertiseCallback)
        isAdvertising = false
    }
}
