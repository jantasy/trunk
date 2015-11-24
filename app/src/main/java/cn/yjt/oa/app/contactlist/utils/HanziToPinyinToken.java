package cn.yjt.oa.app.contactlist.utils;

public class HanziToPinyinToken {
    /**
     * Separator between target string for each source char
     */
    public static final String SEPARATOR = " ";

    public static final int LATIN = 1;
    public static final int PINYIN = 2;
    public static final int UNKNOWN = 3;

    public HanziToPinyinToken() {
    }

    public HanziToPinyinToken(int type, String source, String target) {
        this.type = type;
        this.source = source;
        this.target = target;
    }

    /**
     * Type of this token, ASCII, PINYIN or UNKNOWN.
     */
    public int type;
    /**
     * Original string before translation.
     */
    public String source;
    /**
     * Translated string of source. For Han, target is corresponding Pinyin. Otherwise target is
     * original string in source.
     */
    public String target;
}