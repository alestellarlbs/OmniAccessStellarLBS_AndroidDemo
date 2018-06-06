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

/*
        MIT License

        Copyright (c) 2017 Mapwize

        Permission is hereby granted, free of charge, to any person obtaining a copy
        of this software and associated documentation files (the "Software"), to deal
        in the Software without restriction, including without limitation the rights
        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
        copies of the Software, and to permit persons to whom the Software is
        furnished to do so, subject to the following conditions:

        The above copyright notice and this permission notice shall be included in all
        copies or substantial portions of the Software.

        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
        SOFTWARE.
        */

package com.ale.omniaccess.stellarlbs.demoapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ale.infra.contact.RainbowPresence;
import com.ale.omniaccess.stellarlbs.demoapp.fragment.VisioglobeLocationFragment;
import com.ale.omniaccess.stellarlbs.demoapp.fragment.VisioglobeSearchFragment;
import com.ale.omniaccess.stellarlbs.demoapp.services.CustomSupportMapFragment;
import com.ale.omniaccess.stellarlbs.demoapp.services.GeofencingClient;
import com.ale.omniaccess.stellarlbs.demoapp.services.LocationClient;
import com.ale.omniaccess.stellarlbs.demoapp.services.ManualIndoorLocationProvider;
import com.ale.omniaccess.stellarlbs.demoapp.services.PeopleTracker;
import com.ale.omniaccess.stellarlbs.demoapp.services.RainbowManager;
import com.ale.omniaccess.stellarlbs.demoapp.services.WebFragment;
import com.ale.omniaccess.stellarlbs.demoapp.util.Alogger;
import com.ale.omniaccess.stellarlbs.demoapp.util.HttpHelper;
import com.ale.omniaccess.stellarlbs.demoapp.util.MapwizeSearchResultsListAdapter;
import com.ale.omniaccess.stellarlbs.demoapp.util.MapwizeSuggestionWrapper;
import com.ale.rainbowsdk.RainbowSdk;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.SphericalUtil;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.polestar.naosdk.api.external.NAOAlertRule;
import com.polestar.naosdk.api.external.NaoAlert;
import com.polestar.naosdk.api.external.TALERTRULE;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import io.indoorlocation.core.IndoorLocation;
import io.mapwize.mapwizeformapbox.MapOptions;
import io.mapwize.mapwizeformapbox.MapwizePlugin;
import io.mapwize.mapwizeformapbox.api.Api;
import io.mapwize.mapwizeformapbox.api.ApiCallback;
import io.mapwize.mapwizeformapbox.api.SearchParams;
import io.mapwize.mapwizeformapbox.model.Direction;
import io.mapwize.mapwizeformapbox.model.LatLngFloor;
import io.mapwize.mapwizeformapbox.model.MapwizeObject;
import io.mapwize.mapwizeformapbox.model.Place;
import io.mapwize.mapwizeformapbox.model.Venue;

import static android.view.View.GONE;



public class MainActivity extends AppCompatActivity {
    //TODO Log into file (debug mode in preference)
    private String selectedArea = "";
    Keys _keys = new Keys();

    private int venue;

    private LocationClient locationClient;
    private GeofencingClient geofencingClient;
    private VisioglobeLocationFragment visioglobeLocationFragment;
    private VisioglobeSearchFragment visioglobeSearchFragment;

    private MapView mapView;
    private MapwizePlugin mapwizePlugin;
    private CustomSupportMapFragment mapFragment = null;
    private ManualIndoorLocationProvider manualIndoorLocationProvider;
    private String venueId;
    private MapboxMap mapboxMapp;

    boolean isServiceStarted;

    private FusedLocationProviderClient mFusedLocationClient;

    private FloatingSearchView floatingSearchView;
    private Stack<Handler> handlerStack = new Stack<>();

    private Place lastClickedPlace;
    private boolean debugMode = false;
    private Location debugLocation;

    private Direction saveDirection;

    private WebFragment webFragment;
    private boolean isWebFragmentVisible = false;

    private RainbowManager _Rainbow;
    private Boolean _trackingStatus;
    private int _trackingCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Jerome Elleouet +

        String sd = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = sd + "/ALE LBS/stellarlbsdemo.log";

        String filePattern = "%d - [%c] - %p : %m%n";
        int maxBackupSize = 10;
        long maxFileSize = 1024 * 1024;

        try {
            Alogger.Configure(fileName, filePattern, maxBackupSize, maxFileSize);
            Alogger.setAuthorization(true);
        } catch (Exception e) {
            Log.e("MainActivity", "error trying to log : " + e.getMessage());
        }
        // Jerome Elleouet -

        Alogger.setJournal("MainActivity", "Logger Initialized - OnCreate MainActivity");

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("LBSSettings", Context.MODE_PRIVATE);
        setVenue(StringToVenue(sharedPref.getString("venue", "eDEMO_VISIOGLOBE")));

        _trackingStatus = sharedPref.getBoolean("tracking", false);
        _trackingCount = 0;

        _Rainbow = new RainbowManager();
        String login = sharedPref.getString("rainbowlogin", "");
        String password = sharedPref.getString("rainbowpassword", "");
        if ((!login.isEmpty()) && (!password.isEmpty())) {
            _Rainbow.connectUserToRainbow(login, password);
        }

        Alogger.setJournal("MainActivity", "Venue from preferences = " + String.valueOf(getVenue()));
        Log.i("debug", String.valueOf(getVenue()));
        manualIndoorLocationProvider = new ManualIndoorLocationProvider();
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_CONTACTS}, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.main_content).setElevation(5);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.app_bar_logo);
        Mapbox.getInstance(this, _keys.getMpbxApiKey());
        mapView = new MapView(this);
        floatingSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //autoLocation();

        fetchLocationData(getVenue());
    }

    private void CopyFileForEmulation() {

        String dest_dir_path = this.getExternalFilesDir(null).getAbsolutePath() + "/.nao/replay";
        File dest_dir = new File(dest_dir_path);
        try {
            createDir(dest_dir);
            InputStream in = getApplicationContext().getAssets().open("colombes.gwl");
            OutputStream out = new FileOutputStream(dest_dir_path + "/colombes.gwl");

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
                out.write(buf, 0, len);
            in.close();
            out.close();
        } catch (Exception e) {
            Alogger.setJournal("MainActivity", "exception : " + e.getMessage());
        }
    }

    void DeleteEmulationFile() {
        // Get path for the file on external storage.  If external
        // storage is not currently mounted this will fail.
        String dest_file_path = this.getExternalFilesDir(null).getAbsolutePath() + "/.nao/replay/colombes.gwl";
        File file = new File(dest_file_path);

        if (file != null) {
            file.delete();
        }
    }


    public void createDir(File dir) throws IOException {
        if (dir.exists()) {
            if (!dir.isDirectory()) {
                throw new IOException("Can't create directory, a file is in the way");
            }
        } else {
            dir.mkdirs();
            if (!dir.isDirectory()) {
                throw new IOException("Unable to create directory");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.activityResumed();
    }

    @Override
    protected void onDestroy() {
        // SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("LBSSettings", Context.MODE_PRIVATE);

        Log.i("debugg", "destroy" + getVenue());
        sharedPref.edit().putString("venue", venueToString(getVenue())).apply();
        _Rainbow.disconnectUserFromRainbow();
        stopServices();
        super.onDestroy();
    }

    public void fetchLocation_withVisioGlobe(final String apikey, final String mapKey) {
        mapFragment = null;
        findViewById(R.id.floating_search_view).setVisibility(GONE);
        findViewById(R.id.button_container).setVisibility(GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        visioglobeLocationFragment = new VisioglobeLocationFragment();
        visioglobeLocationFragment.rootMemorize(this);
        visioglobeLocationFragment.setApiKey(mapKey);
        visioglobeSearchFragment = new VisioglobeSearchFragment();
        visioglobeSearchFragment.rootMemorize(this);
        fragmentManager.beginTransaction().replace(R.id.main_content, visioglobeLocationFragment).commit();
        if (locationClient != null) {
            stopServices();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    locationClient = new LocationClient(MainActivity.this, apikey);
                    geofencingClient = new GeofencingClient(MainActivity.this, apikey);
                    startServices();

                }
            }, 3000);
        } else {
            locationClient = new LocationClient(this, apikey);
            geofencingClient = new GeofencingClient(this, apikey);
            startServices();

        }
    }

    public void fetchLocation_withMapwize(int venue) {

        String apikey = _keys.getAppBrestKey(); //default brest

        LatLng patagonia = new LatLng(48.44159, -4.41268);//default brest
        if (venue == R.id.brestmapbox) {
            apikey = _keys.getAppBrestKey();
            patagonia = new LatLng(48.44159, -4.41268);
        } else if (venue == R.id.buenosairesmap) {
            apikey = _keys.getAppArgKey();
            patagonia = new LatLng(-34.526517, -58.470869);
        } else if (venue == R.id.ibmmap) {
            apikey = _keys.getAppIbmKey();
            patagonia = new LatLng(48.9063873, 2.262095);
        } else if (venue == R.id.HHCSXBmap) {
            apikey = _keys.getAppHHCSXBKey();
            patagonia = new LatLng(48.5776435, 7.7381203);
        }

        final String finalApiKey = apikey;

        findViewById(R.id.floating_search_view).setVisibility(View.VISIBLE);
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        MapboxMapOptions options = new MapboxMapOptions();
        options.camera(new CameraPosition.Builder().target(patagonia).zoom(18).build());
        // if (mapFragment == null) {
        mapFragment = CustomSupportMapFragment.newInstance(options);
        transaction.replace(R.id.main_content, mapFragment, "com.mapbox.map");
        transaction.commit();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMapp = mapboxMap;
                MapOptions options = new MapOptions.Builder().showUserPositionControl(true).build();
                mapwizePlugin = new MapwizePlugin(mapFragment.getMapView(), mapboxMap, options);
                configureMapbox(mapboxMap);
            }
        });
        //}

        if (locationClient != null) {
            stopServices();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    locationClient = new LocationClient(MainActivity.this, finalApiKey);
                    geofencingClient = new GeofencingClient(MainActivity.this, finalApiKey);
                    startServices();
                }
            }, 3000);
        } else {
            locationClient = new LocationClient(this, apikey);
            geofencingClient = new GeofencingClient(this, apikey);
            startServices();
        }

    }

    public String venueToString(int venue) {
        String strVenue;
        switch (venue) {
            case R.id.brestmap:
                strVenue = "BREST_VISIOGLOBE";
                break;
            case R.id.brestmapbox:
                strVenue = "BREST_MAPWIZE";
                break;
            case R.id.colombesmap:
                strVenue = "COLOMBES_VISIOGLOBE";
                break;
            case R.id.buenosairesmap:
                strVenue = "BUEONOSAIRES_MAPWIZE";
                break;
            case R.id.ibmmap:
                strVenue = "IBM_MAPWIZE";
                break;
            case R.id.HHCSXBmap:
                strVenue = "HHC_MAPWIZE";
                break;
            case R.id.edemomap:
                strVenue = "eDEMO_VISIOGLOBE";
                break;
            default:
                strVenue = "eDEMO_VISIOGLOBE";
        }
        return strVenue;
    }

    public int StringToVenue(String strVenue) {
        int venue;
        switch (strVenue) {
            case "BREST_VISIOGLOBE":
                venue = R.id.brestmap;
                break;
            case "BREST_MAPWIZE":
                venue = R.id.brestmapbox;
                break;
            case "COLOMBES_VISIOGLOBE":
                venue = R.id.colombesmap;
                break;
            case "BUEONOSAIRES_MAPWIZE":
                venue = R.id.buenosairesmap;
                break;
            case "IBM_MAPWIZE":
                venue = R.id.ibmmap;
                break;
            case "HHC_MAPWIZE":
                venue = R.id.HHCSXBmap;
                break;
            case "eDEMO_VISIOGLOBE":
                venue = R.id.edemomap;
                break;
            default:
                venue = R.id.edemomap;
        }
        return venue;
    }

    //TODO Multisite API Key and test map provider instead of site?
    //TODO find a way to center on coordinate without testing venue, see changeMap method (base loading in GPS/default map)
    public void fetchLocationData(int venue) {

        // REMOTE DEMO ?
        if (venue == R.id.edemomap) {
            Alogger.setJournal("MainActivity", "fetch EDEMO site with VISIO & key:" + _keys.getAppColeDemoKey());
            CopyFileForEmulation(); // Copy GWL file for Colombes
            fetchLocation_withVisioGlobe(_keys.getAppColeDemoKey(), _keys.getVisioMapColKey());
            return;
        } else {
            DeleteEmulationFile();
        }


        // NON REMOTE DEMO CASE
        //Jerome Elleouet -
        if (venue == R.id.brestmap) {
            Alogger.setJournal("MainActivity", "fetch brest site with VISIO & key:" + _keys.getAppBrestKey());
            fetchLocation_withVisioGlobe(_keys.getAppBrestKey(), _keys.getVisioMapBrestKey());

        } else if (venue == R.id.colombesmap) {
            Alogger.setJournal("MainActivity", "fetch COLOMBES site with VISIO & key:" + _keys.getAppColKey());
            fetchLocation_withVisioGlobe(_keys.getAppColKey(), _keys.getVisioMapColKey());
        } else    //MAPWIZE
        {
            fetchLocation_withMapwize(venue);
        }


    }

    public void autoLocation() {

        class VenueLocation {
            private com.google.android.gms.maps.model.LatLng latLng;
            private int id;
            private double distanceToUser;

            public VenueLocation(double latitude, double longitude, int id, Location location) {
                this.latLng = new com.google.android.gms.maps.model.LatLng(latitude, longitude);
                this.id = id;
                this.distanceToUser = SphericalUtil.computeDistanceBetween(new com.google.android.gms.maps.model.LatLng(location.getLatitude(), location.getLongitude()), this.latLng);
            }

            public int getId() {
                return id;
            }

            public double getDistanceToUser() {
                return distanceToUser;
            }
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location == null) {
                    Alogger.setJournal("MainActivity", "autolocation fails : set Location from preferences and not GPS");
                    fetchLocationData(getVenue());
                    return;
                }
                ArrayList<VenueLocation> venues = new ArrayList<>();
                venues.add(new VenueLocation(48.44159, -4.41268, R.id.brestmapbox, location));
                venues.add(new VenueLocation(48.9065881, 2.2620319, R.id.ibmmap, location));
                venues.add(new VenueLocation(48.9339509, 2.2523098, R.id.colombesmap, location));
                venues.add(new VenueLocation(-34.5265248, -58.4709851, R.id.buenosairesmap, location));
                // Jerome Elleouet
                venues.add(new VenueLocation(48.5776435, 7.7381203, R.id.HHCSXBmap, location));
                VenueLocation tampon = venues.get(0);
                for (VenueLocation object : venues) {
                    if (object.getDistanceToUser() < tampon.getDistanceToUser()) {
                        tampon = object;
                    }
                }
                setVenue(tampon.getId());
                fetchLocationData(getVenue());
            }
        });
    }

    //********************OMNIACCESS STELLAR LBS********************************
    //TODO ->GeofencingClient
    public void onFireNaoAlert(NaoAlert alert) {
        java.util.ArrayList<NAOAlertRule> lList = alert.getRules();
        if (lList != null) {
            if (!lList.isEmpty()) {
                NAOAlertRule lRule = lList.get(0);
                if (lRule != null) {
                    if (lRule.getType() == TALERTRULE.ENTERGEOFENCERULE) {
                        String lContent = alert.getContent();
                        if (lContent != null) {
                            if (!lContent.isEmpty()) {
                                if (lContent.startsWith("http")) {
                                    Log.i("Webview Geofence", "open: " + alert.getContent());
                                    Log.i("Webview", "Activity Visible: " + MainApplication.isActivityVisible());
                                    if (MainApplication.isActivityVisible()) {
                                        isWebFragmentVisible = true;
                                        findViewById(R.id.button_container).setVisibility(GONE);
                                        findViewById(R.id.floating_search_view).setVisibility(GONE);
                                        webFragment = new WebFragment();
                                        webFragment.setUrl(alert.getContent());
                                        getFragmentManager().beginTransaction().add(R.id.main_content, webFragment, "webFragment").addToBackStack(null).commit();
                                        try {
                                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                        } catch (Exception e) {
                                            Log.e("Exception", e.getMessage());
                                        }
                                        getSupportActionBar().setDisplayShowHomeEnabled(true);
                                    } else {
                                        Intent launchActivity = new Intent(MainActivity.this, BrowserActivity.class);
                                        launchActivity.putExtra("url", lContent);
                                        startActivity(launchActivity);
                                    }
                                } else if (lContent.startsWith("<DND>")) {
                                    _Rainbow.setPresence(RainbowPresence.DND);
                                }
                            }
                        }
                    } else if (lRule.getType() == TALERTRULE.EXITGEOFENCERULE) {
                        String lContent = alert.getContent();
                        if (lContent != null) {
                            if (!lContent.isEmpty()) {
                                if (lContent.startsWith("<DND>")) {
                                    _Rainbow.setPreviousPresence();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void onEnterGeofence(int regionId, java.lang.String regionName) {

    }

    public void onExitGeofence(int regionId, java.lang.String regionName) {
    }

    public void onEnterSite(String name) {

    }

    public void onExitSite(String name) {

    }

    public void notifyUser(String msg) {
    }

    public void setLocation(Location location) {
        //TODO mapwize: convert altitude to floor
        Log.i("MainActivityLocation", "setLocation ?");
        if (location == null) {
            Log.i("MainActivityLocation", "Location is null");
            return;
        }

        Log.i("MainActivityLocation", String.valueOf(location.getAltitude()));
        if (debugMode == true) {
            Alogger.setJournal("MainActivity", "debugging mode ON");
            location = debugLocation;
        }

        if (getVenue() == R.id.brestmapbox || getVenue() == R.id.ibmmap || getVenue() == R.id.buenosairesmap || getVenue() == R.id.HHCSXBmap) { // MAPBOX
            Alogger.setJournal("MainActivity", "updateLocation on Mapwize fragment");
            this.updateLocation(location);
        } else if ((getVenue() == R.id.brestmap) || (getVenue() == R.id.colombesmap) || (getVenue() == R.id.edemomap)) // VISIOGLOBE
        {
            Alogger.setJournal("MainActivity", "updateLocation on VisioGlobe fragment");
            visioglobeLocationFragment.updateLocation(location);
        }
        //Jerome Elleouet +
        if (_trackingStatus) {
            if (location != debugLocation) {
                if (_trackingCount == 0) { // only every 30 Seconds
                    _trackingCount = 1;
                    if (getVenue() == R.id.brestmapbox) {
                        PeopleTracker.instance().recordLocation(_keys.getBrestSiteId(), getTrackingId(), location.getLongitude(), location.getLatitude(), location.getAltitude());
                    } else if (getVenue() == R.id.brestmap) {
                        PeopleTracker.instance().recordLocation(_keys.getBrestSiteId(), getTrackingId(), location.getLongitude(), location.getLatitude(), location.getAltitude());
                    } else if (getVenue() == R.id.colombesmap) {
                        PeopleTracker.instance().recordLocation(_keys.getColsiteId(), getTrackingId(), location.getLongitude(), location.getLatitude(), location.getAltitude());
                    } else if (getVenue() == R.id.edemomap) {
                        PeopleTracker.instance().recordLocation(_keys.getColeDemositeId(), getTrackingId(), location.getLongitude(), location.getLatitude(), location.getAltitude());
                    } else if (getVenue() == R.id.ibmmap) {
                        PeopleTracker.instance().recordLocation(_keys.getIBMsiteId(), getTrackingId(), location.getLongitude(), location.getLatitude(), location.getAltitude());
                    } else if (getVenue() == R.id.buenosairesmap) {
                        PeopleTracker.instance().recordLocation(_keys.getArgsiteId(), getTrackingId(), location.getLongitude(), location.getLatitude(), location.getAltitude());
                    } else if (getVenue() == R.id.HHCSXBmap) {
                        PeopleTracker.instance().recordLocation(_keys.getHHCSXBsiteId(), getTrackingId(), location.getLongitude(), location.getLatitude(), location.getAltitude());
                    }
                } else {
                    if (_trackingCount == 30)
                        _trackingCount = 0;
                    else
                        _trackingCount++;
                }
            }
        }
        //Jerome Elleouet -
    }

    private String getTrackingId() {
        String id = null;

        String username = RainbowSdk.instance().myProfile().getConnectedUser().getFirstName() + " " + RainbowSdk.instance().myProfile().getConnectedUser().getLastName();
        if (username != " ")
            id = username;
        else
            id = getDeviceName();

        return id;
    }

    private String getDeviceName() {
        String deviceName = null;
        try {
            android.bluetooth.BluetoothAdapter myDevice = android.bluetooth.BluetoothAdapter.getDefaultAdapter();
            deviceName = myDevice.getName();
            if (deviceName == null) {
                deviceName = android.os.Build.MODEL + "-" + Build.MANUFACTURER;
            }
        } catch (Exception exc) {
            // just to protect against app crash
        }
        return deviceName;
    }
    //********************OMNIACCESS STELLAR LBS********************************


    //********************VENUE SELECTION***************************************
    public int getVenue() {
        return venue;
    }

    public void setVenue(int venue) {
        this.venue = venue;
    }
    //********************VENUE SELECTION***************************************


    //******************************MENU****************************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        try {
            int venue = getVenue();
            if (venue == R.id.edemomap) {
                menu.findItem(R.id.edemomap).setChecked(true);
            } else {
                menu.findItem(R.id.edemomap).setChecked(false);
                if (getVenue() == R.id.brestmapbox || getVenue() == R.id.ibmmap || getVenue() == R.id.buenosairesmap || getVenue() == R.id.HHCSXBmap || getVenue() == R.id.brestmap || getVenue() == R.id.colombesmap) {
                    menu.findItem(venue).setChecked(true);
                }
            }
            // Set People Tracking status
            menu.findItem(R.id.tracking).setChecked(_trackingStatus);
        } catch (Exception e) {
            Alogger.setJournal("MainActivity", "Exception in setting menu");
        }
        return true;
    }

    public void changeMap(MenuItem menuItem) {
        if (getVenue() != menuItem.getItemId()) {
            setVenue(menuItem.getItemId());
            // store venue for next start of the app
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("LBSSettings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("venue", venueToString(menuItem.getItemId()));
            editor.apply();


            fetchLocationData(menuItem.getItemId());

        }

    }
    //******************************MENU****************************************

    //*****************************VISIOGLOBE***********************************
    public void setSelectedArea(String area) {
        selectedArea = area;
    }

    public String getSelectedArea() {
        return selectedArea;
    }

    public boolean isDisplayLocationActive() {
        return true;
    }

    public void displaySearchFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_content, visioglobeSearchFragment).commit();
    }
    //*****************************VISIOGLOBE***********************************

    //******************************MAPBOX**************************************
    public void updateLocation(Location location) {
        if (location != null) {
            Log.i("MapboxLocation", "UpdateLocation: " + location.getLatitude() + " " + location.getLongitude());
            if (isDisplayLocationActive()) {
                double floor = 0;
                double altitude = location.getAltitude();
                if (altitude < 5.0) floor = 0;
                if ((altitude >= 5.0) && (altitude < 10.0)) floor = 1;
                if ((altitude >= 10.0) && (altitude < 15.0)) floor = 2;
                manualIndoorLocationProvider.setIndoorLocation(new IndoorLocation("PoleStar", location.getLatitude(), location.getLongitude(), floor, System.currentTimeMillis()));
            }
        } else {
            Log.i("Mapbox location", "location received is NULL");
        }
    }

    public void configureMapbox(final MapboxMap mapboxMap) {
        findViewById(R.id.floating_search_view).setElevation(10);
        setupFloatingSearch();
        setupResultsList();
        this.mapwizePlugin.setLocationProvider(manualIndoorLocationProvider);
        this.mapwizePlugin.setOnVenueEnterListener(new MapwizePlugin.OnVenueEnterListener() {
            @Override
            public void onVenueEnter(Venue venue) {
                venueId = venue.getId();
                Log.i("Mapbox", "onVenueEnter: Id: " + venueId);
                // RAINBOW STUFF
                // connectUserToRainbow();
            }

            @Override
            public void willEnterInVenue(Venue venue) {

            }
        });
        this.mapwizePlugin.setOnPlaceClickListener(new MapwizePlugin.OnPlaceClickListener() {
            @Override
            public boolean onPlaceClick(Place place) {
                lastClickedPlace = place;
                findViewById(R.id.button_container).setVisibility(View.VISIBLE);
                return true;
            }
        });
    }
    //******************************MAPBOX**************************************

    //*********************************LOCATION*********************************
    public boolean startServices() {
        Log.i("MainActivityStart", "startServices");
        // init service
        Alogger.setJournal("MainActivity", "startservices");
        if (locationClient != null && geofencingClient != null) {
            Log.i("MainActivityStart", "creating handles");
            locationClient.createHandle();
            geofencingClient.createHandle();


            if (locationClient.startService()) {
                Alogger.setJournal("MainActivity", "startservices OK");
                Log.i("MainActivityStart", "startService ok");
                isServiceStarted = true;
                Handler handler = new Handler();
                // START THE GEOFENCING CLIENT ONLY AFTER 10 SECONDS.
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (geofencingClient != null)
                            geofencingClient.startService();
                    }
                }, 10000);
                return true;
            } else {
                Alogger.setJournal("MainActivity", "startservices ERROR");

            }
        }
        return false;
    }

    public void stopServices() {
        Log.i("MainActivityStart", "stopServices");
        // init service
        Alogger.setJournal("MainActivity", "stopservices");
        if (locationClient != null && geofencingClient != null) {
            Log.i("MainActivityStart", "creating handles");

            locationClient.stopService();
            geofencingClient.stopService();
            isServiceStarted = false;
            locationClient = null;
            geofencingClient = null;
        }
    }
    //*********************************LOCATION*********************************

    //*******************************SEARCHVIEW*********************************
    private void setupFloatingSearch() {
        floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                if (newQuery != "" && newQuery.length() > oldQuery.length()) {
                    floatingSearchView.showProgress();

                    final Handler handler = new Handler();
                    handlerStack.push(handler);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (handlerStack.size() > 0 && handlerStack.peek() == handler) {
                                SearchParams searchParams = new SearchParams.Builder().setQuery(newQuery).setVenueId(mapwizePlugin.getVenue().getId()).build();
                                Api.search(searchParams, new ApiCallback<List<MapwizeObject>>() {
                                    @Override
                                    public void onSuccess(List<MapwizeObject> mapwizeObjects) {
                                        Log.i("SearchSuccess", "Succes " + mapwizeObjects.toString());
                                        final List<SearchSuggestion> searchSuggestions = new ArrayList<SearchSuggestion>();
                                        for (MapwizeObject o : mapwizeObjects) {
                                            searchSuggestions.add(new MapwizeSuggestionWrapper(o));
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                floatingSearchView.swapSuggestions(searchSuggestions);
                                                floatingSearchView.hideProgress();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onFailure(Throwable throwable) {
                                    }
                                });
                            }
                        }
                    }).run();
                }
            }
        });

        floatingSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {
                Log.i("Search", "Home Clicked");
                floatingSearchView.clearSearchFocus();
                floatingSearchView.clearQuery();
            }
        });

        floatingSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                Log.i("Search", searchSuggestion.getBody());
                floatingSearchView.clearQuery();
                floatingSearchView.clearSuggestions();
                Api.getPlaceWithName(searchSuggestion.getBody(), mapwizePlugin.getVenue(), new ApiCallback<Place>() {
                    @Override
                    public void onSuccess(Place place) {
                        Log.i("Search", "Place clicked: " + place.getName() + " " + place.getAlias());
                        mapwizePlugin.addMarker(place);
                        lastClickedPlace = place;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.button_container).setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.i("Search", "getPlace failed: " + throwable.getMessage());
                    }
                });
                floatingSearchView.clearQuery();
                floatingSearchView.clearSuggestions();
            }

            @Override
            public void onSearchAction(String currentQuery) {
                Log.i("Search", "Query: " + currentQuery);
            }
        });

        floatingSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                Log.i("Search", "OnFocus/clearQuery");
                floatingSearchView.clearQuery();
            }

            @Override
            public void onFocusCleared() {
                Log.i("Search", "OnFocusCleared");
            }
        });

        floatingSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon,
                                         TextView textView, SearchSuggestion item, int itemPosition) {
                MapwizeSuggestionWrapper searchObjectSuggestion = (MapwizeSuggestionWrapper) item;

                leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_history_black_24dp, null));
                Util.setIconColor(leftIcon, Color.BLUE);
                leftIcon.setAlpha(.36f);

                int index = searchObjectSuggestion.getBody().toUpperCase().indexOf(floatingSearchView.getQuery().toUpperCase());

                if (index != -1) {
                    String oldSequence = searchObjectSuggestion.getBody().substring(index, index + floatingSearchView.getQuery().length());
                    String text = searchObjectSuggestion.getBody();
                    text = text.replaceFirst(oldSequence, "<font color=\"" + "#C51586" + "\">" + oldSequence + "</font>");

                    textView.setText(Html.fromHtml(text));
                }

            }

        });
    }

    private void setupResultsList() {
        MapwizeSearchResultsListAdapter mapwizeSearchResultsListAdapter = new MapwizeSearchResultsListAdapter();
    }

    private void getDirection(Place place) {
        LatLngFloor from;
        try {
            from = new LatLngFloor(mapwizePlugin.getUserPosition().getLatitude(), mapwizePlugin.getUserPosition().getLongitude(), mapwizePlugin.getUserPosition().getFloor());
        } catch (Exception e) {
            from = new LatLngFloor(0, 0, 0.0);
        }
        Api.getDirection(from, place, true, new ApiCallback<Direction>() {
            @Override
            public void onSuccess(Direction direction) {
                //mapwizePlugin.setDirection(direction);
                letsgo(direction);
                saveDirection = direction;
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.i("Direction", "failed: " + throwable.getMessage());
            }
        });

    }
    //*******************************SEARCHVIEW*********************************

    //*********************************BUTTONS**********************************
    public void startDirection(View v) {
        getDirection(lastClickedPlace);
        v.setVisibility(GONE);
        findViewById(R.id.buttonStop).setVisibility(View.VISIBLE);
        mapwizePlugin.removeMarkers();
    }

    public void stopDirection(View v) {
        mapwizePlugin.setDirection(null);
        saveDirection = null;
        v.setVisibility(GONE);
        findViewById(R.id.buttonStart).setVisibility(View.VISIBLE);
        findViewById(R.id.button_container).setVisibility(GONE);
    }

    public void showInfos(View v) {
        findViewById(R.id.button_container).setVisibility(GONE);
        findViewById(R.id.floating_search_view).setVisibility(GONE);
        webFragment = new WebFragment();
        try {
            Log.i("showInfos", lastClickedPlace.getData().getString("url"));
            webFragment.setUrl(lastClickedPlace.getData().getString("url"));
        } catch (Exception e) {
            Log.i("showInfos", "error: " + e.getMessage());
        }
        getFragmentManager().beginTransaction().add(R.id.main_content, webFragment, "webFragment").commit();
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        findViewById(R.id.floating_search_view).setVisibility(View.VISIBLE);
        getFragmentManager().beginTransaction().remove(webFragment).commit();
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        return true;
    }

    //*********************************BUTTONS***********************************
    public void toExit(MenuItem item) {
        Alogger.setJournal("MainActivity", "exit the application");
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void toPeopleTracking(MenuItem item) {
        Alogger.setJournal("MainActivity", "people tracking menu");
        item.setChecked(!item.isChecked()); // ??????
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("LBSSettings", Context.MODE_PRIVATE);
        _trackingStatus = item.isChecked();
        sharedPref.edit().putBoolean("tracking", _trackingStatus).apply();

    }

    //*********************************DEBUG*************************************
    public void toDebugActivity(MenuItem menuItem) {
        Intent intent = new Intent(MainActivity.this, DebugActivity.class);
        if (saveDirection != null) {
            intent.putExtra("Direction", (Parcelable) saveDirection);
        }
        // put connection status to Debug Activity
        boolean rainbowstatus = RainbowSdk.instance().connection().isConnected();

        if (rainbowstatus) {
            String presence;
            RainbowPresence rainbowpresence = RainbowSdk.instance().myProfile().getConnectedUser().getPresence();

            if (rainbowpresence != null) {
                intent.putExtra("RainbowStatus", true);
                presence = rainbowpresence.getPresence();
                intent.putExtra("Presence", presence);
                String username = RainbowSdk.instance().myProfile().getConnectedUser().getFirstName() + " " + RainbowSdk.instance().myProfile().getConnectedUser().getLastName();
                Bitmap bmp = RainbowSdk.instance().myProfile().getConnectedUser().getPhoto();
                intent.putExtra("UserName", username);
                intent.putExtra("Photo", bmp);
            } else {
                intent.putExtra("RainbowStatus", false);
            }
        }

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("LBSSettings", Context.MODE_PRIVATE);
        sharedPref.edit().putString("venue", venueToString(getVenue())).apply();

        startActivityForResult(intent, 1);
    }

    // Wayfinding code +
    private void letsgo(final Direction dir) {   // need to run in UI thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapwizePlugin.setDirection(dir);    // draw the wayfinding
            }
        });
    }

    public void wayfinding(View v, final String place) {
        String lvenueName = mapwizePlugin.getVenue().getName();
        Api.getVenueWithName(lvenueName, new ApiCallback<Venue>() { // harcdcoded to run on brest map
            @Override
            public void onSuccess(Venue venue) {
                Api.getPlaceWithAlias(place, venue, new ApiCallback<Place>() { // harcoded final place = pause
                    @Override
                    public void onSuccess(Place place) {
                        Api.getDirection(new LatLngFloor(mapwizePlugin.getUserPosition().getLatitude(), mapwizePlugin.getUserPosition().getLongitude(), mapwizePlugin.getUserPosition().getFloor()), place, true, new ApiCallback<Direction>() {
                            @Override
                            public void onSuccess(Direction direction) {
                                letsgo(direction);
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                Log.i("MainActivity", "error getDirection");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.i("MainActivity", "error getPlaceWithAlias");
                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.i("MainActivity", "error GetVenueWithName");
            }
        });
    }

    // Wayfinding code -

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if (data != null) {
            String method = data.getStringExtra("action");
            if (method.equalsIgnoreCase("SetRainbowCredentials")) {
                String login = data.getStringExtra("rainbowlogin");
                String pwd = data.getStringExtra("rainbowpassword");
                _Rainbow.connectUserToRainbow(login, pwd);
            }
        }
    }

    public void positioningSimulation(MenuItem menuItem) {
        // setVenue(R.id.positionSimul);
        // fetchLocationData(getVenue());
        // upload emulation file to local device.
    }
    //*********************************DEBUG*************************************

    //*********************************INTENT************************************
    @Override
    protected void onNewIntent(Intent intent) {
        Log.i("onNewIntent", "onNewIntent");
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        String method;
        String whereToGo;
        if (uri != null) {
            Log.i("onNewIntent", "uri: " + uri.toString());
            method = uri.getQueryParameter("action");
            if (method != null) {
                if (method.equalsIgnoreCase("wayfinding")) {
                    whereToGo = uri.getQueryParameter("poi");
                    if (whereToGo != null) {
                        if (visioglobeLocationFragment != null) {
                            visioglobeLocationFragment.gotoPlaceId(whereToGo);
                            Log.i("onNewIntent", "method: " + method);
                            Log.i("onNewIntent", "whereToGo: " + whereToGo);
                        }
                        // Jerome Elleouet +
                        else {
                            wayfinding(mapView, whereToGo);
                        }
                        // Jerome Elleouet -
                    }
                } else if (method.equalsIgnoreCase("look4people")) {
                    final String who = uri.getQueryParameter("poi").replace("_", "%20");
                    String ROOT_URL = "https://www.omniaccess-stellar-lbs.com";
                    String AUTH_TOKEN = _keys.getPeopleTrackingAuthtoken(); // Authentication API of Jerome Elleouet

                    String PEOPLE_URL = ROOT_URL + "/nao_trackables.json?tracking_ids=" + who + "&site_id=" + getSiteIDFromVenue() + " &auth_token=" + AUTH_TOKEN;

                    new Thread(new Runnable() {
                        @Override
                        public void run()
                        {
                            HttpHelper lHelper = new HttpHelper(getApplicationContext(), true);
                            String resp = lHelper.getText("https://www.omniaccess-stellar-lbs.com/nao_trackables.json?site_id=151&tracking_ids=JEROME%20ELLEOUET&auth_token=DYfxVdgc3nZ7avJmC-RyBHNohXrbrQ");
                            try {
                                JSONArray arr = new JSONArray(resp);
                                JSONObject obj = arr.getJSONObject(0);
                                String lat = obj.getString("lat");
                                String lng = obj.getString(("lon"));
                                String alt = obj.getString("alt");
                                String date = obj.getString("loc_updated_at");
                                String cat = obj.getString("category");
                                String name = obj.getString("name");
                                Boolean isonsite = obj.getBoolean("is_present");
                                if (visioglobeLocationFragment != null) {
                                    visioglobeLocationFragment.setonMap(name, lat, lng, alt, date, cat, isonsite );
                                }
                            }
                            catch (Exception e){
                                e.getMessage();
                            }
                        }
                    }).start();
                   // HttpHelper.getText("https://www.omniaccess-stellar-lbs.com/nao_trackables.json?site_id=151&tracking_ids=JEROME%20ELLEOUET&auth_token=DYfxVdgc3nZ7avJmC-RyBHNohXrbrQ");

                }
            }
        } else {
            method = intent.getStringExtra("action");
            Log.i("onNewIntent", "method: " + intent.getStringExtra("action"));
            if (method != null) {
                if (method.equalsIgnoreCase("wayfinding")) {
                    whereToGo = getIntent().getStringExtra("poi");
                    if (whereToGo != null) {
                        if (visioglobeLocationFragment != null) {
                            visioglobeLocationFragment.setDestination(whereToGo);
                            Log.i("onNewIntent", "method: " + method);
                            Log.i("onNewIntent", "whereToGo: " + whereToGo);
                        }
                        // Jerome Elleouet +
                        else {
                            wayfinding(mapView, whereToGo);
                        }
                        // Jerome Elleouet -
                    }
                }
            }
        }

    }


    //*********************************INTENT************************************


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isWebFragmentVisible) {
            try {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            isWebFragmentVisible = false;
        }
    }

public void JsonGet(String url)
{


    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Alogger.setJournal("MainActivity", response.toString());
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error

                }
            });
}

    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */);
        urlConnection.setConnectTimeout(15000 /* milliseconds */);
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        String jsonString = sb.toString();
        System.out.println("JSON: " + jsonString);

        return new JSONObject(jsonString);
    }

    int getSiteIDFromVenue()
    {
        int siteId = 0;
        if (getVenue() == R.id.buenosairesmap)
        {
            siteId = _keys.getArgsiteId();
        }
        if (getVenue() == R.id.colombesmap)
        {
            siteId = _keys.getColsiteId();
        }
        if (getVenue() == R.id.edemomap)
        {
            siteId = _keys.getColeDemositeId();
        }
        if (getVenue() == R.id.brestmap)
        {
            siteId = _keys.getBrestSiteId();
        }
        if (getVenue() == R.id.brestmapbox)
        {
            siteId = _keys.getBrestSiteId();
        }
        if (getVenue() == R.id.ibmmap)
        {
            siteId = _keys.getIBMsiteId();
        }
        if (getVenue() == R.id.HHCSXBmap)
        {
            siteId = _keys.getHHCSXBsiteId();
        }
        return siteId;
    }


}

