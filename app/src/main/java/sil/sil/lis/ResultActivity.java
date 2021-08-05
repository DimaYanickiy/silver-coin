package sil.sil.lis;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResultActivity extends AppCompatActivity {

  @BindView(R.id.result_text) TextView resultText;
  @BindView(R.id.restart) Button restart;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);
    ButterKnife.bind(this);

    int coins1 = getIntent().getIntExtra("score_1", 0);
    int coins2 = getIntent().getIntExtra("score_2", 0);
    int coins3 = getIntent().getIntExtra("score_3", 0);
    resultText.setText("You collect\n" + coins1 + " first coins\n"
            + coins2 + " second coins\n"
            + coins3 + " third coins!\n" + "You are winner!!");
  }

  @OnClick(R.id.restart)
  public void onToMenu() {
    Intent rest = new Intent(this, ProcessActivity.class);
    startActivity(rest);
    finish();
  }

  @Override
  public void onBackPressed() {
    Intent home = new Intent(this, MainActivity.class);
    startActivity(home);
    finish();
  }
}
