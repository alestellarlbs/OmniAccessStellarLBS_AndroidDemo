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
package com.ale.omniaccess.stellarlbs.demoapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class DebugActivity extends AppCompatActivity {

    private EditText editloginrainbow;
    private EditText editpasswordrainbow;
    private TextView connectionstatus;
    private ImageView photo;
    private String login;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        photo = (ImageView)findViewById(R.id.userPhoto);

        boolean rainbowstatus = getIntent().getBooleanExtra("RainbowStatus", false);
        String username = getIntent().getStringExtra("UserName");
        String presence = getIntent().getStringExtra("Presence");
        Bitmap bmp = getIntent().getParcelableExtra("Photo");
        connectionstatus = (TextView)findViewById(R.id.isConnected);
        if (rainbowstatus) {
            connectionstatus.setText(username + " is connected to Rainbow - status : " + presence);
            photo.setImageBitmap(bmp);
        }
        else
            connectionstatus.setText("No user connected to Rainbow");

        editloginrainbow = (EditText)findViewById(R.id.rainbowlogin);
        editpasswordrainbow = (EditText)findViewById(R.id.rainbowpassword);
        getPreferences();
    }

    public void getPreferences(){
        //SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("LBSSettings", Context.MODE_PRIVATE);
        login = sharedPreferences.getString("rainbowlogin", "");
        password = sharedPreferences.getString("rainbowpassword", "");
        editloginrainbow.setText(login);
        editpasswordrainbow.setText(password);
    }

    public void savePreferences(View v){
        //SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("LBSSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String newlogin = editloginrainbow.getText().toString();
        String newpassword = editpasswordrainbow.getText().toString();


        if (!(newlogin.equals(login)) || !(newpassword.equals(password)))
        {
            login = newlogin;
            password = newpassword;
            editor.putString("rainbowlogin", login);
            editor.putString("rainbowpassword", password);
            editor.apply();
            sendData();
        }


    }

    public void sendData(){
        Log.i("DebugPlace","send data");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("action","SetRainbowCredentials".toString());
        returnIntent.putExtra("rainbowlogin",editloginrainbow.getText().toString());
        returnIntent.putExtra("rainbowpassword",editpasswordrainbow.getText().toString());
        setResult(RESULT_OK,returnIntent);
        finish();

    }
}
