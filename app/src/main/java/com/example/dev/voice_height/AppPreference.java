package com.example.dev.voice_height;

import android.content.Context;

/**
 * Created by Kim juyoung on 2016-11-18.
 */

public class AppPreference extends SharedPreference{

    private static final String HZ = "Preference.HZ";

    public AppPreference(Context context) {
        super(context);
    }
    @Override
    public void onReadPreference() {
        mPrefOption = mContext.getSharedPreferences(Preference.User.getValue(), Context.MODE_PRIVATE);
    }
    @Override
    public void onWritePreference() {
        mEdition = mContext.getSharedPreferences(Preference.User.getValue()
                , Context.MODE_PRIVATE).edit();
    }
    @Override
    protected void init() {
        onReadPreference();
        onWritePreference();
    }

    public int getHZ(){
        return getPrefInt(HZ);
    }
    public void addHZ(int hz){
        setEdition(HZ, hz);
    }
}
