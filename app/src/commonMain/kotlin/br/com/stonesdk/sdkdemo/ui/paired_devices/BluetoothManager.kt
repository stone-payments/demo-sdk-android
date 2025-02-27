package br.com.stonesdk.sdkdemo.ui.paired_devices

expect class BluetoothDevice() {
    fun startScan()
    fun stopScan()
    fun connect(uuid : String)
}