# Android Pubsub Sample

## Features
* Sign in and login 
  * location ID **NOT** included
* PubSub cloud functions
    * Sending data to Skygear server with a interval of 30 sec
    * Once the data is sent and saved in the Skygear database, send a message back to the dispenser saying that the record is saved successfully

* Pubsub event listener


## Get started

###Android

- You can just download the project and open it with Android Studio.
- Build and run the project on a simulator or an Android device.

###Web Panel
- Visit with your browser at[https://iotsample.skygeario.com/static/index.html](https://iotsample.skygeario.com/static/index.html)

---


## Test Flow
1. You have to signup / login to test out the features.
2. There is a simple [web panel](https://iotsample.skygeario.com/static/index.html) where you can see the app status and ping all devices.
3. Depite you can ping the devices manually, the system already pings the devices every 30 sec.
4. You can see the ping result in the status table if the device replies.
5. Try to send the data with custom content. You should be able to see a notification on the web panel.

## Detail

```text

+--------------+                ping                       +-------------------------+
|  Cloud Code  +------------------------------------------>+                         |
+--------------+                                           |                         |
                                                           |      Android Client     |
+--------------+                ping                       |                         |
|  Web Panel   +------------------------------------------>+                         |
+------+-------+                                           +-----------+-------------+
       ^                                                               |
       |                                                               |
       |                                                               |
       +---------------------------------------------------------------+
                              reply (only on received a ping)


```

- `ping` channel:
  - We publish an event to the `ping` channel whenever we initiate a ping. There are two ways toping the user: 1) Cron Job at Cloud Code, 2) the ping button on the web panel.
  - Both web panel and Android device will subscribe to the `ping` channel after login. 

- `reply` channel:
  - When the devices received an event `ping`, it will publish an event in the `reply` channel. So that the web panel will receive a status update from the device.
  - Note that: the devices will only passively send an update to the `reply` channel. (Of course you can implement your custom logic to make sure the device reports actively)
  - The reply data includes to followings:
     - `device`: the current user id as device id now
     - `platform`: web / android
     - `lastReply`: the current time string at the moment of the reply

- Report `afterSave` notification: 
    - We have implemented a cloud function, which handles the **Report** record `afterSave`. After a Report record is saved, we will push an event to the `report-saved` channel.
    - The web panel has subscribed to the `report-saved` channel, hence you will receive a notification when someone sends a data to the server. 
    - The major difference between this notification and pinging devices is, the Report record has been saved to the cloud database while ping events are volatile. You can fetch back the corresponding Report data later on.

---

## The Android Scaffolding

Note: This app is built based on the Skygear Android Scaffolding template. If you'd like to create a blank Skygear Android Project, you can follow the steps below.

If you're not going to start the app from scratch, please skip this section.
 
### Scaffolding for Skygear Android App

You can start create an android app with [Skygear](https://skygear.io) by the following steps:

1. Install [Android Studio](https://developer.android.com/studio/)
2. Install Android Build Tools (version >= 24.0.0)
3. Register an account on [Skygear Portal](https://portal.skygear.io)
4. Download the [Skygear Starter Project for Android](https://github.com/SkygearIO/skygear-Scaffolding-Android/archive/master.zip).
5. Unzip it and open in Terminal.
6. Declare the location of Android SDK:

  For default Android Studio installation on Mac, the path should be as follow

  `export ANDROID_HOME=$HOME/Library/Android/sdk`

  For Linux distribution, the path default path should be as follow

  `export ANDROID_HOME=/opt/android/sdk`

  For other installation method, like homebrew. Please refer back to the
  installation document.

7. Run a script to update your application settings. For the first time, it
   may take some mintues to download the gradle wrapper:

  `./gradlew -q updateAppSettings`

8. Build your project:

  `./gradlew -q build`

9. Open the project with Android Studio.


You can learn more about the Skygear Android SDK by going to https://docs.skygear.io/guides/quickstart/android/.
