/**
 *  Virtual IOT8Z
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
	definition (name: "Child_IOT8-Z", namespace: "monthpublic25501", author: "Luis Contreras", cstHandler: true, mnmn: "SmartThingsCommunity", vid: "50830b33-69d6-32a0-bebd-952eac44d074") {
		capability "Contact Sensor"        
        capability "Sensor"
        capability "Switch"
        capability "monthpublic25501.analogSensor"
	}

	simulator {
		
	}

	tiles {
		// TODO: define your main and details tiles here
	}
}

def installed() {
	log.debug "Installed"
}

def updated() {
	log.debug "Updated"
}


// parse events into attributes
def parse(String description) {
}

// handle commands
def on() {
    parent.childOn(device.deviceNetworkId)
}

def off() {
    parent.childOff(device.deviceNetworkId)
}