/**
 *  MLC
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
 
 import groovy.transform.Field
 
 @Field final MeteringCluster = 0x0702
 @Field final MeteringCurrentSummation = 0x0000
 @Field final MeteringInstantDemand = 0x0400
 @Field final EnergyDivisor = 100000
 @Field final CurrentDivisor = 100
 @Field final Current = 0x00f0
 @Field final Voltage = 0x00f1
 @Field final SmartenitMfrCode = 0x1075
 
metadata {
	definition (name: "Metering Load Controller", namespace: "Smartenit", author: "Luis Contreras", mnmn: "SmartThingsCommunity", vid: "724d5e90-55eb-326b-84bb-3a4a0e76f654") {
		capability "Actuator"
		capability "Configuration"
		capability "Refresh"
		capability "Power Meter"
		capability "Energy Meter"
		capability "Switch"
		capability "Health Check"
		capability "Voltage Measurement"
		capability "monthpublic25501.current"

		fingerprint model: "ZBMLCSR", manufacturer: "Smartenit, Inc", deviceJoinName: "Smartenit Meter"
	}
}

def getFPoint(String FPointHex){
	return (Float)Long.parseLong(FPointHex, 16)
}

// Parse incoming device messages to generate events
def parse(String description) {
	def event = zigbee.getEvent(description)
	if (event) {
		log.debug "event: ${event}, ${event.name}, ${event.value}"
		if (event.name == "power") {
			log.debug "capturing power param"
			sendEvent(name: "power", value: (event.value/EnergyDivisor))
		}
		else { sendEvent(event) }
	}
	else {
		def mapDescription = zigbee.parseDescriptionAsMap(description)
		log.debug "mapDescription... : ${mapDescription}"
		if (mapDescription) {
			if (mapDescription.clusterInt == MeteringCluster) {
				if (mapDescription.attrInt == MeteringCurrentSummation) {
					return sendEvent(name:"energy", value: getFPoint(mapDescription.value)/EnergyDivisor)
				} else if (mapDescription.attrInt == MeteringInstantDemand) {
					return sendEvent(name:"power", value: getFPoint(mapDescription.value/EnergyDivisor))
				} else if (mapDescription.attrInt == Voltage) {
					return sendEvent(name:"voltage", value: getFPoint(mapDescription.value) / 100)
				} else if (mapDescription.attrInt == Current) {
					return sendEvent(name:"current", value: getFPoint(mapDescription.value) / 100, unit: "A")
				}
			}
		}
	}
}

def on() {
	log.debug "received on command"
	zigbee.on()
}

def off() {
	log.debug "received off command"
	zigbee.off()
}

def refresh() {
	zigbee.readAttribute(MeteringCluster, MeteringCurrentSummation) +
	zigbee.readAttribute(MeteringCluster, MeteringInstantDemand) + 
	zigbee.readAttribute(MeteringCluster, Voltage, [mfgCode: SmartenitMfrCode]) +
	zigbee.readAttribute(MeteringCluster, Current, [mfgCode: SmartenitMfrCode])
}

def configure() {
	log.debug "in configure()"
	return (configureHealthCheck() +
		zigbee.configureReporting(MeteringCluster, MeteringCurrentSummation, 0x25, 0, 600, 50) +
		zigbee.configureReporting(MeteringCluster, MeteringInstantDemand, 0x2a, 0, 600, 50)
	)
}

def configureHealthCheck() {
	Integer hcIntervalMinutes = 10
	sendEvent(name: "checkInterval", value: hcIntervalMinutes * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
	return refresh()
}

def updated() {
	log.debug "in updated()"
	// updated() doesn't have it's return value processed as hub commands, so we have to send them explicitly
	def cmds = configureHealthCheck()
	cmds.each{ sendHubCommand(new physicalgraph.device.HubAction(it)) }
}

def ping() {
	return zigbee.readAttribute(zigbee.ONOFF_CLUSTER, ONOFF_ATTRIBUTE)
}