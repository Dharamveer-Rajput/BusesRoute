package com.busesroute;

import android.support.annotation.ColorInt;

import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

/**
 * Created by dharamveer on 9/3/18.
 */

public class ColorPickerDialogListenerExtened implements ColorPickerDialogListener {

    public ColorPickerDialogListenerExtened(int position) {
        this.position = position;
    }

    int position = 0;


    @Override
    public void onColorSelected(int dialogId, int color) {

    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}
