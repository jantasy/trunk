/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.yjt.oa.app.contactlist.utils;

import java.util.List;

import android.os.Build;


/**
 * An object to convert Chinese character to its corresponding pinyin string. For characters with
 * multiple possible pinyin string, only one is selected according to collator. Polyphone is not
 * supported in this implementation. This class is implemented to achieve the best runtime
 * performance and minimum runtime resources with tolerable sacrifice of accuracy. This
 * implementation highly depends on zh_CN ICU collation data and must be always synchronized with
 * ICU.
 *
 * Currently this file is aligned to zh.txt in ICU 4.6
 */
public class HanziToPinyinUtil {
    
    public static List<HanziToPinyinToken> getToken(String source) {
    	if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
    		return HanziToPinyinIcu.getInstance().get(source);
    	} else {
    		return HanziToPinyin.getInstance().get(source);
    	}
    }
}
