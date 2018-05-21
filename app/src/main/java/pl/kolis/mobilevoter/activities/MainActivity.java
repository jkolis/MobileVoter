package pl.kolis.mobilevoter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.kolis.mobilevoter.R;

public class MainActivity extends AppCompatActivity {

    @BindView((R.id.new_session_btn)) Button mNewSessionBtn;
    @BindView(R.id.join_session_btn) Button joinSessionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.new_session_btn)
    public void newSession() {
        Intent intent = new Intent(MainActivity.this, CreateSessionActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.join_session_btn)
    public void joinSession() {
        Intent i = new Intent(MainActivity.this, ClientVotingActivity.class);
        startActivity(i);
    }
}

