package sil.sil.lis;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProcessActivity extends AppCompatActivity implements SensorEventListener {

  @BindDrawable(R.drawable.wallet) Drawable walletDrawable;

  @BindView(R.id.coin_a) ImageView coinA;
  @BindView(R.id.coin_b) ImageView coinB;
  @BindView(R.id.coin_c) ImageView coinC;
  @BindView(R.id.fire) ImageView fire;
  @BindView(R.id.wallet) ImageView wallet;
  
  private final Timer timer = new Timer();
  private TimerTask timerTask;
  private long endTime;
  
  private boolean isWorking = false;
  
  private int screenX;
  private int screenY;
  
  private int coinBCount;
  private int coinACount;
  private int coinCCount;
  private float fruitSpeed;
  private boolean life = true;
  
  private Random random = new Random();
  private SensorManager sensorManager;
  private Sensor accelerometer;
  private GamePrefsSaver gprefs;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_process);
    ButterKnife.bind(this);
    gprefs = new GamePrefsSaver(this);

    screenX = gprefs.getScreenX();
    screenY = gprefs.getScreenY();
    wallet.setImageDrawable(walletDrawable);

    coinBCount = 0;
    coinACount = 0;
    coinCCount = 0;
    fruitSpeed = 30;

    sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);

    coinA.setY(0 - coinA.getHeight());
    coinA.setX(random.nextInt(screenX));
    coinB.setY(0 - coinB.getHeight());
    coinB.setX(random.nextInt(screenX));
    coinC.setY(0 - coinC.getHeight());
    coinC.setX(random.nextInt(screenX));
    fire.setY(0 - fire.getHeight());
    fire.setX(random.nextInt(screenX));
  }

  public Rect getCollisionObject(int left, int top, int width, int height) {
    return new Rect(left, top, left + width, top + height);
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
      if (event.values[0] > 0.3) {
        if (wallet.getX() + event.values[0] * 4 * -1 >= 0) {
          wallet.setX(wallet.getX() + event.values[0] * 4 * -1);
        }
      } else if (event.values[0] < -0.3) {
        if (wallet.getX() + wallet.getWidth() + event.values[0] * 4 * -1 <= screenX) {
          wallet.setX(wallet.getX() + event.values[0] * 4 * -1);
        }
      }
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
  }

  @Override
  protected void onPause() {
    super.onPause();
    synchronized (timer) {
      isWorking = false;
      timerTask.cancel();
      timer.purge();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    isWorking = true;
    timerTask = new TimerTask() {
      @Override
      public void run() {
        while (isWorking) {
          if ((long) (System.currentTimeMillis() - endTime) > 30) {
            coinA.setY(coinA.getY() + fruitSpeed);
            if (coinA.getY() + coinA.getHeight() >= screenY) {
              coinA.setY(0 - coinA.getHeight());
              coinA.setX(random.nextInt(screenX));
            }
            coinB.setY(coinB.getY() + fruitSpeed);
            if (coinB.getY() + coinB.getHeight() >= screenY) {
              coinB.setY(0 - coinB.getHeight());
              coinB.setX(random.nextInt(screenX));
            }
            coinC.setY(coinC.getY() + fruitSpeed);
            if (coinC.getY() + coinC.getHeight() >= screenY) {
              coinC.setY(0 - coinC.getHeight());
              coinC.setX(random.nextInt(screenX));
            }
            fire.setY(fire.getY() + fruitSpeed + 8);
            if (fire.getY() + fire.getHeight() >= screenY) {
              fire.setY(0 - fire.getHeight());
              fire.setX(random.nextInt(screenX));
            }
            
            if (!life) {
              timer.cancel();
              Intent intent = new Intent(ProcessActivity.this, ResultActivity.class);
              intent.putExtra("score_1", coinACount);
              intent.putExtra("score_2", coinBCount);
              intent.putExtra("score_3", coinCCount);
              startActivity(intent);
              synchronized (timer) {
                isWorking = false;
                timerTask.cancel();
                timer.purge();
                finish();
              }
            }

            if (Rect.intersects(getCollisionObject((int) wallet.getX(), (int) wallet.getY(), wallet.getWidth(), wallet.getHeight())
                    , getCollisionObject((int) coinA.getX(), (int) coinA.getY(), coinA.getWidth(), coinA.getHeight()))) {
              coinA.setY(0 - coinA.getHeight());
              coinA.setX(random.nextInt(screenX));
              coinACount++;
            }

            if (Rect.intersects(getCollisionObject((int) wallet.getX(), (int) wallet.getY(), wallet.getWidth(), wallet.getHeight())
                    , getCollisionObject((int) coinC.getX(), (int) coinC.getY(), coinC.getWidth(), coinC.getHeight()))) {
              coinC.setY(0 - coinC.getHeight());
              coinC.setX(random.nextInt(screenX));
              coinCCount++;
            }

            if (Rect.intersects(getCollisionObject((int) wallet.getX(), (int) wallet.getY(), wallet.getWidth(), wallet.getHeight())
                    , getCollisionObject((int) coinB.getX(), (int) coinB.getY(), coinB.getWidth(), coinB.getHeight()))) {
              coinB.setY(0 - coinB.getHeight());
              coinB.setX(random.nextInt(screenX));
              coinBCount++;
            }

            if (Rect.intersects(getCollisionObject((int) wallet.getX(), (int) wallet.getY(), wallet.getWidth(), wallet.getHeight())
                    , getCollisionObject((int) fire.getX(), (int) fire.getY(), fire.getWidth(), fire.getHeight()))) {
              fire.setY(0 - fire.getHeight());
              fire.setX(random.nextInt(screenX));
              life = false;
            }
            endTime = System.currentTimeMillis();
          }
        }
      }
    };

    timer.schedule(timerTask, 0);
  }
}
