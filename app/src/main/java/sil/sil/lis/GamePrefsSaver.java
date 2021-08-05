package sil.sil.lis;

import android.content.Context;
import android.content.SharedPreferences;

public class GamePrefsSaver {

    private SharedPreferences sharedPreferences;

    public GamePrefsSaver(Context context){
        sharedPreferences = context.getSharedPreferences("PREFERS", Context.MODE_PRIVATE);
    }

    public void setGamePoint(String gamePoint) {
        sharedPreferences.edit().putString("pont", gamePoint).apply();
    }

    public String getGamePoint() {
        return sharedPreferences.getString("pont", "");
    }

    public void setFirstref(boolean first) {
        sharedPreferences.edit().putBoolean("first", first).apply();
    }

    public boolean getFirstref() {
        return sharedPreferences.getBoolean("first", true);
    }

    public void setFirstSt(boolean firstGameStart) {
        sharedPreferences.edit().putBoolean("star", firstGameStart).apply();
    }

    public boolean getFirstSt() {
        return sharedPreferences.getBoolean("star", true);
    }

    public void setfFlyer(boolean appsFlyer) {
        sharedPreferences.edit().putBoolean("fflyer", appsFlyer).apply();
    }

    public boolean getfFlyer() {
        return sharedPreferences.getBoolean("fflyer", true);
    }

    public void setScreenX(int screenX) {
        sharedPreferences.edit().putInt("screenX", screenX).apply();
    }

    public int getScreenX() {
        return sharedPreferences.getInt("screenX", 1080);
    }

    public void setScreenY(int screenX) {
        sharedPreferences.edit().putInt("screenY", screenX).apply();
    }

    public int getScreenY() {
        return sharedPreferences.getInt("screenY", 2080);
    }

    public void setUserPhoto(String userPhoto) {
        sharedPreferences.edit().putString("photo", userPhoto).apply();
    }

    public String getUserPhoto() {
        return sharedPreferences.getString("photo", "DEFAULT_USER_PHOTO");
    }
    public void setAdId(String ad) {
        sharedPreferences.edit().putString("ad", ad).apply();
    }

    public String getAdId() {
        return sharedPreferences.getString("ad", "");
    }
}
