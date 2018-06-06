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
		
 package com.ale.omniaccess.stellarlbs.demoapp.util;

import android.annotation.SuppressLint;
import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.util.List;

import io.mapwize.mapwizeformapbox.model.MapwizeObject;
import io.mapwize.mapwizeformapbox.model.Place;
import io.mapwize.mapwizeformapbox.model.PlaceList;
import io.mapwize.mapwizeformapbox.model.Translation;
import io.mapwize.mapwizeformapbox.model.Venue;

/**
 * Created by vaymonin on 06/02/2018.
 */

@SuppressLint("ParcelCreator")
public class MapwizeSuggestionWrapper implements SearchSuggestion {

    String name;
    String alias;
    String objectId;
    String objectClass;
    List<Translation> translations;
    MapwizeObject searchable;

    public MapwizeSuggestionWrapper(MapwizeObject object){
        name = object.getAlias();
        alias = object.getAlias();
        objectId = object.getId();
        if(object.getClass() == Place.class){
            objectClass = "Place";
        }
        if(object.getClass() == Venue.class){
            objectClass = "Venue";
        }
        if(object.getClass() == PlaceList.class){
            objectClass = "PlaceLits";
        }
        translations = object.getTranslations();
        this.searchable = object;
    }

    @Override
    public String getBody() {
        return translations.get(0).getTitle();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
