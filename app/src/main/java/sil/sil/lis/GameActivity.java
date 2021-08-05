package sil.sil.lis;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

public class GameActivity extends AppCompatActivity {

    private ValueCallback<Uri[]> uploadMessage;
    private String photoPath;

    private WebView webView;
    private ProgressBar progressBar;
    private GamePrefsSaver gprefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        gprefs = new GamePrefsSaver(this);
        setCooks();
        webView.setWebViewClient(new GameController(this));
        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                int permissionStatus = ContextCompat.checkSelfPermission(GameActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    if (uploadMessage != null) {
                        uploadMessage.onReceiveValue(null);
                    }
                    uploadMessage = filePathCallback;
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                            takePictureIntent.putExtra("PhotoPath", photoPath);
                        } catch (IOException ex) {
                        }
                        if (photoFile != null) {
                            photoPath = "file:" + photoFile.getAbsolutePath();
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(photoFile));
                        } else {
                            takePictureIntent = null;
                        }
                    }
                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    contentSelectionIntent.setType("image/*");
                    Intent[] intentArray;
                    if (takePictureIntent != null) {
                        intentArray = new Intent[]{takePictureIntent};
                    } else {
                        intentArray = new Intent[0];
                    }
                    Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                    chooser.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooser.putExtra(Intent.EXTRA_TITLE, "Photo");
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(chooser, 1);
                    return true;
                } else {
                    ActivityCompat.requestPermissions(
                            GameActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA},
                            1);
                }
                return false;
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setActivated(true);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                    progressBar.setActivated(false);
                }
            }
        });

        webView.loadUrl(gprefs.getGamePoint());
    }

    private File createImageFile() throws IOException {
        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DirectoryNameHere");
        if (!imageStorageDir.exists())
            imageStorageDir.mkdirs();
        imageStorageDir = new File(imageStorageDir + File.separator + "Photo_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        return imageStorageDir;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 1 || uploadMessage == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null || data.getData() == null) {
                if (photoPath != null) {
                    results = new Uri[]{Uri.parse(photoPath)};
                }
            } else {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }
        uploadMessage.onReceiveValue(results);
        uploadMessage = null;
    }

    private RelativeLayout getLayout() {
        webView = new WebView(this);
        progressBar = new ProgressBar(this);
        RelativeLayout layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(layoutParams);
        webView.setLayoutParams(layoutParams);
        layout.addView(webView, layoutParams);
        setSettsToLayout();
        layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        progressBar.setFitsSystemWindows(true);
        layout.addView(progressBar, layoutParams);
        return layout;
    }

    public void setCooks(){
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.acceptCookie();
        cookieManager.setAcceptThirdPartyCookies(webView, true);
        cookieManager.flush();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setSettsToLayout(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setSavePassword(true);
        webView.requestFocus(View.FOCUS_DOWN);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exit")
                    .setMessage("Do you really want to exit the application?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", (arg0, arg1) -> System.exit(0))
                    .setNegativeButton("No", null).create().show();
        }
    }
}