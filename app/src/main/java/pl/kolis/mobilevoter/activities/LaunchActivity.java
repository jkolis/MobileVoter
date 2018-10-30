package pl.kolis.mobilevoter.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import pl.kolis.mobilevoter.R;

/**
 * Welcome screen
 */
public class LaunchActivity extends AppCompatActivity {

    @BindView(R.id.login_btn) Button loginBtn;
    @BindView(R.id.dont_btn) Button dontLogBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LaunchActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        dontLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LaunchActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}
