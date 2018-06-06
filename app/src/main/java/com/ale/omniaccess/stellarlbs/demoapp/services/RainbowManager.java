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

import com.ale.infra.contact.RainbowPresence;
import com.ale.infra.proxy.conversation.IRainbowConversation;
import com.ale.listener.IRainbowGetConversationListener;
import com.ale.listener.SigninResponseListener;
import com.ale.listener.SignoutResponseListener;
import com.ale.listener.StartResponseListener;
import com.ale.omniaccess.stellarlbs.demoapp.util.Alogger;
import com.ale.rainbowsdk.RainbowSdk;

/**
 * Created by jelleoue on 06/04/2018.
 */

public class RainbowManager {

    private RainbowPresence _previousPresence;

    public void RainbowManager()
    {

    }

    public void setPresence(RainbowPresence pPresence)
    {
        if (RainbowSdk.instance().connection().isConnected()) {
            Alogger.setJournal("MainActivity", "Push specific PResence into Rainbow");
            _previousPresence = RainbowSdk.instance().myProfile().getConnectedUser().getPresence();
            RainbowSdk.instance().myProfile().setPresenceTo(pPresence);
        }

    }

    public void setPreviousPresence()
    {
        if (RainbowSdk.instance().connection().isConnected()) {
            Alogger.setJournal("MainActivity", "Push specific PResence into Rainbow");
            RainbowSdk.instance().myProfile().setPresenceTo(_previousPresence);
        }

    }

    public void sendChatMessagetoBot(final String msg)
    {
        if (RainbowSdk.instance().connection().isConnected()) {
            Alogger.setJournal("MainActivity", "Send msg to Rainbow: " + msg + " to GEOLOC chatbot");
            RainbowSdk.instance().conversations().getConversationFromContact("6fac1f46fe5b431688d7837467de5963@openrainbow.com", new IRainbowGetConversationListener() {
                @Override
                public void onGetConversationSuccess(IRainbowConversation iRainbowConversation) {
                    Alogger.setJournal("MainActivity", "Get Conversation with super chatbot OK now... send message");
                    RainbowSdk.instance().im().sendMessageToConversation(iRainbowConversation, msg);

                }

                @Override
                public void onGetConversationError() {
                    Alogger.setJournal("MainActivity", "Get Conversation with super chatbot ERROR");
                }
            });
        }
        else
        {
            Alogger.setJournal("MainActivity", "sendChatMessageto Rainbow failed - not connected to Rainbow");
        }
    }

    public void disconnectUserFromRainbow()
    {
        if (RainbowSdk.instance().connection().isConnected())
        {
            RainbowSdk.instance().connection().signout(new SignoutResponseListener() {
                @Override
                public void onSignoutSucceeded() {
                    Alogger.setJournal("MainActivity", "Rainbow signout of user succeeded");
                    //RainbowSdk.instance().connection().uninitialize();
                    //RainbowContext.getInfrastructure().un();
                }

                @Override
                public void onRequestFailed(RainbowSdk.ErrorCode errorCode, String s) {
                    Alogger.setJournal("MainActivity", "Rainbow signout failed");
                }
            });
        }
    }



    public void connectUserToRainbow(final String pLogin, final String pPwd) {


        RainbowSdk.instance().connection().start(new StartResponseListener() {
            @Override
            public void onStartSucceeded() {
                Alogger.setJournal("MainActivity", "onRAINBOWStart OK - start event LISTENER");
                RainbowIMNotificationMgr lHandler = new RainbowIMNotificationMgr();
                RainbowSdk.instance().connection().signin(pLogin, pPwd, "openrainbow.com", new SigninResponseListener() {
                    //RainbowSdk.instance().connection().signin("jerome.elleouet@al-enterprise.com", "@Lcatel29", "sandbox.openrainbow.com", new SigninResponseListener() {

                    @Override
                    public void onSigninSucceeded()
                    {
                        Alogger.setJournal("MainActivity", "user is connected on Rainbow");
                        Alogger.setJournal("MainActivity", "get chatbot from userID - jabber ");

                    }

                    @Override
                    public void onRequestFailed(RainbowSdk.ErrorCode errorCode, String err) {
                        Alogger.setJournal("MainActivity", "user is NOT connected on Rainbow / signin failed: " + err);
                    }
                });
            }

            @Override
            public void onRequestFailed(RainbowSdk.ErrorCode errorCode, String err) {
                // Something was wrong
                Alogger.setJournal("MainActivity", "error initailizing Rainbow");
            }
        });
    }


}
