package com.example.dev.voice_height;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Kim juyoung on 2016-11-18.
 */

public abstract class SharedPreference {

    public enum Preference{
        User("Preference.Userdata"),
        Version("Preference.Version"),
        Map("Preference.Mapdata");
        private final String value;

        private Preference(String value) {
            this.value = value;
        }
        public String getValue(){
            return value;
        }
    }
    protected Context mContext;
    protected SharedPreferences.Editor mEdition;
    protected SharedPreferences mPrefOption;
    public SharedPreference(Context context){
        mContext = context;
        init();
    }
    public abstract void onReadPreference();
    public abstract void onWritePreference();
    protected abstract void init();
    protected void setEdition(String aTag, String aValue){
        mEdition.putString(aTag, aValue);
        mEdition.commit();
    }
    protected void setEdition(String aTag, boolean aValue){
        mEdition.putBoolean(aTag, aValue);
        mEdition.commit();
    }
    protected void setEdition(String aTag, int aValue){
        mEdition.putInt(aTag, aValue);
        mEdition.commit();
    }
    protected void setEdition(String aTag, Long aValue){
        mEdition.putLong(aTag, aValue);
        mEdition.commit();
    }
    protected void setEdition(String aTag, Float aValue){
        mEdition.putFloat(aTag, aValue);
        mEdition.commit();
    }

    protected int getPrefInt(String aTag){
        return mPrefOption.getInt(aTag, -1);
    }

    protected String getPrefString(String aTag){
        return mPrefOption.getString(aTag, null);
    }
    protected boolean getPrefBool(String aTag){
        return mPrefOption.getBoolean(aTag, false);
    }
    protected float getPrefFloat(String aTag){
        return mPrefOption.getFloat(aTag, 0);
    }

}
