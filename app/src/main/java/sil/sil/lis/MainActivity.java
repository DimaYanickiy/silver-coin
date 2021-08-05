package sil.sil.lis;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.play_game) Button playGame;
    @BindView(R.id.options) Button options;
    @BindView(R.id.exit_game) Button exitGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.play_game)
    public void onPlayGame() {
        startActivity(new Intent(MainActivity.this, ProcessActivity.class));
        finish();
    }

    @OnClick(R.id.options)
    public void onOptions() {
        startActivity(new Intent(MainActivity.this, OptionsActivity.class));
        finish();
    }

    @OnClick(R.id.exit_game)
    public void onExitGame() {
        System.exit(0);
    }
}