<a name="top"></a>
# Nyckelharpa
![image nyckelharpa](images/nyckelharpa.jpg)<br /> 
**The buttons and levers controlling Hubitat Elevation's Home Security Monitor's strings.** 
## Table of Contents 
[&ensp;1. Purpose](#purpose)<br />
[&ensp;2. Features](#features)<br />
[&ensp;3. Donate](#support)<br />
[&ensp;4. Installation](#install)<br />
[&ensp;5. Quick Setup Guide](#setup)<br />
[&ensp;6. Global Settings](#globals)<br />
[&ensp;7. Forced Arming, Adjust HSM Settings](#adjustHSM)<br />
[&ensp;8. Modefix Setup and Usage](#modefix)<br />
[&ensp;9. Talker messages](#talker)<br />
[10. Keypad Device Handler](#keypadDH)<br />
[11. User/Pin Profiles](#userpin)<br />
[12. Create Custom HSM Panic Rule](#panicrules)<br />
[13. Debugging](#testing)<br />
[14. Uninstalling](#uninstall)<br />
[15. Get Help, report an issue, or contact information](#help)<br />
[16. Known Issues](#issues)

<a name="purpose"></a>
## 1. Purpose
Nyckepharpa is a user created Hubitat Home Security Monitor (HSM) extension, providing features not available in HSM. Additionally, it simplifies setting up security related messaging. 

[:arrow_up_small: Back to top](#top)

<a name="features"></a>
## 2. Features<br />
All features are optional, you may use whatever app features you choose. For Example: Forced Arming only. Keypads not required.

* HSM arming when a contact is open, with easy user control<br /> 
Why is this needed? HSM does not arm the system when a contact is open. Examples:<br /><br />It's 1AM, you want to arm the system for night, but a contact is broken.<br /><br />You are away from home, forgot to arm the system, and when you try, oops the back door is open. 
* Adjusts Hubitat's mode when HSM's arm state changes. (HSM adjusts HSM's arm state when the mode changes)
* Provides an easy to use security related message control center with output to TTS, Speakers, and Notification devices such as: Hubitat PhoneApp and Pushover

* Keypads: Centralite/Xfinity 3400, Centalite 3400-G, Iris V2 and V3, and UEI devices may use a ported version of Mitch Pond's Keypad DH, Centralitex Keypad making he keypad function as it did in SmartThings with the SHM Delay App, with an easy to use Pin maintenance module with available Panic pins, burnable pins aka maximum use count, restricted date and time, and restricted keypad devices.
* Keypad Panic Alerts: When the keypad's Panic key is pressed, or a Panic Pin is entered, using the Centralitex Keypad driver, a properly configured active HSM Custom Panic rule, and the Nyckelharpa app when Panic Pins are used:<br /> 
*The system immediately executes the custom HSM rule's alert functions*

* Door Chime Function: Use with Keypads and other devices supporting the "beep" command. Optionally issues beep command when system is Disarmed and selected contact sensor opens. Also can optionally issue beep commands when system is armed and Entry Delay begins. Note: when using a keypad the beep command creates a chime sound.

[:arrow_up_small: Back to top](#top)
<a name="support"></a>
## 3. Support this project
This app is free. However, if you like it, derived benefit from it, and want to express your support, donations are appreciated.
* Paypal: https://www.paypal.me/arnbme 

[:arrow_up_small: Back to top](#top)

<a name="install"></a>
## 4. Installation

There are four modules and an optional Keypad Device Handler (DH) associated with this app  
 <table style="width:100%">
  <tr>
    <th>Module Name</th>
    <th>Function</th>
    <th>Required</th>
  </tr>
  <tr>
    <td>Nyckelharpa</td>
    <td>Parent module. Controls HSM forced arming from a keypad, and User pin verification</td>
    <td>Yes</td>
  </tr>
  <tr>
    <td>Nyckelharpa Modefix</td>
    <td>Adjusts HSM mode when HSM State changes, and forced arming from a non keypd source</td>
    <td>Yes</td>
  </tr>
  <tr>
    <td>Nyckelharpa Talker</td>
    <td>Creates security related output to TTS, speakers, and Notification devices such as: Hubitat Phoneapp and Pushover</td>
    <td>Optional</td>
  </tr>
  <tr>
    <td>Nyckelharpa User</td>
    <td>Maintains User pin codes when using the app's Centralite Keypad DH</td>
    <td>Optional</td>
  </tr>
  <tr>
    <td>Centralite Keypad</td>
    <td>Keypad device handler for models: Centralite/Xfinity 3400, Centralite 3400-G, Iris V2, Iris V3, and UEI. Created and converted to HE by Mitch Pond</td>
    <td>Optional</td>
  </tr>
</table> 

* https://docs.hubitat.com/index.php?title=How_to_Install_Custom_Apps <br />
* let's begin by installing the Nyckelharpa parent module into Hubitat (HE) from this Github repository.<br />
* OAuth is not required and should be skipped.<br /> 
* Save the the Nyckeharpa module
* Using Install's Import button: each module's Github raw address is availabe at the beginning of the module.<br />
* Then install Modefix, Talker, and User, ignore OAuth, and do not add these modules as User Apps.
* In Apps: click the "Add User App" button, select the Nycklharpa, click Done

* Should you be using the user Centralite Keypad driver follow these directions<br />
https://docs.hubitat.com/index.php?title=How_to_Install_Custom_Drivers

*  Next step: Quick Setup Guide

[:arrow_up_small: Back to top](#top)
<a name="setup"></a>
## 5. Quick Setup Guide
Detailed instuctions for each step follow the Quick Setup Guide. Begin by clicking on APPs in the HE menu, then click on Nyckelharpa
1. Setup [Global Settings](#globals), then click Next, then Done.

2. Setup [Forced Arming, Adjust HSM's settings](#adjustHSM) 
3. Create [the Required Modefix profile](#modefix)
4. Optionally create a [Talker profile](#talker)
5. Optionally set a one or more existing keypad devices to use the user provided [Centralite Driver](#keypadDH), then add one or more [User pin profiles](#userpin)
6. Optionally enable [HSM Panic](#panicrules) response by creating a Custom HSM Panic Rule 

[:arrow_up_small: Back to top](#top)

<a name="globals"></a>
## 6. Global Settings. Includes preparation information needed for Forced HSM Arming.

Global Settings is reached by: clicking Apps in the menu, then click the Nyckelharpa app, scroll down to Global Settings, then click  "click to show" 
1. Select any keypad devices using the Nyckelharpa provided Centralite Keypad device driver
* When devices are selected, default options for valid and invalid pin message routing are shown

2. <b>Prepare for Forced Arming:</b> <i>For each armState</i> select real contact sensor devices that will allow HSM arming when the device is Open.
* _When Global Settings is saved, each selectd contact generates a child Virtual Contact Sensor named NCKL-contact-sensor-name that must be used to Adjust HSM Settings for Forced HSM Arming
* Specify optional destinations for "Arming canceled contact open" and "Arming forced messages: Push, SMS, Talk. Optional, but must be set to output these messages
3. Select any contact to be monitored for Open / Close Talker messages only, that are not used with Forced HSM Arming

4. Select any alarms and beeps as required
5. Set the Virtual Child Device prefix, Default NCKL. Once set, it displays but cannot be changed.

6. Set any Hubitat PhoneApp and Pushover messaging devices

7. *When finished, click Next, then click Done*

[:arrow_up_small: Back to top](#top)

<a name="adjustHSM"></a>
## 7. Forced Arming, Adjust HSM Settings.

Forced Arming is a two step process: An standard initial HSM arming that fails normally, followed by a second arming within 15 seconds that forces HSM to arm. It works from any arming source, including: keypads, locks, dashboards, and the HSM app

1. Required Basic Setup: 
* Follow instrucions in Section 6 above, generating the NCKL-child-contact-sensors 

2. Setup HSM's devices for Forced Arming: 
* In Intrusion-Away, Intrusion-Home, and Intrusion-Night, "Contact Sensors": replace the real contact-sensor-name(s) with the virtual NCKL-contact-sensor-name(s)
* In "Configure/Arming/Disarming/Cancel Options", "Delay only for selected doors": replace the real contact-sensor-name(s) with the virtual NCKL-contact-sensor-name(s)

3. How to Force Arm, a two step process: Arming that fails normally, then Arming again within 15 seconds
* Arm system as you would normally. When there is an open contact sensor monitored by Nyckelharpa, the system will not arm as is normal for HSM
* At the initial arm fail: any defined keypads beep twice, Talker issues an alert message including the open sensor(s) and the 15 second forced rearm time 
* Arming the system again, after a minimum of 3 seconds, to a maximum of 15 seconds from the initial arming failure, forces the HSM system to Arm
  
[:arrow_up_small: Back to top](#top)
<a name="modefix"></a>
## 8. Modefix setup and usage

Modefix processes HSM armState changes, and optionally sets the Hubitat HSM mode. _It must be created even when the optional mode change data is empty._ 

(Optional) For each armState set:
* all valid modes for the armState
* the default mode for the armState

Caution: improper armState/mode choices, creates havoc with the system.

[:arrow_up_small: Back to top](#top)
<a name="talker"></a>
## 9. Talker messages

Table with Reason Issued and Message Issued. 
* Pin messages, arming canceled, and arming forced, do not allow for text adjustment. 
 <table style="width:100%">
  <tr>
    <th>Reason Issued</th>
    <th>Default Message</th>
   <th>Destinations</th> 
   <th>Issueing Module</th> 
  </tr>
  <tr>
    <td>Contact Sensor Opens, arm state disarmed</td>
    <td>%device is now open</td>
   <td>TTS, Speaker</td>
   <td>Nyckelharpa</td>
  </tr>
  <tr>
    <td>Contact Sensor Closes, arm state disarmed</td>
   <td>%device is now closed</td>
   <td>TTS, Speaker</td>
   <td>Nyckelharpa</td>
 </tr>
  <tr>
    <td>Exit Delay</td>
    <td>Alarm system is arming in %nn seconds. Please exit the facility</td>
   <td>TTS, Speaker</td>
   <td>Nyckelharpa Modefix</td>
  </tr>
  <tr>
    <td>Entry Delay</td>
    <td>Please enter your pin on the keypad</td>
    <td>TTS, Speaker</td>
   <td>Nyckelharpa</td>
  </tr>
  <tr>
    <td>System Armed</td>
    <td>Alarm System is now armed in %hsmStatus Mode</td>
   <td>TTS, Speaker</td>
   <td>Nyckelharpa Modefix</td>
  </tr>
  <tr>
    <td>System Disarmed</td>
    <td>System Disarmed</td>
   <td>TTS, Speaker</td>
   <td>Nyckelharpa Modefix</td>
  </tr>
  <tr>
    <td>Valid Pin Entered</td>
    <td>%keypad.displayname set HSM state to %armState with pin for %userName</td>
   <td>User Defined in global Settings</td>
   <td>Nyckelharpa</td>
  </tr> 
  <tr>
    <td>Bad Pin Entered</td>
    <td>%keypad.displayname Invalid pin entered: %pinCode</td>
    <td>User Defined in global Settings</td>
    <td>Nyckelharpa</td>
  </tr>
   <tr>
    <td>Arming Canceled Open Contact</td>
    <td>Arming Canceled %contact name(s) is open. Rearming within 15 seconds will force arming </td>
    <td>User Defined in global Settings</td>
    <td>Nyckelharpa</td>    
  </tr> 
  <tr>
    <td>Arming Forced Open Contact</td>
    <td>Arming Forced %contact name(s) is open.</td>
    <td>User Defined in global Settings</td>
   </td><td>Nyckelharpa</td>
  </tr>
  <tr>
    <td>Intrusion Message</td>
   <td>Defined in HSM</td>
    <td>User Defined HSM</td>
   </td><td>HSM</td>
  </tr>
<tr>
    <td>Panic Message</td>
   <td>Defined in HSM Custom Rule</td>
    <td>User Defined Custom HSM Rule (see Section 12)</td>
   </td><td>HSM</td>
  </tr>
  </table>

[:arrow_up_small: Back to top](#top)
<a name="keypadDH"></a>
## 10. Keypad Device Handler

The app's Keypad Device Handler was created by Mitch Pond on SmartThings where it is still used by a few Smartapps including SHM Delay. With Mitch's assistance and Zigbee skills it was ported to HE, then I added the Alarm capability that sounds a fast high pitch tone until set off on the Iris V2, and beeps for 255 seconds on the Centralite. 

_This DH may be used with the Centralite/Xfinity 3400, Centralite 3400-G, Iris V2, Iris V3 and UEI keypads_

1. After installing the keypad DH, edit keypad devices changing Type to Centralitex Keypad, Save Device

2. Remove keypad from HSM. In HSM section Configure Arming/Disarming/Cancel Options --> Use keypad(s) to arm/disarm: 
remove keypads using the Centralitex driver.

3. Add keypad to Nyckelharpa Global Settings

4. Create User pin profiles. When using an Iris V3 User pin code 0000 is required and used for instant arming, but will not disarm. This keypad does not send a pin, even if entered, when arming.

5. Create HSM Custom Panic Rule

6. When using an Iris keypad set if Partial key creates Home (default) or Night arming mode

[:arrow_up_small: Back to top](#top)
<a name="userpin"></a>
## 11. User pin Profiles

When using the app's keypad Device Handler
* For each valid user pin, create a User pin profile

* Pin codes may be restricted by date/time, use count (burnable pins), and keypad device

* To use the Iris V2's instant arming, no pin required, create a User profile with pin code 0000. It is not accepted for OFF

* *The Iris V3 requires a User profile with pin code 0000, or it will not arm.* It is not accepted for OFF.

* You may define "Panic Pins" designed for use on keypads without a Panic key, but may be used on any keypad

[:arrow_up_small: Back to top](#top)

<a name="panicrules"></a>
## 12. Create Custom HSM Panic Rule

*A custom HSM Rule is required* to force an HSM response to a Panic key press, or Panic pin entry, enabling an instant Panic response even when the system is disarmed

1. Click on Apps-->then click Hubitat Safety Monitor 

2. Click on Custom

3. Click Create New Monitoring Rule --> Name this Custom Monitoring Rule-- enter Panic -->

4. Rule settings
What kind of device do you want to use: select Contact Sensor<br />
Select Contact Sensors: *check the Keypad Devices using user provided Centralitex Keypad Driver*, click Update<br />
What do you want to monitor?: Set Sensor Opens on/true<br />
Set Alerts for Text, Audio, Siren and Lights<br />
Click the "Arm This Rule" button<br />
Click Done

5. Do a Panic test: Press the Iris keypad's Panic button, on Centralite 3400-G simultaneously press both "Police Icon" buttons,  or enter a Panic pin number on Centralite 3400 / UEI.

6. The Panic Alert may be stopped by entering a valid user pin on Centralite / UEI, or a valid pin and OFF on an Iris; or the "Cancel Alerts" button from HE App HSM options

[:arrow_up_small: Back to top](#top)
<a name="testing"></a>
## 13. Debugging
1. No entry delay tones on keypad<br />
Keypad may be selected as an Optional Alarm device. Remove it as an Alarm device

2. No exit delay tones<br />
Create and save a Modefix profile

3. Forced arming does not occur<br />
A user reported the Snapshot app somehow interfered with Nyclelharpa's forced arming, and removing or disabling Snapshot fixed the issue. This does not make sense to me, merely reporting what I was told by the user.

[:arrow_up_small: Back to top](#top)
<a name="uninstall"></a>
## 14. Uninstalling
1. If using forced arming, change HSM settings NCKL-child devices to real devices<br />
2. If using Panic Key or Panic pins, remove custom Panic rule from HSM<br />
3. it is now safe to remove Nyckelharpa, child devices are deleted during removal process

[:arrow_up_small: Back to top](#top)
<a name="help"></a>
## 15. Get Help, report an issue, and contact information
* [Use the HE Community's Nyckelharpa forum](https://community.hubitat.com/t/release-nyckelharpa/15062) to request assistance, or to report an issue. Direct private messages to user @arnb

[:arrow_up_small: Back to top](#top)

<a name="issues"></a>
## 16. Known Issues
* Messages need individual custom destination settings

* SMS was disabled by Hubitat, but is still defined as a destination. Do not use SMS

* Iris V3 Keypad: Lights don't shut off when device sitting on table or flat surface. Issue: Devices motion or proximity sensor is activated. Move to edge of table, or lay flat on table, or mount on wall. 

[:arrow_up_small: Back to top](#top)

