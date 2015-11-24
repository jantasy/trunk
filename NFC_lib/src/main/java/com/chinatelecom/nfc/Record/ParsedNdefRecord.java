package com.chinatelecom.nfc.Record;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface ParsedNdefRecord {

    /**
     * Returns a view to display this record.
     */
    public View getView(Activity activity, LayoutInflater inflater, ViewGroup parent,
                        int offset);
    /**
     * Returns a view to display this record.
     */
    public String getContent();

}
