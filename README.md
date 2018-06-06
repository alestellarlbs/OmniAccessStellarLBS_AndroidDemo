# OmniAccess Stellar LBS Android Demo Application
[![](http://ec2-52-14-144-186.us-east-2.compute.amazonaws.com/logo-black-stellarLBS-VF-1024px_03.png)](https://www.omniaccess-stellar-lbs.com)
[![](https://alencpasst.s3.amazonaws.com/assets/logo_company_ale-b4b4fec5cd922fd3eeea4b6bfc4ef520.png)](https://www.al-enterprise.com)



OmniAccess Stellar LBS Demo Application leverages LBS features on an Android mobile like:

  - Positioning of the user in indoor map
  - Geofencing alerts (display URL when entering a zone)
  - Wayfinding (turn by turn direction to a given Point Of Interest)
  - Analytics
 
But also integrates with ALE's Rainbow in order to:

  - Set a Do Not Disturb status of the user when entering a zone (e.g a meeting room)
  - Track people based on their Rainbow ID

In association with a chatbot, the application also allow to draw the path to the lounge when user is asking to the bot - through Rainbow client - for a drink. Or to go to the toilets, to look for a particular place. It also displays position of colleagues on the indoor map.

Documentation is available online and will help you developing apps on top of OmniAccess Stellar LBS SDK.  
https://docs.omniaccess-stellar-lbs.com/docs/mobile-sdk/

Any questions ? Feel free to raise at : support-stellarlbs@al-enterprise.com


### Recomended skills

* [Android Studio] - IDE to develop Android applications.
* [Stellar LBS SDK] - Alcatel-Lucent Enterprise SDK for location services
* [Rainbow SDK] - ALcatel-Lucent Enterprise SDK for Rainbow client
* [VisioGlobe SDK] - Indoor Mapping provider SDK
* [Mapwize SDK] - Indoor Mapping provider SDK


### Installation

Clone or download the repository and open it with Android Studio 3.+  
A file is missing in the repo. You must develop your own Keys.java implementing the IKeys interface. This file is defining the API keys in use for Stellar LBS, tokens, or indoor maps keys (MapWize, VisioGlobe, MapBox).


### Development

Want to contribute? Great!  
You can fork from Github and submit your modifications.

### Todos

License
----

Apache v2.0  
see licence files.

