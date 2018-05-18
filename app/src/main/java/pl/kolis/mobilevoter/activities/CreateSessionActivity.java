package pl.kolis.mobilevoter.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mobi.upod.timedurationpicker.TimeDurationPicker;
import mobi.upod.timedurationpicker.TimeDurationPickerDialog;
import pl.kolis.mobilevoter.R;
import pl.kolis.mobilevoter.utilities.Constants;

public class CreateSessionActivity extends AppCompatActivity {
    private static final String TAG = CreateSessionActivity.class.getName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.create_card_view1)
    CardView cardView;
    @BindView(R.id.add_answer_btn)
    Button addBtn;
    @BindView(R.id.questionText)
    EditText question;
    @BindView(R.id.open_checkbox)
    CheckBox openSession;
    @BindView(R.id.time_duration_button)
    Button timeButton;
    @BindView(R.id.confirm_create_fab)
    FloatingActionButton create;

    private String mQuestion;
    private ArrayList<String> mAnswers = new ArrayList<>();
    private long mDurationMs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @OnClick(R.id.time_duration_button)
    public void onDurationClick() {
        TimeDurationPickerDialog timeDurationPickerDialog = new TimeDurationPickerDialog(this,
                new TimeDurationPickerDialog.OnDurationSetListener() {
                    @Override
                    public void onDurationSet(TimeDurationPicker view, long duration) {
                        mDurationMs = duration;
                        formatDurationButton(duration);
                    }
                }, mDurationMs);
        timeDurationPickerDialog.show();
    }

    @OnClick(R.id.confirm_create_fab)
    public void onConfirmClick() {
        mQuestion = question.getText().toString();
        LinearLayout linearLayout = ((LinearLayout) findViewById(R.id.cards_linear));

        getTextChildren(linearLayout);
        Intent i = new Intent(CreateSessionActivity.this, VotingActivity.class);
        i.putExtra(Constants.QUESTION, mQuestion);
        i.putExtra(Constants.ANSWERS, mAnswers);
        i.putExtra(Constants.DURATION, mDurationMs);
        startActivity(i);
    }

    public void onCardClick(View view) {
        Log.d(TAG, "CLICKED!");
        if(addEmptyAnswerCard()) {
            Button btn = (Button) view;
            btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_radio_button_unchecked_24, 0, 0, 0);
        }
    }

    private void formatDurationButton(long millis) {
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;
        String time = String.format("Length: %02dh %02dm %02ds", hour, minute, second);
        timeButton.setText(time);
    }

    private boolean validateAnswer(ArrayList<String> textInputEditTexts) {
        return !(textInputEditTexts.get(textInputEditTexts.size()-1).equals(""));

    }

    private boolean addEmptyAnswerCard() {
        LinearLayout linearLayout = ((LinearLayout) findViewById(R.id.cards_linear));

        getTextChildren(linearLayout);

        if(!(validateAnswer(mAnswers))) {
            return false;
        }

        LayoutInflater inflater = getLayoutInflater();
        View cardLayout = inflater.inflate(R.layout.answer_card_layout, linearLayout, false);
        linearLayout.addView(cardLayout);
        return true;
    }

    private void getTextChildren(LinearLayout linearLayout) {
        ArrayList<View> allViewsWithinMyTopView = getAllChildren(linearLayout);
        mAnswers.clear();

        //find children of type TextInputEditText
        for (View child : allViewsWithinMyTopView) {
            if (child instanceof TextInputEditText) {
                TextInputEditText childTextView = (TextInputEditText) child;
                mAnswers.add(childTextView.getText().toString());
                Log.d(TAG, "Found " + childTextView.getText());
            }
        }
    }

    private ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<>();

        ViewGroup vg = (ViewGroup) v;
        for (int i = 0; i < vg.getChildCount(); i++) {

            View child = vg.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }

}


