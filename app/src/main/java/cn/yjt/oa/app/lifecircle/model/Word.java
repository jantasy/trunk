package cn.yjt.oa.app.lifecircle.model;

import java.util.ArrayList;

public class Word {
    private int count;
    private String name;
    private String reason;
    private int result;
    private ArrayList<Word> words;

    public int getCount() {
        return this.count;
    }

    public String getName() {
        return this.name;
    }

    public String getReason() {
        return this.reason;
    }

    public int getResult() {
        return this.result;
    }

    public ArrayList<Word> getWords() {
        return this.words;
    }

    public void setCount(int i) {
        this.count = i;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setReason(String str) {
        this.reason = str;
    }

    public void setResult(int i) {
        this.result = i;
    }

    public void setWords(ArrayList<Word> arrayList) {
        this.words = arrayList;
    }
}
