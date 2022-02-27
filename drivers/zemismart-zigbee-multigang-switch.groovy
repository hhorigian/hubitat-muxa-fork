/**
 *  Zemismart ZigBee Wall Switch Multi-Gang
 *  Device Driver for Hubitat Elevation hub
 *
 *  Based on Muxa's driver Version 0.2.0, last updated Feb 5, 2020 
 *
 *  Ver. 0.2.1 2022-02-26 kkossev - TuyaBlackMagic for TS0003 _TZ3000_vjhcenzo 
 *  Ver. 0.2.2 2022-02-27 kkossev - (development branch) 10:03 AM : TS0004 4-button, logEnable, txtEnable, ping(), intercept cluster: E000 attrId: D001 and D002 exceptions
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */

import hubitat.device.HubAction
import hubitat.device.Protocol

def version() { "0.2.2" }
def timeStamp() {"2022/02/27 10:21 AM"}

metadata {
    definition (name: "Zemismart ZigBee Wall Switch Multi-Gang", namespace: "muxa", author: "Muxa") {
        capability "Initialize"
        capability "Actuator"
        capability "Configuration"
        capability "Refresh"
        capability "Switch"
        capability "Health Check"
 
        // fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006", outClusters: "0019", manufacturer: "Zemismart", model: "TS0002", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"  
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0003,0004,0005,0006,E000,E001,0000", outClusters:"0019,000A", model:"TS0004", manufacturer:"_TZ3000_excgg5kb"     // 4-relays module
        
        attribute "lastCheckin", "string"    
    }
    preferences {
        input (name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: true)
        input (name: "txtEnable", type: "bool", title: "Enable description text logging", defaultValue: true)
    }
 
}


// Parse incoming device messages to generate events

def parse(String description) {
   checkDriverVersion()
   //log.debug "Parsing '${description}'"
   def descMap = [:] 
   try {
       descMap = zigbee.parseDescriptionAsMap(description)
   }
   catch ( e ) {
       if (settings?.logEnable) log.warn "exception caught while parsing description ${description} \r descMap:  ${descMap}"
       return null
   }    
   logDebug "Parsed: $descMap"
    
   Map map = null // [:]
        
   if (descMap.cluster == "0006" && descMap.attrId == "0000") {
       // descMap.command =="0A" - switch toggled physically
       // descMap.command =="01" - get switch status
       def cd = getChildDevice("${device.id}-${descMap.endpoint}")
       if (cd == null) {
           log.warn "Child device ${device.id}-${descMap.endpoint} not found. Initialise parent device first"
           return
       }
       def switchAttribute = descMap.value == "01" ? "on" : "off"
       if (descMap.command =="0A") {
           // switch toggled
           cd.parse([[name: "switch", value:switchAttribute, descriptionText: "Child switch ${descMap.endpoint} turned $switchAttribute"]])
       } else if (descMap.command =="01") {
           // report switch status
           cd.parse([[name: "switch", value:switchAttribute, descriptionText: "Child switch  ${descMap.endpoint} is $switchAttribute"]])
       }
       
       if (switchAttribute == "on") {
           logDebug "Parent switch on"
           return createEvent(name: "switch", value: "on")
       } else if (switchAttribute == "off") {
            def cdsOn = 0
            // cound number of switches on
            getChildDevices().each {child ->
                if (getChildId(child) != descMap.endpoint && child.currentValue('switch') == "on") {
                    cdsOn++
                }
            }
            if (cdsOn == 0) {
                logDebug "Parent switch off"
                return createEvent(name: "switch", value: "off")
            }
       }
   }
}

def off() {
    if (settings?.txtEnable) log.info "Turn all child switches off"	
    "he cmd 0x${device.deviceNetworkId} 0xFF 0x0006 0x0 {}"
}

def on() {
    if (settings?.txtEnable) log.info "Turn all child switches on"
    "he cmd 0x${device.deviceNetworkId} 0xFF 0x0006 0x1 {}"
}

def refresh() {
	logDebug "refreshing"
    "he rattr 0x${device.deviceNetworkId} 0xFF 0x0006 0x0"
}

def ping() {
    refresh()
}

private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}

private String getChildId(childDevice) {
    return childDevice.deviceNetworkId.substring(childDevice.deviceNetworkId.length() - 2)
}

def componentOn(childDevice) {
    logDebug "componentOn ${childDevice.deviceNetworkId}"
    sendHubCommand(new HubAction("he cmd 0x${device.deviceNetworkId} 0x${getChildId(childDevice)} 0x0006 0x1 {}", Protocol.ZIGBEE))
}

def componentOff(childDevice) {
    logDebug "componentOff ${childDevice.deviceNetworkId}"
    sendHubCommand(new HubAction("he cmd 0x${device.deviceNetworkId} 0x${getChildId(childDevice)} 0x0006 0x0 {}", Protocol.ZIGBEE))
}

def componentRefresh(childDevice) {
    logDebug "componentRefresh ${childDevice.deviceNetworkId} ${childDevice}"    
    sendHubCommand(new HubAction("he rattr 0x${device.deviceNetworkId} 0x${getChildId(childDevice)} 0x0006 0x0", Protocol.ZIGBEE))
}

def setupChildDevices() {
    logDebug "Parent setupChildDevices"
    deleteObsoleteChildren()    
    def buttons = 0
    switch (device.data.model) {
        case 'TS0004':
            buttons = 4
            break
        case 'TS0003':
            buttons = 3
            break
        case 'TS0002':
            buttons = 2
            break
        default :
            break
    }
    logDebug "model: ${device.data.model} buttons: $buttons"
    createChildDevices((int)buttons)
}

def createChildDevices(int buttons) {
    logDebug "Parent createChildDevices"
    
    if (buttons == 0)
        return            
    
    for (i in 1..buttons) {
        def childId = "${device.id}-0${i}"
        def existingChild = getChildDevices()?.find { it.deviceNetworkId == childId}
    
        if (existingChild) {
            log.info "Child device ${childId} already exists (${existingChild})"
        } else {
            log.info "Creatung device ${childId}"
            addChildDevice("hubitat", "Generic Component Switch", childId, [isComponent: true, name: "Switch EP0${i}", label: "${device.displayName} EP0${i}"])
        }
    }
}

def deleteObsoleteChildren() {
	logDebug "Parent deleteChildren"
    
    getChildDevices().each {child->
        if (!child.deviceNetworkId.startsWith(device.id) || child.deviceNetworkId == "${device.id}-00") {
            log.info "Deleting ${child.deviceNetworkId}"
  		    deleteChildDevice(child.deviceNetworkId)
        }
    }
}

def driverVersionAndTimeStamp() {version()+' '+timeStamp()}

def checkDriverVersion() {
    if (state.driverVersion == null || (driverVersionAndTimeStamp() != state.driverVersion)) {
        if (txtEnable==true) log.debug "${device.displayName} updating the settings from the current driver version ${state.driverVersion} to the new version ${driverVersionAndTimeStamp()}"
        initializeVars( fullInit = false ) 
        state.driverVersion = driverVersionAndTimeStamp()
    }
}

void initializeVars(boolean fullInit = true) {
    if (settings?.txtEnable) log.info "${device.displayName} InitializeVars()... fullInit = ${fullInit}"
    if (fullInit == true ) {
        state.clear()
        state.driverVersion = driverVersionAndTimeStamp()
    }
    if (fullInit == true || device.getDataValue("logEnable") == null) device.updateSetting("logEnable", true)
    if (fullInit == true || device.getDataValue("txtEnable") == null) device.updateSetting("txtEnable", true)    
}

def initialize() {
    logDebug "Initializing..."
    initializeVars(fullInit = true) 
    setupChildDevices()
}

def installed() {
    logDebug "Parent installed"
}

def updated() {
    logDebug "Parent updated"
}


def tuyaBlackMagic() {
    return zigbee.readAttribute(0x0000, [0x0004, 0x000, 0x0001, 0x0005, 0x0007, 0xfffe], [:], delay=200)    // Cluster: Basic, attributes: Man.name, ZLC ver, App ver, Model Id, Power Source, attributeReportingStatus
}

def configure() {
    logDebug " configure().."
    List<String> cmds = []
    cmds += tuyaBlackMagic()
    cmds += refresh()
    cmds += zigbee.onOffConfig()
    sendZigbeeCommands(cmds)
}

void sendZigbeeCommands(List<String> cmds) {
    logDebug"${device.displayName} sendZigbeeCommands received : ${cmds}"
	sendHubCommand(new hubitat.device.HubMultiAction(cmds, hubitat.device.Protocol.ZIGBEE))
}


def logDebug(msg) {
    if (settings?.logEnable) log.debug msg
}