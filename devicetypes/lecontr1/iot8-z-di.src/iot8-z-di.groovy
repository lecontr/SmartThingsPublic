/**
 *  IOT8-Z_DI
 *
 *  Copyright 2021 Luis Contreras
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
metadata {
	definition (name: "IOT8-Z_DI", namespace: "lecontr1", author: "Luis Contreras") {
		capability "Actuator"
        capability "Configuration"
        capability "Refresh"
        capability "Contact Sensor"
		capability "Switch"
        capability "Health Check"
	}


    // simulator metadata
	simulator {
		// status messages
		status "on": "on/off: 1"
		status "off": "on/off: 0"

		// reply messages
		reply "zcl on-off on": "on/off: 1"
		reply "zcl on-off off": "on/off: 0"
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"st.switches.light.on", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.switches.light.on", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
			}
		}
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		main "switch"
		details(["switch", "refresh"])
	}
}

// Parse incoming device messages to generate events
def parse(String description) {
	log.debug "description is $description"
	def event = zigbee.getEvent(description)
	if (event) {
		sendEvent(event)
	}
	else {
		log.warn "DID NOT PARSE MESSAGE for description : $description"
		log.debug zigbee.parseDescriptionAsMap(description)
	}
}

def on() {
	log.debug "Executing 'on'"
	parent.childOn(device.deviceNetworkId)
}

def off() {
	log.debug "Executing 'off'"   
	parent.childOff(device.deviceNetworkId)
}

/**
 * PING is used by Device-Watch in attempt to reach the Device
 * */
def ping() {
	return refresh()
}

def refresh() {
	zigbee.onOffRefresh() + zigbee.onOffConfig()
}

def configure() {
	// Device-Watch allows 2 check-in misses from device + ping (plus 2 min lag time)
	sendEvent(name: "checkInterval", value: 2 * 10 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
	log.debug "Configuring Reporting and Bindings."
	zigbee.onOffRefresh() + zigbee.onOffConfig()
}