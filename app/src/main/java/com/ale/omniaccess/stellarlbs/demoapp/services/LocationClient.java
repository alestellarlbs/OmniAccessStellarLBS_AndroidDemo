/*
 * Copyright (C) 2018 Alcatel-Lucent Enterprise
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ale.omniaccess.stellarlbs.demoapp.services;

import android.util.Log;

import com.ale.omniaccess.stellarlbs.demoapp.util.Alogger;
import com.polestar.naosdk.api.external.NAOERRORCODE;
import com.polestar.naosdk.api.external.NAOLocationHandle;
import com.polestar.naosdk.api.external.NAOLocationListener;
import com.polestar.naosdk.api.external.NAOSensorsListener;
import com.polestar.naosdk.api.external.NAOServiceHandle;
import com.polestar.naosdk.api.external.NAOSyncListener;
import com.polestar.naosdk.api.external.TNAOFIXSTATUS;
import com.ale.omniaccess.stellarlbs.demoapp.MainActivity;


public class LocationClient extends NAOServiceHandle implements NAOLocationListener, NAOSensorsListener, NAOSyncListener {

    private NAOLocationHandle handle; // generic service handle
    private MainActivity mainActivityHandle; // generic service handle
    private String applicationKey;



    public LocationClient(MainActivity mainActivity, String key) {
        Log.i("LocationClient","Location Client Created");
        mainActivityHandle = mainActivity;
        applicationKey = key;
    }

    public void createHandle() {
        Log.i("LocationClient","Handle");
        if (handle == null) {
            handle = new NAOLocationHandle(mainActivityHandle, AndroidService.class, applicationKey, this, this);
            Alogger.setJournal("Location Client", "calling synchronize data with KEY : " + applicationKey);
            handle.synchronizeData(this);
            Log.i("LocationClient","Handle Synchronize");
        }
    }

    public boolean startService() {
        if (handle != null) {
            Log.i("LocationClient ","startService");
            Alogger.setJournal("Location Client", "start location service");
            return handle.start();
        }
        else {
            Log.i("LocationClient ", "startService handle is null");
            return false;
        }
    }

    public void stopService() {
        if (handle == null) {
            Log.i("LocationClient ", "stopService handle pointer is null");
         }
        else {
            handle.stop();
        }
    }

    // NAOLocationListener interface
    @Override
    public void onLocationChanged(android.location.Location location) {
        Log.i("onLocationChanged", location.getLatitude() + "," + location.getLongitude() + "," + location.getAltitude() + "," + location.getAccuracy() + "," + location.getBearing());
        //Alogger.setJournal("Location Client", "location received:" + location.getLatitude() + "," + location.getLongitude());
        mainActivityHandle.setLocation(location);
    }

    @Override
    public void onLocationStatusChanged(TNAOFIXSTATUS status) {
        // Your code here
        Alogger.setJournal("LocationClient", "onLocationStatus Chnaged : " + status.toString());
        Log.i("locationClient ", "onLocationStatusChanged event received");
        if (status.equals(TNAOFIXSTATUS.NAO_FIX_AVAILABLE)) {
            Log.i("locationClient ", "onLocationStatusChanged NAO_FIX_AVAILABLE event received");
        }
        else if (status.equals(TNAOFIXSTATUS.NAO_OUT_OF_SERVICE)) {
            Log.i("locationClient ", "onLocationStatusChanged NAO_OUT_OF_SERVICE event received");
        }
        else if (status.equals(TNAOFIXSTATUS.NAO_TEMPORARY_UNAVAILABLE)) {
            Log.i("locationClient ", "onLocationStatusChanged NAO_TEMPORARY_UNAVAILABLE event received");
        }
        else if (status.equals(TNAOFIXSTATUS.NAO_FIX_UNAVAILABLE)) {
            Log.i("locationClient ", "onLocationStatusChanged NAO_FIX_UNAVAILABLE event received");
        }
    }

    @Override
    public void onEnterSite(java.lang.String name) {
        // Your code here
        Log.i("locationClient ", "onEnterSite event received");
        mainActivityHandle.onEnterSite(name);
    }

    @Override
    public void onExitSite (java.lang.String name) {
        // Your code here
        Log.i("locationClient ", "onExitSite event received");
        mainActivityHandle.onExitSite(name);
    }

    @Override
    public void onError(NAOERRORCODE errCode,
                        java.lang.String message) {
        Log.i("locationClient onError", "event received");
        Log.e(this.getClass().getName(), "onError " + message);
        mainActivityHandle.notifyUser("onError " + errCode + " " + message);
    }

    // NAOSensorsListener interface
    @Override
    public void requiresCompassCalibration() {
        Log.i("locationClient ", "requiresCompassCalibration event received");
        /* Receives notification that the client app needs to display calibration message to the user. */
    }

    @Override
    public void requiresWifiOn() {
        Log.i("locationClient ", "requiresWifiOn event received");
        /*  Receives notification that the Wifi needs to be activated on the device */
    }

    @Override
    public void requiresBLEOn() {
        Log.i("locationClient  ", "requiresBLEOn event received");
        /* Receives notification that the Bluetooth needs to be activated on the device */
    }

    @Override
    public void requiresLocationOn() {
        Log.i("locationClient ", "requiresLocationOn event received");
    /* Receives notification that the Localisation needs to be activated on the device */
    }

    // NAOSyncListener interface
    @Override
    public void onSynchronizationSuccess() {
        Log.i("locationClient ", " onSynchronizationSuccess event received");
        Alogger.setJournal("Location Client", "data synchronization done");
        // REGISTER SITE FOR WAKE UP BASED ON GPS POSITION
       /* NAOServicesConfig.enableOnSiteWakeUp(this, applicationKey, WakeUpClient.class, new NAOWakeUpRegistrationListener() {

            @Override
            public void onStatusChanged(TNAOWAKEUP_REGISTER_STATUS status, String message) {
                //Receive registration status here
            }
        });*/

    }

    @Override
    public void onSynchronizationFailure(NAOERRORCODE errorCode,
                                         java.lang.String message) {
        Log.i("locationClient ", "onSynchronizationFailure event received " + errorCode.toString());
    }
}
