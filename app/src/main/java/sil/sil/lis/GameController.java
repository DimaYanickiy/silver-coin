package sil.sil.lis;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class GameController extends WebViewClient {

    private GameActivity activity;
    private GamePrefsSaver gprefs;

    public GameController(GameActivity activity) {
        this.activity = activity;
        gprefs = new GamePrefsSaver(activity);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith("mailto:")) {
            Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
            activity.startActivity(i);
            return true;
        } else if (url.startsWith("tg:") || url.startsWith("https://t.me") || url.startsWith("https://telegram.me")) {
            try {
                WebView.HitTestResult result = view.getHitTestResult();
                String data = result.getExtra();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                view.getContext().startActivity(intent);
            } catch (Exception ex) {
            }
            return true;
        } else {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String reqUrl = request.getUrl().toString();
        if (reqUrl.startsWith("mailto:")) {
            Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(reqUrl));
            activity.startActivity(i);
            return true;
        } else if (reqUrl.startsWith("tg:") || reqUrl.startsWith("https://t.me") || reqUrl.startsWith("https://telegram.me")) {
            try {
                WebView.HitTestResult result = view.getHitTestResult();
                String data = result.getExtra();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                view.getContext().startActivity(intent);
            } catch (Exception ex) {
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (gprefs.getFirstref()) {
            gprefs.setGamePoint(url);
            gprefs.setFirstref(false);
        }
    }
}
