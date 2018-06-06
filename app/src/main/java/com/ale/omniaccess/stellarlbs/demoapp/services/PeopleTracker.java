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

import com.ale.omniaccess.stellarlbs.demoapp.Keys;
import com.ale.omniaccess.stellarlbs.demoapp.util.HttpHelper;

/**
 * Created by vaymonin on 24/10/2017
 */

public class PeopleTracker {
    static Keys _keys = new Keys();
    private static final String ROOT_URL 		= "https://www.omniaccess-stellar-lbs.com";
    private static final String AUTH_TOKEN 		= _keys.getPeopleTrackingAuthtoken(); // Authentication API of Jerome Elleouet
    private static final String PEOPLE_TRACKING_URL 		= ROOT_URL + "/nao_trackables/record_location.json?site_id=ID&auth_token=" + AUTH_TOKEN;
    private static final PeopleTracker instance = new PeopleTracker();

    public static PeopleTracker instance() {
        return instance;
    }

    //TODO enable in initialization
    private boolean isEnabled = true;
    private boolean isSendingLocation = false;

    public boolean isEnabled(){
        return this.isEnabled;
    }

    public void setEnabled(boolean isEnabled){
        this.isEnabled = isEnabled;
    }

    public void recordLocation(final int siteId, final String user, final double lon, final double lat, final double alt){
        if (isEnabled && !isSendingLocation) {
            isSendingLocation = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String url = PEOPLE_TRACKING_URL.replace("ID", String.valueOf(siteId));
                    HttpHelper.postText(url,
                            "nao_trackable[name]", user,
                            "nao_trackable[lon]", String.valueOf(lon),
                            "nao_trackable[lat]", String.valueOf(lat),
                            "nao_trackable[alt]", String.valueOf(alt)
                    );
                    isSendingLocation = false;
                }
            }).start();


        }
    }
}
