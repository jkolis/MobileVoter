package pl.kolis.mobilevoter;

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
    @BindView(R.id.timeDurationButton)
    Button timeButton;
    @BindView(R.id.confirm_create_fab)
    FloatingActionButton create;

    ArrayList<String> mAnswers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.confirm_create_fab)
    public void onConfrimClick() {
        Intent i = new Intent(CreateSessionActivity.this, VotingActivity.class);
        startActivity(i);
    }

    public void onCardClick(View view) {
        Log.d(TAG, "CLICKED!");
        if(addEmptyAnswerCard()) {
            Button btn = (Button) view;
            btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.circle, 0, 0, 0);
//            view.setVisibility(View.GONE);
        }
    }

    private boolean validateAnswer(ArrayList<String> textInputEditTexts) {
        return !(textInputEditTexts.get(textInputEditTexts.size()-1).equals(""));

    }

    private boolean addEmptyAnswerCard() {
        LinearLayout linearLayout = ((LinearLayout) findViewById(R.id.cards_linear));

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

        if(!(validateAnswer(mAnswers))) {
            return false;
        }

        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.answer_card_layout, linearLayout, false);
        linearLayout.addView(myLayout);
        return true;
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


