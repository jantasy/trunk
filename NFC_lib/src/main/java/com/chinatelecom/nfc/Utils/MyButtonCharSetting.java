/*
 * $Id$
 * Copyright (C) 2012 Sharp Corp. Communication Systems Group.
 */
package com.chinatelecom.nfc.Utils;
/**
 * @author iss_lyc
 *
 */
public class MyButtonCharSetting {
    public final String charcter;
    public final float angle;
    public final float x;
    public final float y;

    public MyButtonCharSetting(String charcter, float angle, float x, float y) {
        super();
        this.charcter = charcter;
        this.angle = angle;
        this.x = x;
        this.y = y;
    }

    public static final MyButtonCharSetting[] settings = {
            new MyButtonCharSetting("、", 0.0f, 0.7f, -0.6f),
            new MyButtonCharSetting("。", 0.0f, 0.7f, -0.6f),
            new MyButtonCharSetting("【", 90.0f, -1.0f, -0.3f),
            new MyButtonCharSetting("】", 90.0f, -1.0f, 0.0f),
            new MyButtonCharSetting("a", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("b", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("c", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("d", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("e", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("f", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("g", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("h", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("i", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("j", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("k", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("l", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("m", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("n", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("o", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("p", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("q", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("r", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("s", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("t", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("u", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("v", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("w", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("x", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("y", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("z", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("A", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("B", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("C", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("D", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("E", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("F", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("G", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("H", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("I", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("J", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("K", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("L", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("M", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("N", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("O", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("P", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("Q", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("R", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("S", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("T", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("U", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("V", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("W", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("X", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("Y", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("Z", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting(":", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting(";", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("/", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting(" ", 0.0f, 0.0f, -0.1f),
            new MyButtonCharSetting(".", 90.0f, 0.0f, -0.1f), 
            new MyButtonCharSetting("*", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("0", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("1", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("2", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("3", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("4", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("5", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("6", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("7", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("8", 90.0f, 0.0f, -0.1f),
            new MyButtonCharSetting("9", 90.0f, 0.0f, -0.1f),
            };

    public static MyButtonCharSetting getSetting(String character) {
        for (int i = 0; i < settings.length; i++) {
            if (settings[i].charcter.equals(character)) {
                return settings[i];
            }
        }
        return null;
    }

    private static final String[] PUNCTUATION_MARK = { "、", "。", "【", "】"};

    public static boolean isPunctuationMark(String s) {
        for (String functuantionMark : PUNCTUATION_MARK) {
            if (functuantionMark.equals(s)) {
                return true;
            }
        }
        return false;
    }


    public String getCharcter() {
        return charcter;
    }

}