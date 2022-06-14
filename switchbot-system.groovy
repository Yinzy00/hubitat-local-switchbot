/*

Driver for local switchbot api by Yari Mariën

*/

import groovy.json.*
//log.debug(new JsonBuilder( httpParams ).toPrettyString());

metadata
{
    definition(name: 'Local SwitchBot System', namespace: 'local_switchbot', author: 'Yinzy00', importUrl: '')
    {
        capability 'Actuator'
        capability 'Initialize'
        capability 'Refresh'

        command 'createChildDevices'
        command 'deleteChildDevices'

        //command "executeScene", ["sceneId"]
        attribute 'status', 'string'
    }
}

preferences
{
    section
    {
        input 'apiUrl', 'text', title: 'Local api url', required: true
        input 'refreshInterval', 'number', title: 'Polling refresh interval in ms (by default every 500ms)', defaultValue: 500
        input name: 'logEnable', type: 'bool', title: 'Enable debug logging', defaultValue: false
    }
}

def logDebug(message) {
    if (logEnable) {
        log.debug(message);
    }
}

def initialize() {
    logDebug('Initializing switchbot system...');
    sendEvent(name: 'status', value: 'unknown');
    //Refresh status & devices
    refresh();
}

def refresh(){
    logDebug('Refreshing switchbot system...');
    checkStatus();
}

def createChildDevices(){
    logDebug('Creating child devices...');
    def apiDevices = getDevicesFromApi();
    if(!apiDevices){
        return;
    }
    // logDebug(new JsonBuilder( apiDevices ).toPrettyString())
    for (device in apiDevices) {
        logDebug('Creating child device: ' + new JsonBuilder( device.customName ).toPrettyString());
        switch (device.type) {
            case 'SwitchbotDeviceWoCurtain':
                logDebug('Creating curtain device: ' + device.id + " - " + device.customName);
                createChildDevice(device.id, device.customName, 'Curtain');
                break;
            default:
                logDebug('Device type not supported: ' + device.type);
                break;
        }
    }
}

def createChildDevice(id, name, deviceType)
{
    def child
    try
    {
        if(!["Bot", "Curtain", "Meter", "Humidifier", "Strip Light"].contains(deviceType.toString()))
        {
            logDebug("createChildDevice: deviceType not supported")
            throw new Exception("deviceType not supported")
        }
            
        return addChildDevice(
        "Local SwitchBot ${deviceType.toString()}", 
        childDni(id, deviceType), 
        [name: childName(name, deviceType), label: childName(name, deviceType), isComponent: false]
        )
    }
    catch (Exception e)
    {
        logDebug("Creating child device failed: ${e.message}")
        return
    }
}

def deleteChildDevices(){
    for(child in getChildDevices())
    {
        deleteChildDevice(child.deviceNetworkId)
    }
}

def childDni(id, deviceType){
    return id.toString() + "-" + deviceType.toString();
}
def childName(name, deviceType){
    return name.toString() + "-" + deviceType.toString();
}


def getHttpParams(urlParams) {
    def url;
    if (apiUrl.endsWith('/')) {
        url = apiUrl + urlParams;
    }
    else {
        url = apiUrl + '/' + urlParams;
    }

    def params =
    [
        uri: url,
        headers:
        [
            'Content-Type' : 'application/json;'
        ]
    ];

    return params;
}

def httpGetRequest(command) {
    try {
        def result;
        def httpParams = getHttpParams(command);
        httpGet(httpParams)
        {
            response ->

                logDebug('Status response: ' + response.data);

		        result = response.data.toString();

                try{
		            result = parseJson(result);
                }
                catch(Exception e){
                    logDebug("Error parsing json: ${e.message}");
                }
        }
        return result;
    }
    catch (e) {
        logDebug('Error while getting data from local api: ' + e.message);
        sendEvent(name: 'status', value: 'error');
    }
}

def sendCommand(command){
    return httpGetRequest(command);
}

def checkStatus(){
    logDebug('Checking status...');
    def status = httpGetRequest('');
    if(status == "succes"){
        sendEvent(name: 'status', value: "ok");
    }
}

//Get devices from local api
def getDevicesFromApi(){
    logDebug('Getting devices from local api...');
    def devices;
    if(device.currentValue("status") == 'ok'){
        devices = httpGetRequest('devices');
        // log.debug(new JsonBuilder( devicesData ).toPrettyString());        
    }
    else{
        logDebug('failed to connect, try refreshing...');
    }
    return devices;
}

def getDeviceId(networkId){
    return networkId.split("-")[0];
}

//TODO:
//Get device states from local api
//Add support for other devices