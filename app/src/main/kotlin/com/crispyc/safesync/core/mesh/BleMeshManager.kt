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

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            result.scanRecord?.serviceData?.get(ParcelUuid(MeshPacketProtocol.SERVICE_UUID))?.let {
                _incomingPackets.tryEmit(MeshPacketProtocol.MeshPacket.deserialize(it))
            }
        }
    }

    fun startScanning() {
        val scanFilter = ScanFilter.Builder().setServiceUuid(ParcelUuid(MeshPacketProtocol.SERVICE_UUID)).build()
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        bleScanner?.startScan(listOf(scanFilter), settings, scanCallback)
    }

    fun broadcastPacket(packet: MeshPacketProtocol.MeshPacket) {
        val settings = AdvertiseSettings.Builder().setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY).setConnectable(false).build()
        val data = AdvertiseData.Builder().addServiceUuid(ParcelUuid(MeshPacketProtocol.SERVICE_UUID))
            .addServiceData(ParcelUuid(MeshPacketProtocol.SERVICE_UUID), packet.serialize()).build()
        bleAdvertiser?.startAdvertising(settings, data, object : AdvertiseCallback() {})
    }
}
