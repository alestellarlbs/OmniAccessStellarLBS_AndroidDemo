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

import com.ale.infra.contact.IRainbowContact;
import com.ale.infra.manager.IMMessage;
import com.ale.listener.IRainbowImListener;
import com.ale.omniaccess.stellarlbs.demoapp.util.Alogger;
import com.ale.rainbowsdk.RainbowSdk;

import java.util.List;

/**
 * Created by jelleoue on 03/04/2018.
 */

public class RainbowIMNotificationMgr implements IRainbowImListener {

    public RainbowIMNotificationMgr() {
        RainbowSdk.instance().im().registerListener(this);
        Alogger.setJournal("IM Listener", "Listener registered on Rainbow");
    }

    @Override
    public void onImReceived(String conversationId, IMMessage message) {
        Alogger.setJournal("IM Listener", "on IM received");
//        IRainbowConversation conversation = RainbowSdk.instance().conversations().getConversationFromId(conversationId);
//        if (conversation != null) {
//            displayNotificationForContact(conversation.getContact(), message);
//        }
    }
    @Override
    public void onImSent(String conversationId, IMMessage message) {
        Alogger.setJournal("IM Listener", "message sent : conversationID = " + conversationId + "/msg :" + message);

    }
    @Override
    public void isTypingState(IRainbowContact other, boolean isTyping, String roomId) {

    }

    @Override
    public void onMessagesListUpdated(int i, String s, List<IMMessage> list) {

    }

    @Override
    public void onMoreMessagesListUpdated(int i, String s, List<IMMessage> list) {

    }


    private void displayNotificationForContact(IRainbowContact contact, IMMessage msg) {
        // Create and display the notification for this contact
    }

}
