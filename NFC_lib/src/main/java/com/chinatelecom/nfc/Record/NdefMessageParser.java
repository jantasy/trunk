/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.chinatelecom.nfc.Record;

import java.util.ArrayList;
import java.util.List;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

/**
 * Utility class for creating {@link ParsedNdefMessage}s.
 */
public class NdefMessageParser {

    // Utility class
    private NdefMessageParser() {

    }

    /** Parse an NdefMessage */
    public static List<ParsedNdefRecord> parse(NdefMessage message) {
        return getRecords(message.getRecords());
    }

    public static List<ParsedNdefRecord> getRecords(NdefRecord[] records) {
        List<ParsedNdefRecord> elements = new ArrayList<ParsedNdefRecord>();
        for (NdefRecord record : records) {
            if (UriRecord.isUri(record)) {
                elements.add(UriRecord.parse(record));
            } else if (TextRecord.isText(record)) {
                elements.add(TextRecord.parse(record));
            } else if (SmartPoster.isPoster(record)) {
                elements.add(SmartPoster.parse(record));
            }
        }
        return elements;
    }
    
    /** Parse an NdefMessage */
    public static String parseToString(NdefMessage message) {
        return getRecordsToString(message.getRecords());
    }
    
    public static String getRecordsToString(NdefRecord[] records) {
    	StringBuffer sb = new StringBuffer();
    	for (NdefRecord record : records) {
    		if (UriRecord.isUri(record)) {
    			sb.append(UriRecord.parse(record));
    		} else if (TextRecord.isText(record)) {
    			sb.append(TextRecord.parse(record));
    		} else if (SmartPoster.isPoster(record)) {
    			sb.append(SmartPoster.parse(record));
    		}
    	}
    	return sb.toString();
    }
}
