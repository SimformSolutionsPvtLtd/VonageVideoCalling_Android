# Video Call Integration with Vonage OpenTok #

[![Android-Studio](https://img.shields.io/badge/Android%20Studio-4.0+-orange.svg?style=flat)](https://developer.android.com/studio/)
[![Kotlin Version](https://img.shields.io/badge/Kotlin-v1.4.10-blue.svg)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19)


### This applications is provide one to one video calls with using <a href="https://tokbox.com/developer/sdks/android/">Vonage Video API Android SDK</a> ###

![](ezgif.com-gif-maker.gif)


### This application provided the below feature ###

* Picture In Picture Mode
* Enable / disable audio
* Switch camera views

### Pre-requisites ###

* Android studio 4.0+
* Android Build Tools v30.0.2
* Kotlin 1.4.10

### For developers ###

* Clone this repository into your folder
* Create your *SESSION_ID*, *API_KEY*, *TOKEN*  from <a href="https://tokbox.com/developer/guides/create-token/java/">OpenTok Java server-side library</a>

```
 buildTypes.each {
            /* Add your OpenTok Session Id in Double quote Ex. "<session_id>" */
            it.buildConfigField 'String', 'SESSION_ID', '""'
            /* Add your OpenTok API key in Double quote Ex. "<api key>" */
            it.buildConfigField 'String', 'API_KEY', '""'
            /* Add your OpenTok Token in Double quote Ex. "<token>" */
            it.buildConfigField 'String', 'TOKEN','""'
        }
```

* Replace the following empty strings with your value
* Set your gradle & compiler online for first time only to sync gradle
* After complete sync, just press run button to execute code

### Tech/framework used ###

* Opentok library
* 100% kotlin

### Permissions used ###

##### Automatically granted #####

* `INTERNET`: requires for accessing the internet.

##### Requested only when needed #####
* `RECORD_AUDIO`: Requires for supporting Audio recording option to be enabled (by default, its disabled).
* `CAMERA`: Requires for supporting Camera image capture option to be enabled (by default, it’s disabled).

### How to use? ###

* You need to install same build in two different device and open the application. You will directly connect in single video call.

## License

```
Copyright 2020 Simform Solutions

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
