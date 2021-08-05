package sil.sil.lis;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindDimen;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OptionsActivity extends AppCompatActivity {

    @BindView(R.id.user_img) ImageView userImg;
    @BindView(R.id.change_photo) Button changePhoto;
    @BindView(R.id.market) Button market;
    @BindView(R.id.back_to_menu) Button backToMenu;

    @BindDrawable(R.drawable.def_usr) Drawable defUsr;

    @BindDimen(R.dimen.dimen) int dimen;

    private String userPhotoLink;
    private GamePrefsSaver gprefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        ButterKnife.bind(this);
        gprefs = new GamePrefsSaver(this);
        userPhotoLink = gprefs.getUserPhoto();
        ActivityCompat.requestPermissions(OptionsActivity.this, new String[]{android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        setPhoto();
    }

    @OnClick(R.id.change_photo)
    public void onChangePhoto() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 69);
    }

    @OnClick(R.id.market)
    public void onMarket() {
        try {
            String marketStr = "market://details?id=" + getPackageName();
            Intent market = new Intent(Intent.ACTION_VIEW, Uri.parse(marketStr));
            startActivity(market);
        } catch (ActivityNotFoundException e) {
            String marketStr = "https://play.google.com/store/apps/details?id=" + getPackageName();
            Uri.parse(marketStr);
        }
    }

    @OnClick(R.id.back_to_menu)
    public void onBackToMenu() {
        Intent home = new Intent(this, MainActivity.class);
        startActivity(home);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 69 && resultCode == RESULT_OK) {
            userPhotoLink = savePhoto((Bitmap) Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(data).getExtras()).get("data")));
            setPhoto();
        }
    }

    private String savePhoto(Bitmap photoBitmap) {
        File file = new File(getCacheDir(), "username" + UUID.randomUUID() + ".png");
        try (OutputStream out = getContentResolver().openOutputStream(Uri.fromFile(file))) {
            photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return file.getAbsolutePath();
        } catch (IOException e) {
            return "DEFAULT_USER_PHOTO";
        }
    }

    private void setPhoto() {
        if (!userPhotoLink.equals("DEFAULT_USER_PHOTO")) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDither = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.outHeight = dimen;
            options.outWidth = dimen;
            try (InputStream stream = getContentResolver().openInputStream(Uri.fromFile(new File(userPhotoLink)))) {
                Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
                userImg.setImageBitmap(bitmap);
                gprefs.setUserPhoto(userPhotoLink);
            } catch (IOException e) {
            }
        } else {
            userImg.setImageDrawable(defUsr);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}