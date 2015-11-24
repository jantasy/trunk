/*
 * Copyright (C) 2013 The Android Open Source Project
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import android.util.Log;

/**
 * Exposes icu4c's Transliterator.
 */
public final class Transliterator {

	private static final String CLASS_NAME = "libcore.icu.Transliterator";

	Object transliteratorInstance;
	Method transliterate;
	/**
	 * Creates a new Transliterator for the given id.
	 */
	public Transliterator(String id) {
		Class<?> clazz;
		try {
			clazz = Class.forName(CLASS_NAME);
			Constructor<?> constructor = clazz.getConstructor(new Class[] {String.class});
			transliteratorInstance = constructor.newInstance(id);
			
			transliterate = clazz.getDeclaredMethod("transliterate", String.class);
		} catch (Throwable e) {
			Log.d("Transliterator", "not support");
		}
	}

	/**
	 * Transliterates the specified string.
	 */
	public String transliterate(String s) {
		try {
			if (transliteratorInstance != null && transliterate != null)
				return (String) transliterate.invoke(transliteratorInstance, s);
		} catch (Throwable e) {
			return null;
		}
		return null;
	}
}
