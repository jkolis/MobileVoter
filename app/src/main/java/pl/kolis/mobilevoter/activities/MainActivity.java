package pl.kolis.mobilevoter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.kolis.mobilevoter.R;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.join_session_btn) Button joinSessionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Button newSessionBtn = (Button) findViewById(R.id.new_session_btn);
        newSessionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, CreateSessionActivity.class);
                startActivity(intent);
            }
        });
    }

}

