package pl.kolis.mobilevoter;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateSessionActivity extends AppCompatActivity {
    private static final String TAG = CreateSessionActivity.class.getName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.create_card_view1)
    CardView cardView;
    @BindView(R.id.circle1)
    Button add;
    @BindView(R.id.questionText)
    EditText question;
    @BindView(R.id.open_checkbox)
    CheckBox openSession;
    @BindView(R.id.timeDurationButton)
    Button timeButton;
    @BindView(R.id.confirm_create_fab)
    FloatingActionButton create;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.confirm_create_fab)
    public void onConfrimClick() {
        Intent i = new Intent(CreateSessionActivity.this, VotingActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.circle1)
    public void onCardClick() {
        Log.d(TAG, "CLICKED!");
    }

}


