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
    }
}


def initialize()
{
    sendEvent(name: "status", value: "unknown")
    sendEvent(name: "position", value: "unknown")
    refresh()
}


def refresh(){
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