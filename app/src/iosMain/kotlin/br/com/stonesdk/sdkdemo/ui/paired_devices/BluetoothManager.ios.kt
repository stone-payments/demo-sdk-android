package br.com.stonesdk.sdkdemo.ui.paired_devices

import kotlinx.cinterop.objcPtr
import platform.CoreBluetooth.CBCentralManager
import platform.darwin.NSObject
import platform.CoreBluetooth.CBCentralManagerDelegateProtocol
import platform.CoreBluetooth.CBManagerStatePoweredOff
import platform.CoreBluetooth.CBManagerStatePoweredOn
import platform.CoreBluetooth.CBManagerAuthorizationAllowedAlways
import platform.CoreBluetooth.CBManagerAuthorizationDenied
import platform.CoreBluetooth.CBManagerAuthorizationNotDetermined
import platform.CoreBluetooth.CBManagerAuthorizationRestricted
import platform.CoreBluetooth.CBManagerStateResetting
import platform.CoreBluetooth.CBManagerStateUnauthorized
import platform.CoreBluetooth.CBManagerStateUnsupported
import platform.CoreBluetooth.CBPeripheral
import platform.CoreBluetooth.CBPeripheralManager
import platform.CoreBluetooth.CBPeripheralManagerDelegateProtocol
import platform.CoreBluetooth.CBPeripheralStateConnected
import platform.ExternalAccessory.EAAccessory
import platform.ExternalAccessory.EAAccessoryManager
import platform.ExternalAccessory.EAAccessoryManagerMeta
import platform.Foundation.NSNumber
import platform.Foundation.NSURL
import platform.UIKit.UIApplication


actual class BluetoothDevice: NSObject(), CBCentralManagerDelegateProtocol, CBPeripheralManagerDelegateProtocol {
    private lateinit var centralManager: CBCentralManager
    private val discoveredPeripherals : HashMap<String, CBPeripheral> = hashMapOf()

    override fun centralManagerDidUpdateState(central: CBCentralManager) {
        print(">>> centralManagerDidUpdateState")
        println(">>> state: ${central.state}")
        when(central.state){
            CBManagerStatePoweredOn -> {
                println(">>> CBManagerStatePoweredOn")
                println(">>> startScan is scanning = ${central.isScanning}")
                central.scanForPeripheralsWithServices(null, null)
            }
            CBManagerStatePoweredOff -> {
                println(">>> CBManagerStatePoweredOff")
            }
            CBManagerAuthorizationAllowedAlways -> {
                println(">>> CBManagerAuthorizationAllowedAlways")
            }
            CBManagerAuthorizationDenied ->{
                // Sem permissão no info.plist
                println(">>> CBManagerAuthorizationDenied")
            }
            CBManagerAuthorizationNotDetermined -> {
                println(">>> CBManagerAuthorizationNotDetermined")
            }

            CBManagerAuthorizationRestricted -> {
                println(">>> CBManagerAuthorizationRestricted")
            }
            CBManagerStateResetting -> {
                println(">>> CBManagerStateResetting")
            }
            CBManagerStateUnauthorized -> {
                println(">>> CBManagerStateUnauthorized")
            }

            CBManagerStateUnsupported -> {
                println(">>> CBManagerStateUnsupported")
            }

            else ->  {
                println(">>> else")
            }

        }
    }

    override fun peripheralManagerDidUpdateState(peripheral: CBPeripheralManager) {
        print(">>> peripheralManagerDidUpdateState")
        println(">>> state: ${peripheral.state}")

        when(peripheral.state) {
            CBManagerStatePoweredOn -> {
                println(">>> CBManagerStatePoweredOn")
            }

            CBManagerStatePoweredOff -> {
                println(">>> CBManagerStatePoweredOff")
            }

            CBManagerAuthorizationAllowedAlways -> {
                println(">>> CBManagerAuthorizationAllowedAlways")
            }

            CBManagerAuthorizationDenied -> {
                println(">>> CBManagerAuthorizationDenied")
            }

            CBManagerAuthorizationNotDetermined -> {
                println(">>> CBManagerAuthorizationNotDetermined")
            }

            CBManagerAuthorizationRestricted -> {
                println(">>> CBManagerAuthorizationRestricted")
            }

            CBManagerStateResetting -> {
                println(">>> CBManagerStateResetting")
            }

            CBManagerStateUnauthorized -> {
                println(">>> CBManagerStateUnauthorized")
            }

            CBManagerStateUnsupported -> {
                println(">>> CBManagerStateUnsupported")
            }

            else -> {
                println(">>> else")
            }
        }
    }

    actual fun startScan() {
        println(">>> startScan")
        centralManager = CBCentralManager(this, null)
        EAAccessoryManager.sharedAccessoryManager().connectedAccessories()
    }

    actual fun stopScan() {
        centralManager.stopScan()
    }

    actual fun connect(uuid : String){
        val peripheral = discoveredPeripherals[uuid]
        if(peripheral != null){
            centralManager.connectPeripheral(peripheral, null)
        }
    }



    override fun centralManager(
        central: CBCentralManager,
        didDiscoverPeripheral: CBPeripheral,
        advertisementData: Map<Any?, *>,
        RSSI: NSNumber
    ) {
        print(">>> ${centralManager.isScanning}")
        val peripheral = didDiscoverPeripheral
        discoveredPeripherals[peripheral.identifier.UUIDString] = peripheral
        println(">>> didDiscoverPeripheral: ${peripheral.name ?: "No name"} - ${peripheral.identifier} - connected ${peripheral.state == CBPeripheralStateConnected}" )
        println(">>> didDiscoverPeripheral: ${peripheral.name ?: "No name"} - ${peripheral.identifier}" )
    }


    override fun centralManager(central: CBCentralManager, didConnectPeripheral: CBPeripheral) {
        println(">>> didConnectPeripheral")
        println(">>> didConnectPeripheral: ${didConnectPeripheral.name ?: "No name"} - ${didConnectPeripheral.identifier}" )

    }

    fun openNSUrl(string: String) {
        val settingsUrl: NSURL = NSURL.URLWithString(string)!!
        if (UIApplication.sharedApplication.canOpenURL(settingsUrl)) {
            UIApplication.sharedApplication.openURL(settingsUrl)
        }
    }


}