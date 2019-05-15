/**
 *  Update Url:   https://raw.githubusercontent.com/arnbme/nyckelharpa/master/Centralite-Keypad.groovy
 *
 *  Centralite Keypad
 *
 *  Copyright 2015-2016 Mitch Pond, Zack Cornelius
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 * 	May 15, 2019 v0.1.8 For 3400 and UEI, change off command to issue beep(0)
 *						Show Version as an unused setting. No other way to get it to show AFAIK
 * 	May 11, 2019 v0.1.7 Add support for UEI keypad device
 *					added UEI XHK1 fingerprint but did not test if DTH is assigned correctly
 *					added Steve Jackson's changed battery reference voltages to accommodate the higher voltage of
 *					of the UEI XHK1 keypad.  Changed voltages from 3.5 to 7.2 (voltage too high),
 *					3.5 to 5.2 (MinVolts), 3.0 to 6.8 (MaxVolts).
 *	May 11, 2019 added version number, initially set to 0.1.7 17 changes from initial porting
 *	May 11, 2019 Generate beeps for 3400-G Keypad, Centralite V3 does not respond to Siren Command
 *	May 05, 2019 Restore button capability as pushableButton, capability ContactSensor add update Url
 *	Apr 30, 2019 HSM hijacked command setExitDelay to send all HSM delay to keypad
 *								avoid confusion by changing ours to setExitAway
 *  Apr 29, 2019 Arn Burkhoff Updated siren and off commands
 *  Apr 29, 2019 Arn Burkhoff added commands setExitNight setExitStay, capability Alarm.
 *							When Panic entered, internally issue siren command
 *  Apr 24, 2019 Mitch Pond fixed Temperature and converted module to HE structure 
 *  Apr 22, 2019 changed battery back to % from volts for users. Temperature is still very wrong 
 *  Mar 31, 2019 routine disarm and others issued multiple times. fixed in other modules 
 *  Mar 31, 2019 routine disarm threw an error caused by HE sending a delay value, add delay parm that is ignored
 *  Mar 31, 2019 deprecate Sep 20, 2018 change, HE should be fast enough for proper acknowledgements
 *  Feb 26, 2019 in sendRawStatus set seconds to Integer or it fails
 *	Feb 26, 2019 HE device.currentValue gives value at start of event, use true as second parameter to get live actual value
 *  Feb 25, 2019 kill setmodehelper on detentrydelay and setexitdelay commands, mode help sets mode icon lights
 *					error found in Hubitat with entry delay
 *  Feb 22, 2019 convertToHexString default in ST not available in HE, change width to 2
 *  Feb 21, 2019 V1.0.1 Hubitat command names vary from Smartthings, add additional commands
 *  -------------------------Porting to Hubitat starts around here----------------------------------------
 *  Sep 20, 2018 per ST tech support. Issue acknowlegement in HandleArmRequest
 *               disable routines: acknowledgeArmRequest and sendInvalidKeycodeResponse allowing SHM Delay to have no code changes
 *
 *  Sep 18, 2018 comment out health check in an attempt to fix timout issue  (no improvement) 
 *  Sep 04, 2018 add health check and vid for new phone app. 
 *  Mar 25, 2018 add volts to battery message 		
 *  Aug 25, 2017 deprecate change of Jul 12, 2017, change from Jul 25 & 26, 2017 remains but is no longer needed or used 		
 *  Jul 26, 2017 Stop entryDelay from updating field lastUpdate or alarm is not triggered in CoRE
 *			pistons that assume a time change means alarm mode(off or on) was reset
 *  Jul 25, 2017 in formatLocalTime add seconds to field lastUpdate. 
 * 			need seconds to catch a rearm within the open time delay in Core Front Door Opens piston
 * 			otherwise alarm sounds after rearm
 *  Jul 12, 2017 in sendStatustoDevice light Night button not HomeStay button (no such mode in SmartHome) 		
 */
metadata {
	definition (name: "Centralitex Keypad", namespace: "mitchpond", author: "Mitch Pond", vid: "generic-motion") {
		capability "SecurityKeypad"
		capability "Alarm"
		capability "Battery"
		capability "Configuration"
        capability "Motion Sensor"
		capability "Sensor"
		capability "Temperature Measurement"
		capability "Refresh"
		capability "Lock Codes"
		capability "Tamper Alert"
		capability "Tone"
		capability "PushableButton"
//      capability "polling"
		capability "ContactSensor"
		
		attribute "armMode", "String"
        attribute "lastUpdate", "String"
		
		command "setDisarmed"
		command "setArmedAway"
		command "setArmedStay"
		command "setArmedNight"
		command "setExitAway", ['number']		//this was setExitDelay in ST
		command "setExitStay", ['number']
		command "setExitNight", ['number']
		command "setEntryDelay", ['number']		//issue same hardware command as Beep
		command "testCmd", ['number']
		command "sendInvalidKeycodeResponse"
		command "acknowledgeArmRequest",['number']
//		HSM commands		
		command "armNight"						//not set as part of device capabilities
		
		fingerprint endpointId: "01", profileId: "0104", deviceId: "0401", inClusters: "0000,0001,0003,0020,0402,0500,0B05", outClusters: "0019,0501", manufacturer: "CentraLite", model: "3400", deviceJoinName: "Xfinity 3400-X Keypad"
		fingerprint endpointId: "01", profileId: "0104", deviceId: "0401", inClusters: "0000,0001,0003,0020,0402,0500,0501,0B05,FC04", outClusters: "0019,0501", manufacturer: "CentraLite", model: "3405-L", deviceJoinName: "Iris 3405-L Keypad"
 		fingerprint endpointId: "01", profileId: "0104", deviceId: "0401", inClusters: "0000,0001,0003,0020,0402,0500,0B05", outClusters: "0003,0019,0501", manufacturer: "Universal Electronics Inc", model: "URC4450BC0-X-R", deviceJoinName: "Xfinity XHK1-UE Keypad" 
	}
	
	preferences{
		input ("version", "text", title: "Version (for display only)", defaultValue: "${version()}" )
		input ("tempOffset", "number", title: "Enter an offset to adjust the reported temperature",
				defaultValue: 0, displayDuringSetup: false)
		input ("beepLength", "number", title: "Enter length of beep in seconds",
				defaultValue: 1, displayDuringSetup: false)
                
        input ("motionTime", "number", title: "Time in seconds for Motion to become Inactive (Default:10, 0=disabled)",	defaultValue: 10, displayDuringSetup: false)
        input ("showVolts", "bool", title: "Turn on to show actual battery voltage. Default (Off) is percentage", defaultValue: false, displayDuringSetup: false)
        input ("logdebugs", "bool", title: "Log debugging messages", defaultValue: false, displayDuringSetup: false)
        input ("logtraces", "bool", title: "Log trace messages", defaultValue: false, displayDuringSetup: false)
//		paragraph "Centralitex Keypad Plus UEI Version ${version()}" Does not work in HE
	}

}

def version()
	{
	return "0.1.8" as String;	
	}

// Statuses:
// 00 - Command: setDisarmed   Centralite all icons off / Iris Off button on
// 01 - Command: setArmedStay  lights Centralite Stay button / Iris Partial
// 02 - Command: setArmedNight lights Centralite Night button / Iris does nothing
// 03 - Command: setArmedAway  lights Centralite Away button / Iris ON 
// 04 - Panic Sound, uses seconds for duration (fast beep sound on Iris V2, not available on Centralite)
// 05 - Command: Beep and SetEntryDelay Slow beep (1 per second, uses seconds for duration, max 255) Appears to keep the status lights as it was, used for entry delay command
// 06 - Amber status blink (Runs forever until Off or some command issued)
// 07 - ? 
// 08 - Command: setExitStay  Exit delay Slow beep (1 per second, accelerating to 2 beep per second for the last 10 seconds) - With red flashing status - lights Stay icon/Iris Partial Uses seconds 
// 09 - Command: setExitNight Exit delay Slow beep (1 per second, accelerating to 2 beep per second for the last 10 seconds) - With red flashing status - lights Night icon/ Uses seconds  (does nothing on Iris)
// 10 - Command: setExitDelay Exit delay Slow beep (1 per second, accelerating to 2 beep per second for the last 10 seconds) - With red flashing status - lights Away Uses/Iris ON seconds
// 11 - ?
// 12 - ?
// 13 - ?

// parse events into attributes
def parse(String description) {
	logdebug "Parsing '${description}'";
	def results = [];
	
	//------Miscellaneous Zigbee message------//
	if (description?.startsWith('catchall:')) {
		def message = zigbee.parseDescriptionAsMap(description);
		//------ZDO packets - drop ------//
		if (message.profileId == '0000') return []
		//------Profile-wide command (rattr responses, errors, etc.)------//
		else if (message?.isClusterSpecific == false) {
			//------Default response------//
			if (message?.command == '0B') {
				if (message?.data[1] == '81') 
					log.error "Device: unrecognized command: "+description;
				else if (message?.data[1] == '80') 
					log.error "Device: malformed command: "+description;
			}
			//------Read attributes responses------//
			else if (message?.command == '01') {
				if (message?.clusterId == '0402') {
					logdebug "Device: read attribute response: "+description;

					results = parseTempAttributeMsg(message)
				}}
			else 
				log.info "Unhandled profile-wide command: "+description;
		}
		//------Cluster specific commands------//
		else if (message?.isClusterSpecific) {
			//------Poll Control - drop------//
			if (message?.clusterId == '0020') return []
			//------IAS ACE------//
			else if (message?.clusterId == '0501') {
				if (message?.command == '07') {
                	motionON()
				}
                else if (message?.command == '04') {
                	results = createEvent(name: "button", value: "pushed", data: [buttonNumber: 1], descriptionText: "$device.displayName panic button was pushed", isStateChange: true)
					siren()	
                    panicContact()
                }
				else if (message?.command == '00') {
					results = handleArmRequest(message)
					logtrace results
				}
			}
			else log.warn "Unhandled cluster-specific command: "+message
		}
	}
	//------IAS Zone Enroll request------//
	else if (description?.startsWith('enroll request')) {
		logtrace "Sending IAS enroll response..."
		results = zigbee.enrollResponse()
	}
	//------Read Attribute response------//
	else if (description?.startsWith('read attr -')) {
		results = parseReportAttributeMessage(description)
	}
	//------Temperature Report------//
	else if (description?.startsWith('temperature: ')) {
		logdebug "Got ST-style temperature report.."
		results = createEvent(getTemperatureResult(zigbee.parseHATemperatureValue(description, "temperature: ", getTemperatureScale())))
		logdebug results
	}
    else if (description?.startsWith('zone status ')) {
    	results = parseIasMessage(description)
    }
	return results
}


def configure() {
    logtrace "--- Configure Called"
    String hubZigbeeId = swapEndianHex(device.hub.zigbeeEui)
    def cmd = [
        //------IAS Zone/CIE setup------//
        "zcl global write 0x500 0x10 0xf0 {${hubZigbeeId}}", "delay 100",
        "send 0x${device.deviceNetworkId} 1 1", "delay 200",

        //------Set up binding------//
        "zdo bind 0x${device.deviceNetworkId} 1 1 0x500 {${device.zigbeeId}} {}", "delay 200",
        "zdo bind 0x${device.deviceNetworkId} 1 1 0x501 {${device.zigbeeId}} {}", "delay 200",
        
    ] + 
    zigbee.configureReporting(1,0x20,0x20,3600,43200,0x01) + 
    zigbee.configureReporting(0x0402,0x00,0x29,30,3600,0x0064)

    return cmd + refresh()
}

def poll() { 
	refresh()
}

def refresh() {
	 return sendStatusToDevice() +
		zigbee.readAttribute(0x0001,0x20) + 
		zigbee.readAttribute(0x0402,0x00)
}

private formatLocalTime(time, format = "EEE, MMM d yyyy @ h:mm:ss.SSS a z") {
	if (time instanceof Long) {
    	time = new Date(time)
    }
	if (time instanceof String) {
    	//get UTC time
    	time = timeToday(time, location.timeZone)
    }   
    if (!(time instanceof Date)) {
    	return null
    }
	def formatter = new java.text.SimpleDateFormat(format)
	formatter.setTimeZone(location.timeZone)
	return formatter.format(time)
}

private parseReportAttributeMessage(String description) {
	descMap = zigbee.parseDescriptionAsMap(description)
	//logdebug "Desc Map: $descMap"

	def results = []
	
	if (descMap.cluster == "0001" && descMap.attrId == "0020") {
		logdebug "Received battery level report"
//		sendNotificationEvent ("Received battery level report descMap.value")
		results = createEvent(getBatteryResult(Integer.parseInt(descMap.value, 16)))
	}
    else if (descMap.cluster == "0001" && descMap.attrId == "0034")
    {
    	logdebug "Received Battery Rated Voltage: ${descMap.value}"
//		sendNotificationEvent ("Received Battery Rated Voltage: descMap.value")
    }
    else if (descMap.cluster == "0001" && descMap.attrId == "0036")
    {
    	logdebug "Received Battery Alarm Voltage: ${descMap.value}"
//		sendNotificationEvent ("Received Battery Alarm Voltage: descMap.value")
    }
	else if (descMap.cluster == "0402" && descMap.attrId == "0000") {
		def value = getTemperature(descMap.value)
		results = createEvent(getTemperatureResult(value))
	}

	return results
}

private parseTempAttributeMsg(message) {
	byte[] temp = message.data[-2..-1].reverse()
	createEvent(getTemperatureResult(getTemperature(temp.encodeHex() as String)))
}

private Map parseIasMessage(String description) {
    List parsedMsg = description.split(' ')
    String msgCode = parsedMsg[2]
    
    Map resultMap = [:]
    switch(msgCode) {
        case '0x0020': // Closed/No Motion/Dry
        	resultMap = getContactResult('closed')
            break

        case '0x0021': // Open/Motion/Wet
        	resultMap = getContactResult('open')
            break

        case '0x0022': // Tamper Alarm
            break

        case '0x0023': // Battery Alarm
            break

        case '0x0024': // Supervision Report
        	resultMap = getContactResult('closed')
            break

        case '0x0025': // Restore Report
        	resultMap = getContactResult('open')
            break

        case '0x0026': // Trouble/Failure
            break

        case '0x0028': // Test Mode
            break
        case '0x0000':
			resultMap = createEvent(name: "tamper", value: "clear", isStateChange: true, displayed: false)
            break
        case '0x0004':
			resultMap = createEvent(name: "tamper", value: "detected", isStateChange: true, displayed: false)
            break;
        default:
        	log.warn "Invalid message code in IAS message: ${msgCode}"
    }
    return resultMap
}


private Map getMotionResult(value) {
	String linkText = getLinkText(device)
	String descriptionText = value == 'active' ? "${linkText} detected motion" : "${linkText} motion has stopped"
	return [
		name: 'motion',
		value: value,
		descriptionText: descriptionText
	]
}
def motionON() {
//    logdebug "--- Motion Detected"
    sendEvent(name: "motion", value: "active", displayed:true, isStateChange: true)
    
	//-- Calculate Inactive timeout value
	def motionTimeRun = (settings.motionTime?:0).toInteger()

	//-- If Inactive timeout was configured
	if (motionTimeRun > 0) {
//		logdebug "--- Will become inactive in $motionTimeRun seconds"
		runIn(motionTimeRun, "motionOFF")
	}
}

def motionOFF() {
//	logdebug "--- Motion Inactive (OFF)"
    sendEvent(name: "motion", value: "inactive", displayed:true, isStateChange: true)
}

def panicContact() {
	logdebug "--- Panic button hit"
    sendEvent(name: "contact", value: "open", displayed: true, isStateChange: true)
    runIn(3, "panicContactClose")
}

def panicContactClose()
{
	sendEvent(name: "contact", value: "closed", displayed: true, isStateChange: true)
}

//Converts the battery level response into a percentage to display in ST
//and creates appropriate message for given level

private getBatteryResult(rawValue) {
	def linkText = getLinkText(device)
	def result = [name: 'battery']
	def volts = rawValue / 10
	def excessVolts=3.5			
	def maxVolts=3.0
	def minVolts=2.6
	if (device.data.model.substring(0,3)!='340')	//adjust voltages if not Centralite 3400 or Iris 3405 V2 keypads
		{
		excessVolts=7.2
		maxVolts=6.8
		minVolts=5.2
		}
	if (volts > excessVolts)
		{
		result.descriptionText = "${linkText} battery voltage: $volts, exceeds max voltage: $excessVolts"
		result.value = Math.round(((volts * 100) / maxVolts))
		}
	else
		{
		def pct = (volts - minVolts) / (maxVolts - minVolts)
		result.value = Math.min(100, Math.round(pct * 100))
		result.descriptionText = "${linkText} battery was ${result.value}% $volts volts"
		}
	if (showVolts)			//test if voltage setting is true
	    result.value=rawValue
	return result
}

private getTemperature(value) {
	def celcius = Integer.parseInt(value, 16).shortValue() / 100
//	log.debug "Celcius: $celcius Farenheit: ${celsiusToFahrenheit(celcius) as Integer}"
	if(getTemperatureScale() == "C"){  
		return celcius
	} else {
		return celsiusToFahrenheit(celcius) as Integer
	}
}

private Map getTemperatureResult(value) {
	logdebug 'TEMP'
	def linkText = getLinkText(device)
	if (tempOffset) {
		def offset = tempOffset as int
		def v = value as int
		value = v + offset
	}
	def descriptionText = "${linkText} was ${value}°${temperatureScale}"
	return [
		name: 'temperature',
		value: value,
		descriptionText: descriptionText
	]
}

//------Command handlers------//
private handleArmRequest(message){
	def keycode = new String(message.data[2..-2].join().decodeHex(),"UTF-8")
	def reqArmMode = message.data[0].substring(1)
	//state.lastKeycode = keycode
	logdebug "Received arm command with keycode/armMode: ${keycode}/${reqArmMode}"

	//Acknowledge the command. This may not be *technically* correct, but it works
	/*List cmds = [
				 "raw 0x501 {09 01 00 0${reqArmMode}}", "delay 200",
				 "send 0x${device.deviceNetworkId} 1 1", "delay 500"
				]
	def results = cmds?.collect{ new hubitat.device.HubAction(it,, hubitat.device.Protocol.ZIGBEE) } + createCodeEntryEvent(keycode, reqArmMode)
	*/
//	def results = createCodeEntryEvent(keycode, reqArmMode)
//	List cmds = [
//				 "raw 0x501 {09 01 00 0${reqArmMode}}",
//				 "send 0x${device.deviceNetworkId} 1 1", "delay 100"
//				]
//	def results = cmds?.collect{ new hubitat.device.HubAction(it, hubitat.device.Protocol.ZIGBEE) } + createCodeEntryEvent(keycode, reqArmMode)     
//	log.trace "Method: handleArmRequest(message): "+results
//	return results
//	cmds
	createCodeEntryEvent(keycode, reqArmMode)
}

def createCodeEntryEvent(keycode, armMode) {
	createEvent(name: "codeEntered", value: keycode as String, data: armMode as String, 
				isStateChange: true, displayed: false)
}

private sendStatusToDevice(armModex='') {
	logdebug 'Entering sendStatusToDevice armModex: '+armModex+', Device.armMode: '+device.currentValue('armMode',true)  	
	def armMode=null
	if (armModex=='')
		{
//		logdebug "using device armMode"
		armMode = device.currentValue("armMode",true)
		}
	else
		{
//		logdebug "using passed armModex"
		armMode = armModex
		}
	def status = ''
	if (armMode == null || armMode == 'disarmed') status = 0
	else if (armMode == 'armedAway') status = 3
	else if (armMode == 'armedStay') status = 1	
	else if (armMode == 'armedNight') status = 2
	else logdebug 'Invalid Arm Mode in sendStatusToDevice: '+armMode
	
	// If we're not in one of the 4 basic modes, don't update the status, don't want to override beep timings, exit delay is dependent on it being correct
	if (status != '')
	{
		return sendRawStatus(status)
	}
    else
    {
    	return []
    }
}


private sendRawStatus(status, secs = 00) {
	def seconds=secs as Integer
	logdebug "sendRawStatus info ${zigbee.convertToHexString(status,2)}${zigbee.convertToHexString(seconds,2)} to device..."

    
    // Seems to require frame control 9, which indicates a "Server to client" cluster specific command (which seems backward? I thought the keypad was the server)
    List cmds = ["raw 0x501 {09 01 04 ${zigbee.convertToHexString(status,2)}${zigbee.convertToHexString(seconds,2)}}",
    			 "send 0x${device.deviceNetworkId} 1 1", 'delay 100']
                 
	cmds
//  def results = cmds?.collect{ new hubitat.device.HubAction(it,hubitat.device.Protocol.ZIGBEE) };
//	logdebug "sendRawStatus results"+results
//  return results
}

def notifyPanelStatusChanged(status) {
	//TODO: not yet implemented. May not be needed.
}
//------------------------//

def setDisarmed() {
	logdebug ('setDisarm entered')
	setModeHelper("disarmed",0)
	}
def setArmedAway(def delay=0) { setModeHelper("armedAway",delay) }
def setArmedStay(def delay=0) { setModeHelper("armedStay",delay) }
def setArmedNight(def delay=0) { setModeHelper("armedNight",delay) }
//	Hubitat Command set V1.0.1 Feb 21, 2019, 
//on Mar 31, 2019 HE sent disarm 3 times, ignore when mode is correct on device
//on Apr 19, 2019 Not using HSM commands
def disarm(delay=0) 	
	{
	logdebug ('disarm entered')
//	if (device.currentValue('armMode',true) != 'disarmed')
//		setModeHelper("disarmed",0) 
	}
def armAway(def delay=0)
	{
	logdebug ('armAway entered')
//	if (device.currentValue('armMode',true) != 'armedAway')
//		setModeHelper("armedAway",delay) 
	}
def armHome(def delay=0)
	{
	logdebug ('armHome entered')
//	if (device.currentValue('armMode',true) != 'armedStay')
//		setModeHelper("armedStay",delay)
	}
def armNight(def delay=0) 
	{
	logdebug ('armNight entered')
//	if (device.currentValue('armMode',true) != 'armedNight')
//		setModeHelper("armedNight",delay)
	}

def entry(delay=0)
	{
	logdebug "entry entered delay: ${delay}"
//	setEntryDelay(delay)	//disabled until I understand why this is issued when setting away from actiontiles
	}
	
def setEntryDelay(delay) {
//	setModeHelper("entryDelay", delay)
	sendRawStatus(5, delay) // Entry delay beeps
}

def setExitAway(delay) {
//	setModeHelper("exitDelay", delay)
//	setModeHelper("exitDelay", 0)
	sendRawStatus(10, delay)  // Exit delay
}

def setExitNight(delay) {
	sendRawStatus(9, delay)		//Night delay
	}

def setExitStay(delay) {
	sendRawStatus(8, delay)		//Stay Delay
	}

/*
 *	Alarm Capability Commands
 */

def both()
	{
	siren()
	}
def off()
	{
	if (device.data.model.contains ('3400') || device.data.model.substring(0,3)=='URC')							
		beep(0)							
	else
		{
    	List cmds = ["raw 0x501 {19 01 04 00 00 01 01}",
    			 "send 0x${device.deviceNetworkId} 1 1", 'delay 100']
		cmds
		}
	}
def siren()
	{
/*	device.data.model not available in ST 
 *  siren command does not work on Centralite 3400 V2 and 3400-G (V3) or UEI
 */ 
	if (device.data.model.contains ('3400') || device.data.model.substring(0,3)=='URC')							
		beep(255)							
	else
		{
    	List cmds = ["raw 0x501 {19 01 04 07 00 01 01}",
    	 "send 0x${device.deviceNetworkId} 1 1", 'delay 100']
		cmds
		}
	}
def strobe() 
	{
	sendRawStatus(6)			//blinks Iris light, not sure on Centralite
	}

private setModeHelper(String armMode, delay) {
	logdebug "In setmodehelper armMode: $armMode delay: $delay"
	sendEvent(name: "armMode", value: armMode)
	if (armMode != 'entryDelay')
		{
		def lastUpdate = formatLocalTime(now())
		sendEvent(name: "lastUpdate", value: lastUpdate, displayed: false)
		}
	sendStatusToDevice(armMode)
}

private setKeypadArmMode(armMode){
	Map mode = [disarmed: '00', armedAway: '03', armedStay: '01', armedNight: '02', entryDelay: '', exitDelay: '']
    if (mode[armMode] != '')
    {
		return ["raw 0x501 {09 01 04 ${mode[armMode]}00}",
				 "send 0x${device.deviceNetworkId} 1 1", 'delay 100']
    }
}

def acknowledgeArmRequest(armMode='0'){
	logtrace "entered acknowledgeArmRequest armMode: ${armMode}"
	List cmds = [
				 "raw 0x501 {09 01 00 0${armMode}}",
				 "send 0x${device.deviceNetworkId} 1 1", "delay 100"
				]
//	def results = cmds?.collect{ new hubitat.device.HubAction(it, hubitat.device.Protocol.ZIGBEE) }
//	logtrace "Method: acknowledgeArmRequest(armMode): "+results
//	return results
	cmds

}

def sendInvalidKeycodeResponse(){
	List cmds = [
				 "raw 0x501 {09 01 00 04}",
				 "send 0x${device.deviceNetworkId} 1 1", "delay 100"
				]
				 
	logtrace 'Method: sendInvalidKeycodeResponse(): '+cmds
//	return (collect{ new hubitat.device.HubAction(it, hubitat.device.Protocol.ZIGBEE) }) + sendStatusToDevice()
	cmds
	sendStatusToDevice()
}

def beep(def beepLength = settings.beepLength as Integer) {
	if ( beepLength == null )
	{
		beepLength = 1
	}

	def len = zigbee.convertToHexString(beepLength, 2)
//	List cmds = ["raw 0x501 {09 01 04 05${len}}", 'delay 200',
//				 "send 0x${device.deviceNetworkId} 1 1", 'delay 500']
	List cmds = ["raw 0x501 {09 01 04 05${len}}",
				 "send 0x${device.deviceNetworkId} 1 1", 'delay 100']
	cmds
//	return (cmds?.collect{ new hubitat.device.HubAction(it, hubitat.device.Protocol.ZIGBEE) }
}

//------Utility methods------//

private String swapEndianHex(String hex) {
	reverseArray(hex.decodeHex()).encodeHex()
}

private byte[] reverseArray(byte[] array) {
	int i = 0;
	int j = array.length - 1;
	byte tmp;
	while (j > i) {
		tmp = array[j];
		array[j] = array[i];
		array[i] = tmp;
		j--;
		i++;
	}
	return array
}
//------------------------//

private testCmd(cmd=5,time=15){
	//logtrace zigbee.parse('catchall: 0104 0501 01 01 0140 00 4F2D 01 00 0000 07 00 ')
	//beep(10)
	//test exit delay
	//logdebug device.zigbeeId
	//testingTesting()
	//discoverCmds()
	//zigbee.configureReporting(1,0x20,0x20,3600,43200,0x01)		//battery reporting
	//["raw 0x0001 {00 00 06 00 2000 20 100E FEFF 01}",
	//"send 0x${device.deviceNetworkId} 1 1"]
	//zigbee.command(0x0003, 0x00, "0500") //Identify: blinks connection light
    
	//logdebug 		//temperature reporting  
    
//	return zigbee.readAttribute(0x0020,0x01) + 
//		    zigbee.readAttribute(0x0020,0x02) +
//		    zigbee.readAttribute(0x0020,0x03)
//	if (cmd < 12)
//		sendRawStatus(cmd as Integer, time as Integer)		
//    List cmds = ["raw 0x501 {09 01 03 ${zigbee.convertToHexString(cmd as Integer,2)}${zigbee.convertToHexString(time as Integer,2)}}",
//    			 "send 0x${device.deviceNetworkId} 1 1", 'delay 100']
//	cmds
    List cmds = ["raw 0x501 {19 01 04 07 00 01 01}",
    			 "send 0x${device.deviceNetworkId} 1 1", 'delay 100']
	cmds
}

private discoverCmds(){
	List cmds = ["raw 0x0501 {08 01 11 0011}", 'delay 200',
				 "send 0x${device.deviceNetworkId} 1 1", 'delay 500']
	cmds
}

private testingTesting() {
	logdebug "Delay: "+device.currentState("armMode").toString()
	List cmds = ["raw 0x501 {09 01 04 050A}", 'delay 200',
				 "send 0x${device.deviceNetworkId} 1 1", 'delay 500']
	cmds
}

def logdebug(txt)
	{
   	if (logdebugs)
   		log.debug ("${txt}")
    }
def logtrace(txt)
	{
   	if (logtraces)
   		log.trace ("${txt}")
    }