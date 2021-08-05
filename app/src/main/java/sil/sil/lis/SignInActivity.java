package sil.sil.lis;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends AppCompatActivity {

    @BindView(R.id.email) EditText email;
    @BindView(R.id.password) EditText password;
    @BindView(R.id.sign_in) Button signIn;
    @BindView(R.id.close_app) Button closeApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.sign_in)
    public void onSignIn() {
        if (email.getText().length() > 0 && email.getText().toString().indexOf('@') != -1 && password.getText().length() > 0) {
            signLogin(email.getText().toString(), password.getText().toString());
        } else {
            Toast.makeText(this, "You wrote incorrect ", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.close_app)
    public void onCloseApp() {
        System.exit(0);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }
    }

    public void register(String email, String password) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignInActivity.this, "Account " + FirebaseAuth.getInstance().getCurrentUser().getEmail() + " was created", Toast.LENGTH_SHORT).show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                        builder.setTitle("Authentication failed")
                                .setMessage("Sign-In/Sign-Up failed")
                                .setCancelable(true)
                                .setPositiveButton("OK", null).create().show();
                    }
                    Intent main = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(main);
                    finish();
                });
    }

    public void signLogin(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Intent main = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(main);
                        finish();
                    } else {
                        register(email, password);
                    }
                });
    }
}