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
	definition (name: "IOT8-Z-child-contact-switch", namespace: "Smartenit", author: "Luis Contreras") {
		capability "Actuator"
		capability "Contact Sensor"
		capability "Switch"
		capability "Health Check"
	}
}

def installed() {
	log.debug "Installed"
}

def updated() {
	log.debug "Updated"
}

// Parse incoming device messages to generate events
def parse(String description) {
}

def on() {
	log.debug "Executing 'on'"
	parent.childOn(device.deviceNetworkId)
}

def off() {
	log.debug "Executing 'off'"   
	parent.childOff(device.deviceNetworkId)
}