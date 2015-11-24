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

import java.util.ArrayList;

import android.text.TextUtils;
import android.util.Log;

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
class HanziToPinyinIcu {
	private static final String TAG = "HanziToPinyinIcu";

    private static HanziToPinyinIcu sInstance;
    private Transliterator mPinyinTransliterator;
    private Transliterator mAsciiTransliterator;

    private HanziToPinyinIcu() {
        try {
            mPinyinTransliterator = new Transliterator("Han-Latin/Names; Latin-Ascii; Any-Upper");
            mAsciiTransliterator = new Transliterator("Latin-Ascii");
        } catch (RuntimeException e) {
            Log.w(TAG, "Han-Latin/Names transliterator data is missing,"
                  + " HanziToPinyin is disabled");
        }
    }

    public boolean hasChineseTransliterator() {
        return mPinyinTransliterator != null;
    }

    public static HanziToPinyinIcu getInstance() {
        synchronized (HanziToPinyin.class) {
            if (sInstance == null) {
                sInstance = new HanziToPinyinIcu();
            }
            return sInstance;
        }
    }

    private void tokenize(char character, HanziToPinyinToken token) {
        token.source = Character.toString(character);

        // ASCII
        if (character < 128) {
            token.type = HanziToPinyinToken.LATIN;
            token.target = token.source;
            return;
        }

        // Extended Latin. Transcode these to ASCII equivalents
        if (character < 0x250 || (0x1e00 <= character && character < 0x1eff)) {
            token.type = HanziToPinyinToken.LATIN;
            token.target = mAsciiTransliterator == null ? token.source :
                mAsciiTransliterator.transliterate(token.source);
            return;
        }

        token.type = HanziToPinyinToken.PINYIN;
        token.target = mPinyinTransliterator.transliterate(token.source);
        if (TextUtils.isEmpty(token.target) ||
            TextUtils.equals(token.source, token.target)) {
            token.type = HanziToPinyinToken.UNKNOWN;
            token.target = token.source;
        }
    }

    /**
     * Convert the input to a array of tokens. The sequence of ASCII or Unknown characters without
     * space will be put into a HanziToPinyinToken, One Hanzi character which has pinyin will be treated as a
     * HanziToPinyinToken. If there is no Chinese transliterator, the empty token array is returned.
     */
    public ArrayList<HanziToPinyinToken> get(final String input) {
        ArrayList<HanziToPinyinToken> tokens = new ArrayList<HanziToPinyinToken>();
        if (!hasChineseTransliterator() || TextUtils.isEmpty(input)) {
            // return empty tokens.
            return tokens;
        }

        final int inputLength = input.length();
        final StringBuilder sb = new StringBuilder();
        int tokenType = HanziToPinyinToken.LATIN;
        HanziToPinyinToken token = new HanziToPinyinToken();

        // Go through the input, create a new token when
        // a. HanziToPinyinToken type changed
        // b. Get the Pinyin of current charater.
        // c. current character is space.
        for (int i = 0; i < inputLength; i++) {
            final char character = input.charAt(i);
            if (Character.isSpaceChar(character)) {
                if (sb.length() > 0) {
                    addToken(sb, tokens, tokenType);
                }
            } else {
                tokenize(character, token);
                if (token.type == HanziToPinyinToken.PINYIN) {
                    if (sb.length() > 0) {
                        addToken(sb, tokens, tokenType);
                    }
                    tokens.add(token);
                    token = new HanziToPinyinToken();
                } else {
                    if (tokenType != token.type && sb.length() > 0) {
                        addToken(sb, tokens, tokenType);
                    }
                    sb.append(token.target);
                }
                tokenType = token.type;
            }
        }
        if (sb.length() > 0) {
            addToken(sb, tokens, tokenType);
        }
        return tokens;
    }

    private void addToken(
            final StringBuilder sb, final ArrayList<HanziToPinyinToken> tokens, final int tokenType) {
        String str = sb.toString();
        tokens.add(new HanziToPinyinToken(tokenType, str, str));
        sb.setLength(0);
    }
}
