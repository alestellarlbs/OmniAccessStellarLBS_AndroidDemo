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
/* Copyright 2015-2018, Visioglobe SAS

        ALL RIGHTS RESERVED


        LICENSE: Visioglobe grants the user ("Licensee") permission to reproduce,
        distribute, and create derivative works from this Source Code,
        provided that: (1) the user reproduces this entire notice within
        both source and binary format redistributions and any accompanying
        materials such as documentation in printed or electronic format;
        (2) the Source Code is not to be used, or ported or modified for
        use, except in conjunction with Visioglobe SDK; and (3) the
        names of Visioglobe SAS may not be used in any
        advertising or publicity relating to the Source Code without the
        prior written permission of Visioglobe.  No further license or permission
        may be inferred or deemed or construed to exist with regard to the
        Source Code or the code base of which it forms a part. All rights
        not expressly granted are reserved.

        This Source Code is provided to Licensee AS IS, without any
        warranty of any kind, either express, implied, or statutory,
        including, but not limited to, any warranty that the Source Code
        will conform to specifications, any implied warranties of
        merchantability, fitness for a particular purpose, and freedom
        from infringement, and any warranty that the documentation will
        conform to the program, or any warranty that the Source Code will
        be error free.

        IN NO EVENT WILL VISIOGLOBE BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT
        LIMITED TO DIRECT, INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES,
        ARISING OUT OF, RESULTING FROM, OR IN ANY WAY CONNECTED WITH THE
        SOURCE CODE, WHETHER OR NOT BASED UPON WARRANTY, CONTRACT, TORT OR
        OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
        PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM,
        OR AROSE OUT OF USE OR RESULTS FROM USE OF, OR LACK OF ABILITY TO
        USE, THE SOURCE CODE.

        Contact information:  Visioglobe SAS,
        55, rue Blaise Pascal
        38330 Monbonnot Saint Martin
        FRANCE
        or:  http://www.visioglobe.com
*/

package com.ale.omniaccess.stellarlbs.demoapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ale.omniaccess.stellarlbs.demoapp.MainActivity;
import com.ale.omniaccess.stellarlbs.demoapp.R;
import com.visioglobe.visiomoveessential.VMEMapView;
import com.visioglobe.visiomoveessential.interfaces.VMEMapListener;
import com.visioglobe.visiomoveessential.interfaces.VMESearchViewInterface;
import com.visioglobe.visiomoveessential.model.VMEPosition;



public class VisioglobeSearchFragment extends android.support.v4.app.Fragment{

    /** The fragment's map view. */
    private VMEMapView mMapView;

    private MainActivity mainActivity;

    /**
     * This is the callback that will be notified of search view events.
     */
    private VMESearchViewInterface.SearchViewCallback mSearchViewCallback = new VMESearchViewInterface.SearchViewCallback() {
        @Override public void searchView(VMEMapView pMapView, String pPlaceID) {
            mainActivity.setSelectedArea(pPlaceID);
        }
        @Override public void searchViewDidCancel(VMEMapView pMapView) {
        }
    };

    /**
     * The location demo fragment's map listener that will be notified of map events.
     */
    private VMEMapListener mMapListener = new VMEMapListener() {
        @Override public void mapReadyForPlaceUpdate(VMEMapView pMapBlockView) {
            Log.i("VG Search Fragment", "mapReadyForPlaceUpdate ");
        }
        @Override public void mapDidLoad(VMEMapView pMapBlockView) {
            Log.i("VG Search Fragment", "mapDidLoad ");
            mMapView.showSearchViewWithTitle("Search place", mSearchViewCallback);
        }

        @Override public boolean mapDidSelectPlace(VMEMapView pMapBlock, String pPlaceId, VMEPosition pPosition) {
            Log.i("VG Search Fragment", "mapDidSelectPlace ");
            return false;
        }
    };

    public VisioglobeSearchFragment() {
    }

    public void rootMemorize (MainActivity father) {
        mainActivity = father;
    }

    @Override
    public View onCreateView(LayoutInflater pInflater, ViewGroup pContainer, Bundle pSavedInstanceState) {
        if (mMapView == null) {
            // Inflate the fragment's layout
            if (mainActivity.getVenue() == R.id.brestmap) {
                mMapView = (VMEMapView) pInflater.inflate(R.layout.search_fragment_brest_visioglobe, pContainer , false);
            }
            else if ((mainActivity.getVenue() == R.id.colombesmap) || (mainActivity.getVenue() == R.id.edemomap)) {
                mMapView = (VMEMapView) pInflater.inflate(R.layout.search_fragment_colombes_visioglobe, pContainer , false);
            }
            /*else if (mainActivity.getVenue() == R.id.simultracking) {
                mMapView = (VMEMapView) pInflater.inflate(R.layout.search_fragment_monaco_visioglobe, pContainer , false);
            }
            else if (mainActivity.getVenue() == R.id.simulgeofence) {
                mMapView = (VMEMapView) pInflater.inflate(R.layout.search_fragment_monaco_visioglobe, pContainer , false);
            }*/
            // Set up map listener to know when map view has loaded.
            mMapView.setMapListener(mMapListener);
            // Load the map
            mMapView.loadMap();
        }
        return mMapView;
    }

    @Override
    public void onPause () {
        try {
            Log.i("VG Search Fragment", " onPause");
            super.onPause();
            mMapView.onPause();
        }
        catch (Exception e) {
            Log.i("SearchFr pauseEx", e.getMessage());
        }
    }

    @Override
    public void onResume () {
        try {
            Log.i("VG Search Fragment", " onResume");
            super.onResume();
            mMapView.onResume();
        }
        catch (Exception e) {
            Log.i("SearchFr resumeEx", e.getMessage());
        }
    }

}
