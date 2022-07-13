/**
 *  Zemismart ZigBee Wall Switch Multi-Gang
 *  Device Driver for Hubitat Elevation hub
 *
 *  Based on Muxa's driver Version 0.2.0, last updated Feb 5, 2020 
 *
 *  Ver. 0.0.1 2019-08-21 Muxa    - first version
 *  Ver. 0.1.0 2020-02-05 Muxa    - Driver name "Zemismart ZigBee Wall Switch Multi-Gang"
 *  Ver. 0.2.1 2022-02-26 kkossev - TuyaBlackMagic for TS0003 _TZ3000_vjhcenzo 
 *  Ver. 0.2.2 2022-02-27 kkossev - TS0004 4-button, logEnable, txtEnable, ping(), intercept cluster: E000 attrId: D001 and D002 exceptions;
 *  Ver. 0.2.3 2022-03-04 kkossev - powerOnState options
 *  Ver. 0.2.4 2022-04-16 kkossev - _TZ3000_w58g68s3 Yagusmart 3 gang zigbee switch fingerprint
 *  Ver. 0.2.5 2022-05-28 kkossev - _TYZB01_Lrjzz1UV Zemismart 3 gang zigbee switch fingerprint; added TS0011 TS0012 TS0013 models and fingerprints; more TS002, TS003, TS004 manufacturers
 *  Ver. 0.2.6 2022-06-03 kkossev -  powerOnState and Debug logs improvements; importUrl; singleThreaded
 *  Ver. 0.2.7 2022-06-06 kkossev -  command '0B' (command response) bug fix; added Tuya Zugbee mini switch TMZ02L (_TZ3000_txpirhfq); bug fix for TS0011 single-gang switches.
 *  Ver. 0.2.8 2022-07-13 kkossev -  (dev branch) - added _TZ3000_18ejxno0 and _TZ3000_qewo8dlz fingerprints; added TS0001 wall switches fingerprints; added TS011F 2-gang wall outlets; added switchType configuration
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
import groovy.transform.Field

def version() { "0.2.8" }
def timeStamp() {"2022/07/13 9:56 PM"}

@Field static final Boolean debug = false

metadata {
    definition (name: "Zemismart ZigBee Wall Switch Multi-Gang", namespace: "muxa", author: "Muxa", importUrl: "https://raw.githubusercontent.com/kkossev/hubitat-muxa-fork/development/drivers/zemismart-zigbee-multigang-switch.groovy", singleThreaded: true ) {
        capability "Initialize"
        capability "Actuator"
        capability "Configuration"
        capability "Refresh"
        capability "Switch"
        capability "Health Check"
        
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0003,0004,0005,0006,E000,E001,0000", outClusters:"0019,000A", model:"TS0001", manufacturer:"_TZ3000_npzfdcof", deviceJoinName: "Tuya Zigbee Switch"                  // https://www.aliexpress.com/item/1005002852788275.html
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0003,0004,0005,0006,E000,E001,0000", outClusters:"0019,000A", model:"TS0001", manufacturer:"_TZ3000_hktqahrq", deviceJoinName: "Tuya Zigbee Switch"
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0003,0004,0005,0006,E000,E001,0000", outClusters:"0019,000A", model:"TS0001", manufacturer:"_TZ3000_mx3vgyea", deviceJoinName: "Tuya Zigbee Switch"
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0003,0004,0005,0006,E000,E001,0000", outClusters:"0019,000A", model:"TS0001", manufacturer:"_TZ3000_5ng23zjs", deviceJoinName: "Tuya Zigbee Switch"
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0003,0004,0005,0006,E000,E001,0000", outClusters:"0019,000A", model:"TS0001", manufacturer:"_TZ3000_rmjr4ufz", deviceJoinName: "Tuya Zigbee Switch"
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0003,0004,0005,0006,E000,E001,0000", outClusters:"0019,000A", model:"TS0001", manufacturer:"_TZ3000_v7gnj3ad", deviceJoinName: "Tuya Zigbee Switch"
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0003,0004,0005,0006,E000,E001,0000", outClusters:"0019,000A", model:"TS0001", manufacturer:"_TZ3000_mx3vgyea", deviceJoinName: "Tuya Zigbee Switch"
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0003,0004,0005,0006,E000,E001,0000", outClusters:"0019,000A", model:"TS0001", manufacturer:"_TZ3000_qsp2pwtf", deviceJoinName: "Tuya Zigbee Switch"
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0003,0004,0005,0006,E000,E001,0000", outClusters:"0019,000A", model:"TS000F", manufacturer:"_TZ3000_m9af2l6g", deviceJoinName: "Tuya Zigbee Switch"
 
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0004,0005 0006",           outClusters:"0019",      model:"TS0002", manufacturer:"Zemismart",        deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"  
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,000A,0004,0005,0006",           outClusters:"0019",      model:"TS0002", manufacturer:"_TZ3000_tas0zemd", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"  
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,000A,0004,0005,0006",           outClusters:"0019",      model:"TS0002", manufacturer:"_TYZB01_tas0zemd", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"  
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,000A,0004,0005,0006",           outClusters:"0019",      model:"TS0002", manufacturer:"_TZ3000_7hp93xpr", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"  
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0004,0005,0006",                     outClusters:"0019",      model:"TS0002", manufacturer:"_TZ3000_7hp93xpr", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"  
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0004,0005 0006",                outClusters:"0019,000A", model:"TS0002", manufacturer:"_TZ3000_vjhyd6ar", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"  
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0004,0005,0006",           outClusters:"0019",      model:"TS0002", manufacturer:"_TZ3000_tonrapsk", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0004,0005,0006",           outClusters:"0019",      model:"TS0002", manufacturer:"_TZ3000_bvrlqyj7", deviceJoinName: "Avatto Zigbee Switch Multi-Gang"    // check!
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0004,0005,0006",           outClusters:"0019",      model:"TS0002", manufacturer:"_TZ3000_atp7xmd9", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"   // check!
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0004,0005,0006",           outClusters:"0019",      model:"TS0002", manufacturer:"_TZ3000_h34ihclt", deviceJoinName: "Tuya Zigbee Switch Multi-Gang"      //// check!
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0004,0005,0006",           outClusters:"0019",      model:"TS0002", manufacturer:"_TYZB01_wmak4qjy", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"  // check!
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0004,0005,0006,E000,E001", outClusters:"0019,000A", model:"TS0002", manufacturer:"_TZ3000_qn8qvk9y", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"
        
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,000A,0004,0005,0006",           outClusters:"0019",      model:"TS0003", manufacturer:"_TYZB01_pdevogdj", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"  
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,000A,0004,0005,0006",           outClusters:"0019",      model:"TS0003", manufacturer:"_TZ3000_pdevogdj", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"  
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0004,0005,0006",           outClusters:"0019",      model:"TS0003", manufacturer:"_TZ3000_odzoiovu", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"  
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0004,0005,0006",           outClusters:"0019",      model:"TS0003", manufacturer:"_TZ3000_vsasbzkf", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"  
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0004,0005,0006",           outClusters:"0019",      model:"TS0003", manufacturer:"_TZ3000_34zbimxh", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"  
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0004,0005,0006",           outClusters:"0019",      model:"TS0003", manufacturer:"_TZ3000_odzoiovu", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"  
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0004,0005,0006",                     outClusters:"0019",      model:"TS0003", manufacturer:"_TZ3000_wqfdvxen", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"  
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0004,0005,0006",                     outClusters:"0019",      model:"TS0003", manufacturer:"_TZ3000_c0wbnbbf", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"  
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0004,0005,0006",                outClusters:"0019",      model:"TS0003", manufacturer:"_TZ3000_c0wbnbbf", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"  
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0003,0004,0005,0006,E000,E001",      outClusters:"0019,000A", model:"TS0003", manufacturer:"_TZ3000_tbfw3xj0", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"

        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0004,0005,0006",           outClusters:"0019",      model:"TS0004", manufacturer:"_TZ3000_ltt60asa", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"  // check! 
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0004,0005,0006",           outClusters:"0019",      model:"TS0004", manufacturer:"_TZ3000_excgg5kb", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"  // check!
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0004,0005,0006",           outClusters:"0019",      model:"TS0004", manufacturer:"_TZ3000_a37eix1s", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"  // check!
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,000A,0004,0005,0006",           outClusters:"0019",      model:"TS0004", manufacturer:"_TZ3000_go9rahj5", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0003,0004,0005,0006,E000,E001",      outClusters:"0019",      model:"TS0004", manufacturer:"_TZ3000_aqgofyol", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"
        
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0003,0004,0005,0006,E000,E001,0000", outClusters:"0019,000A", model:"TS0004", manufacturer:"_TZ3000_excgg5kb"  // 4-relays module
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0003,0004,0005,0006,E000,E001,0000", outClusters:"0019,000A", model:"TS0004", manufacturer:"_TZ3000_w58g68s3"  // Yagusmart 3 gang zigbee switch
        
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0004,0005,0006",           outClusters:"0019",      model:"TS0011", manufacturer:"_TZ3000_ybaprszv", deviceJoinName: "Zemismart Zigbee Switch No Neutral"  
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0003,0004,0005,0006,E000,E001,0000", outClusters:"0019,000A", model:"TS0011", manufacturer:"_TZ3000_txpirhfq", deviceJoinName: "Tuya Zigbee Mini Switch TMZ02L"  
        
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0001,0007,0000,0003,0004,0005,0006,E000,E001,0002", outClusters:"0019,000A", model:"TS0012", manufacturer:"_TZ3000_k008kbls", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang" // check! 
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0004,0005,0006",           outClusters:"0019",      model:"TS0012", manufacturer:"_TZ3000_uz5xzdgy", deviceJoinName: "Zemismart Zigbee Switch No Neutral"
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0004,0005,0006",                outClusters:"0019,000A", model:"TS0012", manufacturer:"_TZ3000_fvh3pjaz", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0004,0005,0006,EF00",           outClusters:"0019,000A", model:"TS0012", manufacturer:"_TZ3000_lupfd8zu", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0003,0004,0005,0006,E000,E001",      outClusters:"0019,000A", model:"TS0012", manufacturer:"_TZ3000_jl7qyupf", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0004,0005,0006,E000,E001", outClusters:"0019,000A", model:"TS0012", manufacturer:"_TZ3000_18ejxno0", deviceJoinName: "Tuya Zigbee Switch Multi-Gang"     // @dingyang.yee
        
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0006",                     outClusters:"0019",      model:"TS0013", manufacturer:"_TYZB01_Lrjzz1UV", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"  // check!
        
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0006",                     outClusters:"0019",      model:"TS0013", manufacturer:"_TZ3000_bvrlqyj7", deviceJoinName: "Avatto Zigbee Switch Multi-Gang"  // check!
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0006",                     outClusters:"0019",      model:"TS0013", manufacturer:"_TZ3000_wu0shw0i", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang" // check! 
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0006",                     outClusters:"0019",      model:"TS0013", manufacturer:"_TYZB01_stv9a4gy", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang" // check! 
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0004,0005,0006",                outClusters:"0019,000A", model:"TS0013", manufacturer:"_TZ3000_wyhuocal", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0004,0005,0006",                outClusters:"0019",      model:"TS0013", manufacturer:"_TYZB01_mqel1whf", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang"
        
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0006",                     outClusters:"0019",      model:"TS0013", manufacturer:"_TZ3000_fvh3pjaz", deviceJoinName: "Zemismart Zigbee Switch Multi-Gang" // check! 
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0006",                     outClusters:"0019",      model:"TS0013", manufacturer:"_TYZB01_mtlhqn48", deviceJoinName: "Lonsonho Zigbee Switch Multi-Gang" // check!
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0006",                     outClusters:"0019",      model:"TS0013", manufacturer:"TUYATEC-O6SNCwd6", deviceJoinName: "TUYATEC Zigbee Switch Multi-Gang" // check! 
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0006",                     outClusters:"0019",      model:"TS0013", manufacturer:"_TZ3000_h34ihclt", deviceJoinName: "Tuya Zigbee Switch Multi-Gang"  // check!
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0000,0003,0004,0005,0006",           outClusters:"0019",      model:"TS0013", manufacturer:"_TZ3000_k44bsygw", deviceJoinName: "Zemismart Zigbee Switch No Neutral"
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0003,0004,0005,0006,0000",           outClusters:"0019,000A", model:"TS0013", manufacturer:"_TZ3000_qewo8dlz", deviceJoinName: "Tuya Zigbee Switch 3 Gang No Neutral"    // @dingyang.yee https://www.aliexpress.com/item/4000298926256.html https://github.com/Koenkk/zigbee2mqtt/issues/6138#issuecomment-774720939
        
        
        command "powerOnState", [
            [name:"powerOnState",    type: "ENUM",   constraints: ["--- Select ---", "OFF", "ON", "Last state"], description: "Select Power On State"] 
        ]
        command "switchType", [
            [name:"switchType",    type: "ENUM",   constraints: ["--- Select ---", "toggle", "state", "momentary"], description: "Select Switch Type"]     // 0: 'toggle', 1: 'state', 2: 'momentary'
        ]
        
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
   //log.debug "${device.displayName} Parsing '${description}'"
   def descMap = [:] 
   try {
       descMap = zigbee.parseDescriptionAsMap(description)
   }
   catch ( e ) {
       if (settings?.logEnable) log.warn "${device.displayName} exception caught while parsing description ${description} \r descMap:  ${descMap}"
       return null
   }    
   logDebug "Parsed: $descMap"
    
   Map map = null // [:]
        
   if (descMap.cluster == "0006" && descMap.attrId == "0000") {
       // descMap.command =="0A" - switch toggled physically
       // descMap.command =="01" - get switch status
       // descMap.command =="0B" - command response
       def cd = getChildDevice("${device.id}-${descMap.endpoint}")
       if (cd == null) {
           if (!(device.data.model  in ['TS0011', 'TS0001'])) {
               log.warn "${device.displayName} Child device ${device.id}-${descMap.endpoint} not found. Initialise parent device first"
               return
           }
       }
       def switchAttribute = descMap.value == "01" ? "on" : "off"
       if (cd != null ) {
           if (descMap.command in ["0A", "0B"]) {
               // switch toggled
               cd.parse([[name: "switch", value:switchAttribute, descriptionText: "Child switch ${descMap.endpoint} turned $switchAttribute"]])
           } 
           else if (descMap.command =="01") {
               // report switch status
               cd.parse([[name: "switch", value:switchAttribute, descriptionText: "Child switch  ${descMap.endpoint} is $switchAttribute"]])
           }
       }
       if (switchAttribute == "on") {
           logDebug "Parent switch on"
           return createEvent(name: "switch", value: "on")
       } 
       else if (switchAttribute == "off") {
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
    } // OnOff cluster, attrId "0000"
    else if (descMap.cluster == "0006" && descMap.attrId != "0000") { // other attr
        processOnOfClusterOtherAttr( descMap )
    }
    else if (descMap.cluster == "E001") { // Tuya Switch Mode cluster
        processOnOfClusterOtherAttr( descMap )
    }
    else {
        logDebug "${device.displayName} unprocessed EP: ${descMap.sourceEndpoint} cluster: ${descMap.clusterId} attrId: ${descMap.attrId}"
    }
}

def off() {
    if (settings?.txtEnable) log.info "${device.displayName} Turning all child switches off"	
    "he cmd 0x${device.deviceNetworkId} 0xFF 0x0006 0x0 {}"
}

def on() {
    if (settings?.txtEnable) log.info "${device.displayName} Turning all child switches on"
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
    logDebug "sending componentOn ${childDevice.deviceNetworkId}"
    sendHubCommand(new HubAction("he cmd 0x${device.deviceNetworkId} 0x${getChildId(childDevice)} 0x0006 0x1 {}", Protocol.ZIGBEE))
}

def componentOff(childDevice) {
    logDebug "sending componentOff ${childDevice.deviceNetworkId}"
    sendHubCommand(new HubAction("he cmd 0x${device.deviceNetworkId} 0x${getChildId(childDevice)} 0x0006 0x0 {}", Protocol.ZIGBEE))
}

def componentRefresh(childDevice) {
    logDebug "sending componentRefresh ${childDevice.deviceNetworkId} ${childDevice}"    
    sendHubCommand(new HubAction("he rattr 0x${device.deviceNetworkId} 0x${getChildId(childDevice)} 0x0006 0x0", Protocol.ZIGBEE))
}

def setupChildDevices() {
    logDebug "Parent setupChildDevices"
    deleteObsoleteChildren()    
    def buttons = 0
    switch (device.data.model) {
        case 'TS0004' :
        case 'TS0014' :
            buttons = 4
            break
        case 'TS0003' :
        case 'TS0013' :
            buttons = 3
            break
        case 'TS0002' :
        case 'TS0012' :
            buttons = 2
            break
        case 'TS0011' :
        case 'TS0001' :
            buttons = 0
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
            log.info "${device.displayName} Child device ${childId} already exists (${existingChild})"
        } else {
            log.info "${device.displayName} Creatung device ${childId}"
            addChildDevice("hubitat", "Generic Component Switch", childId, [isComponent: true, name: "Switch EP0${i}", label: "${device.displayName} EP0${i}"])
        }
    }
}

def deleteObsoleteChildren() {
	logDebug "Parent deleteChildren"
    
    getChildDevices().each {child->
        if (!child.deviceNetworkId.startsWith(device.id) || child.deviceNetworkId == "${device.id}-00") {
            log.info "${device.displayName} Deleting ${child.deviceNetworkId}"
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
    if (settings?.logEnable == null) device.updateSetting("logEnable", true)
    if (settings?.txtEnable == null) device.updateSetting("txtEnable", true)    
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
    return zigbee.readAttribute(0x0000, [0x0004, 0x000, 0x0001, 0x0005, 0x0007, 0xfffe], [:], delay=200)
}

def configure() {
    logDebug " configure().."
    List<String> cmds = []
    cmds += tuyaBlackMagic()
    //cmds += refresh()
    cmds += zigbee.onOffConfig()
    cmds += zigbee.onOffRefresh()
    sendZigbeeCommands(cmds)
}

void sendZigbeeCommands(List<String> cmds) {
    logDebug "sendZigbeeCommands received : ${cmds}"
	sendHubCommand(new hubitat.device.HubMultiAction(cmds, hubitat.device.Protocol.ZIGBEE))
}


def logDebug(msg) {
    String sDnMsg = device.displayName + " " + msg
    if (settings?.logEnable) log.debug sDnMsg
}

def powerOnState(relayMode) {
    List<String> cmds = []
    int modeEnum = 99
    switch(relayMode) {
        case "OFF" :
            modeEnum = 0
            break
        case "ON" :
            modeEnum = 1
            break
        case "Last state" :
            modeEnum = 2
            break
        default :
            log.error "${device.displayName} please select a Power On State option"
            return
    }
    logDebug ("setting  Power On State option to: ${relayMode}  (${modeEnum}")
    cmds += zigbee.writeAttribute(0x0006, 0x8002,  DataType.ENUM8, modeEnum)
    sendZigbeeCommands(cmds)
}

def switchType(type) {
    List<String> cmds = []
    int modeEnum = 99
    switch(type) {
        case "toggle" :
            modeEnum = 0
            break
        case "state" :
            modeEnum = 1
            break
        case "momentary" :
            modeEnum = 2
            break
        default :
            log.error "${device.displayName} please select a Switch Type"
            return
    }
    logDebug ("setting  Switch Type to: ${type} (${modeEnum})")
    cmds += zigbee.writeAttribute(0xE001, 0xD030,  DataType.ENUM8, modeEnum)
    sendZigbeeCommands(cmds)
}

//            [name:"switchType",    type: "ENUM",   constraints: ["--- Select ---", "toggle", "state", "state"], description: "Select Switch Type"]     // 0: 'toggle', 1: 'state', 2: 'momentary'


def processOnOfClusterOtherAttr( descMap ) {
    logDebug "cluster OnOff  attribute ${descMap.attrId} reported: value=${descMap.value}"
    def mode
    def attrName
    def value = descMap.value as int
    switch (descMap.attrId) {
        case "8000" :
            attrName = "Child Lock"
            mode = value == 0 ? "off" : "on"
            break
        case "8001" :
            attrName = "LED mode"
            mode = value == 0 ? "Disabled"  : value == 1 ? "Lit when On" : value == 2 ? "Lit when Off" : null
            break
        case "8002" :
            attrName = "Power On State"
            mode = value == 0 ? "off" : value == 1 ? "on" : value == 2 ?  "Last state" : null
            break
        case "D030" : // cluster E001
            attrName = "Switch Type"
            mode = value == 0 ? "toggle" : value == 1 ? "state" : value == 2 ?  "momentary state" : null
            break
        default :
            logDebug "processOnOfClusterOtherAttr: <b>UNPROCESSED On/Off Cluster</b>  attrId: ${descMap.attrId} value: ${descMap.value}"
            break
    }
    if (txtEnable) log.info "${device.displayName} ${attrName} is: ${mode}"    
}
