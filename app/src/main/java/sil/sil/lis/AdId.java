package sil.sil.lis;

import android.content.Context;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;

public class AdId extends Thread{
    Context context;
    private String adv_id = "";
    GamePrefsSaver gprefS;

    public AdId(Context context){
        this.context = context;
        gprefS = new GamePrefsSaver(context);
    }

    @Override
    public void run() {
        try {
            AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context.getApplicationContext());
            adv_id = adInfo != null ? adInfo.getId() : null;
            gprefS.setAdId(adv_id);
        } catch (IOException | GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException exception) {
        }
    }
}
