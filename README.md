# Hubitat local switchbot

Drivers for hubitat integration of [my local switchbot api](https://github.com/Yinzy00/local-switchbot-api).

Drivers are created using my api based on [node-switchbot](https://www.npmjs.com/package/node-switchbot) package.

**To use drivers you need:**
- [My local switchbot api](https://github.com/Yinzy00/local-switchbot-api)

## Installation

**Make sure the [local api](https://github.com/Yinzy00/local-switchbot-api) is running**

1. Add all needed driver code to the hubitat drivers code section.
2. Create a new virtual device of type `Local SwitchBot System`.
3. Set `Local api url`.
4. Set `polling refresh interval` in ms.
5. Save these settings.
6. Use the `Create Child Devices` button to create hubitat devices for each found switchbot device.

**Now you are ready to go!**

## Community

* [SwitchBot (Official website)](https://www.switch-bot.com/)
* [Facebook @SwitchBotRobot](https://www.facebook.com/SwitchBotRobot/) 
* [Twitter @SwitchBot](https://twitter.com/switchbot) 
