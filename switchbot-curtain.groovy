/*

Driver for local switchbot api by Yari Mariën

*/

metadata
{
    definition(name: 'Local SwitchBot Curtain', namespace: 'local_switchbot', author: 'Yinzy00', importUrl: '')
    {
        capability "Initialize"
        capability "Refresh"
        capability "WindowShade"

        attribute 'status', 'string'
        attribute 'position', 'string'
        attribute 'lightLevel', 'string'
        attribute 'battery', 'string'
    }
}


def initialize()
{
    sendEvent(name: "status", value: "unknown");
    sendEvent(name: "position", value: "unknown");
    sendEvent(name: "lightLevel", value: "unknown");
    sendEvent(name: "battery", value: "unknown");
    refresh()
}

def refresh(){
    // getParent()?.logDebug(getParent()?.getRefreshInterval());
    unschedule(getDeviceState);
    runInMillis(getParent()?.getRefreshInterval(), getDeviceState);
    //TODO:
    //GET DEVICE STATE (position, moving?) from api
}

def open(){
    getParent()?.logDebug("Opening curtain");
    getParent()?.logDebug("devices/id/" + getParent()?.getDeviceId(device.getDeviceNetworkId()) + "/open/");
    
    def value = getParent()?.sendCommand("devices/id/" + getParent()?.getDeviceId(device.getDeviceNetworkId()) + "/open/");
    sendEvent(name: 'status', value: value);
}

def close(){
    def value = getParent()?.sendCommand("devices/id/" + getParent()?.getDeviceId(device.getDeviceNetworkId()) + "/close/");
    sendEvent(name: 'status', value: value);
}

def setPosition(position){
    def value = getParent()?.sendCommand("devices/id/" + getParent()?.getDeviceId(device.getDeviceNetworkId()) + "/runToPos/" + position);
    sendEvent(name: 'status', value: value);
}

def stopPositionChange(){
    def value = getParent()?.sendCommand("devices/id/" + getParent()?.getDeviceId(device.getDeviceNetworkId()) + "/pause/");
    sendEvent(name: 'status', value: value);
}

def startPositionChange(position){
    switch(position){
        case "open":
            open();
            break;
        case "close":
            close();
            break;
    }
}

def getDeviceState(){
    // getParent()?.logDebug("Getting device state");
    def params = getParent()?.getHttpParams("devices/id/" + getParent()?.getDeviceId(device.getDeviceNetworkId()) + "/");
    try{
        asynchttpGet('setState', params);
    }
    catch(Exception e){
        //Server is probably down
    }
    runInMillis(getParent()?.getRefreshInterval(), getDeviceState);
}

def setState(response, data){
    // getParent()?.logDebug(response.data.toString());
    try{
        def responseData = response.data.toString();
        def obj = parseJson(responseData);
        if(obj){
            sendEvent(name: "position", value: obj.state.position);
            sendEvent(name: "lightLevel", value: obj.state.lightLevel);
            sendEvent(name: "battery", value: obj.state.battery);
        }
    }
    catch(Exception e){
        //Server is probably down
    }
}