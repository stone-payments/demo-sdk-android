package br.com.stonesdk.sdkdemo.ui.paired_devices

actual class BluetoothDevice {
    actual fun startScan() {
        println(">>> startScan")
    }

    actual fun stopScan() {
        println(">>> stopScan")
    }

    actual fun connect(uuid: String) {
        println(">>> connect")
    }
}