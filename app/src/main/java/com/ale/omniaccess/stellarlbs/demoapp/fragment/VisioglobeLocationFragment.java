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

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ale.omniaccess.stellarlbs.demoapp.MainActivity;
import com.ale.omniaccess.stellarlbs.demoapp.R;
import com.ale.omniaccess.stellarlbs.demoapp.util.Alogger;
import com.visioglobe.visiomoveessential.VMEMapView;
import com.visioglobe.visiomoveessential.interfaces.VMEComputeRouteInterface;
import com.visioglobe.visiomoveessential.interfaces.VMEMapInterface;
import com.visioglobe.visiomoveessential.interfaces.VMEMapListener;
import com.visioglobe.visiomoveessential.interfaces.VMEPlaceInterface;
import com.visioglobe.visiomoveessential.model.VMELocation;
import com.visioglobe.visiomoveessential.model.VMEPosition;
import com.visioglobe.visiomoveessential.model.VMEViewMode;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

//import static com.ale.proserv.geoapp.demo.R.id.visit_button;

/**
 * This fragment is a demo for VisioMove Essential's VMELocationInterface API.
 */
public class VisioglobeLocationFragment extends android.support.v4.app.Fragment {

    /** The fragment's map view. */
    private VMEMapView mMapView;

    private String mDest;

    /** The fragment's layout. */
    private ViewGroup mFragment;

    private MainActivity mainActivity;

    private boolean _mapLoaded;
    private Location currentLocation;
    private String _apiKey;

    private LocationManager mLocationManager;

    public VisioglobeLocationFragment()
    {
        mDest = null;
    }

    public void setApiKey(String apiKey)
    {
        _apiKey = apiKey;
    }
    public void setDestination(String lDest)
    {
        mDest = lDest;
    }

    public void rootMemorize (MainActivity father) {
        mainActivity = father;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater pInflater, ViewGroup pContainer, Bundle pSavedInstanceState) {

        _mapLoaded = false;
        if (mFragment == null)
        {
            mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            mFragment = (ViewGroup) pInflater.inflate(R.layout.location_fragment_visioglobe, pContainer, false); // edemo = colombes
            // Add Menu
            setHasOptionsMenu(true);
            // Fetch the views
            mMapView = (VMEMapView) mFragment.findViewById(R.id.map_view);
            mMapView.setMapHash(_apiKey);
            // Set up map listener to know when map view has loaded.
            mMapView.setMapListener(mMapListener);
            // Load the map
            mMapView.loadMap();
        }
        else if (mMapView == null) {
            Log.i("VG Fragment", " onCreateView mMapView sans fragment");
        }

        return mFragment;
    }

    public void gotoPlaceId(String placeID)
    {
      /*  Location lLoc = new Location(LocationManager.);
        lLoc.setLatitude(45.7424829);
        lLoc.setLongitude(4.8800862);
        lLoc.setAltitude(52);*/

        List<Object> lDests = new java.util.ArrayList<>();
        // final destination = input parameter
        lDests.addAll(Arrays.asList(placeID));
        //lDests.addAll(Arrays.asList("Governement"));
        VMEComputeRouteInterface.RouteDestinationsOrder lDestOrder = VMEComputeRouteInterface.RouteDestinationsOrder.OPTIMAL;

        VMEComputeRouteInterface.RouteRequest lRouteRequest = new VMEComputeRouteInterface.RouteRequest(VMEComputeRouteInterface.RouteRequestType.FASTEST, lDestOrder, false);

        if (currentLocation != null) {
            VMEPosition lVMEPosition = mMapView.createPositionFromLocation(currentLocation);
            lRouteRequest.setOrigin(lVMEPosition);
        }
        else
        {
            // default origin for BREST GROUND FLOORS

            lRouteRequest.setOrigin("Innovation");
        }

        lRouteRequest.addDestinations(lDests);


        if (mMapView != null) { // avoid NPE
            mMapView.computeRoute(lRouteRequest, mRouteCallback);//no cbk
        }

        //reset
        mDest = null;

    }

    public void updateLocation(Location location) {
        Log.i("VG Fragment"," updateLocation");
        if (_mapLoaded)
        {
            if (mainActivity.isDisplayLocationActive())
            {
               Alogger.setJournal("VisioGlobeFragment", "mapLoaded => push position");
                currentLocation = location;

                //JEL+
                // generic code+
                VMELocation lVMELocation = mMapView.createLocationFromLocation(location);
                mMapView.updateLocation(lVMELocation);
                // generic code-
                // WAIT for updateLocation instead of OnCreateView to go to a direction that has been received from an intent
                if (mDest != null) {
                    gotoPlaceId(mDest);
                }
            //JEL-
            }
        }
    }

    @Override
    public void onPause () {
        try {
            Log.i("VG Fragment"," onPause");

            super.onPause();
            mMapView.onPause();

            // these lines were causing crash when pausing the application while load the map
    /*        if (mainActivity.isDisplayLocationActive()){
                // Stop location updates.
                mMapView.updateLocation((VMELocation)null);
            }*/
        }
        catch (Exception e) {
            Log.i("LocationFr pauseEx", e.getMessage());
        }
    }

    @Override
    public void onResume () {
        try {

            Log.i("VG Fragment", " onResume");
            super.onResume();
            mMapView.onResume();
        }
        catch (Exception e) {
            Log.i("LocationFr resumeEx", e.getMessage());
        }
    }

    /**
     * The location demo fragment's map listener that will be notified of map events.
     */
    private VMEMapListener mMapListener = new VMEMapListener() {
        @Override public void mapReadyForPlaceUpdate(VMEMapView pMapBlockView) {
            Log.i("VG Fragment", "mapReadyForPlaceUpdate ");
        }

        @Override public void mapDidLoad(VMEMapView pMapBlockView) {
            Log.i("VG Fragment", "mapDidLoad");
            _mapLoaded = true;
            if (mainActivity.isDisplayLocationActive()) {
                if (currentLocation != null) {
                    VMELocation lVMELocation = mMapView.createLocationFromLocation(currentLocation);
                    Log.i("VG Fragment", "mapDidLoad updatelocation");

                    mMapView.updateLocation(lVMELocation);
                }
                //not the best place to apply code : need at least 1 position to be set by NAO
               /* if (mDest != null) {
                    gotoPlaceId(mDest);
                }*/
            }

        /*    if (mainActivity.startServices()) {
                Log.i("VG Fragment ", "startService ok ");
                // RAINBOW STUFF
              //  mainActivity.connectUserToRainbow();
            }
            else {
                Log.i("VG Fragment ", "startService Nok ");
            }*/
        }

        @Override public boolean mapDidSelectPlace(VMEMapView pMapBlock, String pPlaceId, VMEPosition pPosition) {
            Log.i("VG Fragment", "mapDidSelectPlace ");
            return false;
        }

    };

    public void ComputeRoute() {
        Log.i(" VG Fragment", "ComputeRoute ");
        String dest = null;

        String area = mainActivity.getSelectedArea();

        if (area == null || area.isEmpty()) {
            dest = "service_custo";
            Log.i(" VGFragment", "ComputeRoute -->service_custo");
        } else {
            Log.i(" VG Fragment", "ComputeRoute -->"+area);
            dest = area;
        }

        VMEComputeRouteInterface.RouteRequest lRouteRequest = new VMEComputeRouteInterface.RouteRequest(VMEComputeRouteInterface.RouteRequestType.FASTEST, VMEComputeRouteInterface.RouteDestinationsOrder.OPTIMAL);


        if (currentLocation != null) {
            VMEPosition lVMEPosition = mMapView.createPositionFromLocation(currentLocation);
            lRouteRequest.setOrigin(lVMEPosition);
        }
        else lRouteRequest.setOrigin((VMEPosition)null);

        lRouteRequest.addDestination(dest);

        Log.i(" VG Fragment", " DisplayComputeRoute ");
        mMapView.computeRoute(lRouteRequest, mRouteCallback);
        Log.i(" VG Fragment", "ended ComputeRoute ");
    }

    /**
     * The callback that will be notified of route events.
     */
    private VMEComputeRouteInterface.ComputeRouteCallback mRouteCallback = new VMEComputeRouteInterface.ComputeRouteCallback() {
        @Override public boolean computeRouteDidFinish(VMEMapView pMapView, VMEComputeRouteInterface.RouteRequest pRouteRequest, VMEComputeRouteInterface.RouteResult var3) {
            if (getContext() != null) {
                // toast notification on app
                // Toast.makeText(getContext(), "computeRouteDidFinish", Toast.LENGTH_LONG).show();
            }

            Log.i("VG Fragment ","computeRouteDidFinish");
            return true;
        }
        @Override public void computeRouteDidFail(VMEMapView pMapView, VMEComputeRouteInterface.RouteRequest pRouteRequest, String pError) {
            if (getContext() != null) {

                Toast.makeText(getContext(), "computeRouteDidFail" + pError, Toast.LENGTH_LONG).show();
                mainActivity.displaySearchFragment();
            }
            Log.i("VG Fragment ","computeRouteDidFail"+pError);
        }
    };

    public void setonMap(String name, String lat, String lng, String alt, String date, String categorie, Boolean isOnSite) {
        Location lLoc = new Location("ALE");
        lLoc.setLatitude(Double.valueOf(lat));
        lLoc.setLongitude(Double.valueOf(lng));
        lLoc.setAltitude(Double.valueOf(alt));

        VMEPosition lPos = mMapView.createPositionFromLocation(lLoc);
        JSONObject lPlaceData = null;

        try {
            lPlaceData = new JSONObject();
            lPlaceData.put("name", name);
            lPlaceData.put("description", "<b>Information about" + name + "'s position</b> <hr>is currently on site : " + isOnSite + "<hr>last known position date : " + date + "<hr>people category : " + categorie);
            lPlaceData.put("icon", "http://ec2-52-14-144-186.us-east-2.compute.amazonaws.com/avatar.png");
        }
        catch (Exception e)
        {
            e.getMessage();
        }
        //note: visiogloeb not ready to upload (rainbow) avatars locally - need URL from the web => default avatar
        Uri lIconUri = Uri.parse("http://ec2-52-14-144-186.us-east-2.compute.amazonaws.com/avatar.png");

        Boolean lRetVal = mMapView.addPlace("jel_id",
                lIconUri,
                lPlaceData,
                lPos,
                new VMEPlaceInterface.VMEPlaceSize(3.0f),
                VMEPlaceInterface.VMEPlaceAnchorMode.BOTTOM_CENTER,
                VMEPlaceInterface.VMEPlaceAltitudeMode.RELATIVE,
                VMEPlaceInterface.VMEPlaceDisplayMode.OVERLAY,
                VMEPlaceInterface.VMEPlaceOrientation.newPlaceOrientationFacing(),
                new VMEPlaceInterface.VMEPlaceVisibilityRamp());

        VMEMapInterface.CameraUpdate lCameraUpdate = new VMEMapInterface.CameraUpdate.Builder()
                .setTargets(Arrays.asList(lPos))
                .setViewMode(VMEViewMode.FLOOR)
                .setHeading(VMEMapInterface.CameraHeading.newPlaceID("jel_id"))
                .build();
        mMapView.animateCamera(lCameraUpdate);
    }
   // mMapView.setPlaceSize(lCatMapBundle, new VMEPlaceInterface.VMEPlaceSize(20.0f), true);
}
