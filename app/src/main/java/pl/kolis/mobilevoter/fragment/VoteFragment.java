package pl.kolis.mobilevoter.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.kolis.mobilevoter.R;
import pl.kolis.mobilevoter.adapter.VotingAnswerAdapter;
import pl.kolis.mobilevoter.utilities.Constants;


public class VoteFragment extends Fragment {

    private static final String TAG = VoteFragment.class.getName();
    @BindView(R.id.recyclerview_answers)
    RecyclerView mAnswersRV;
    @BindView(R.id.duration_count)
    TextView mDurationCountText;

    private VotingAnswerAdapter mAdapter;
    private ArrayList<String> mAnwers;
    private String mQuestion;
    private int mDuration;

    public VoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voting_vote, container, false);
        ButterKnife.bind(this, view);
        mAnwers = getArguments().getStringArrayList(Constants.ANSWERS);
        mQuestion = getArguments().getString(Constants.QUESTION);
        mDuration = getArguments().getInt(Constants.DURATION);
        setupRecyclerView();
        setupCounter();

        return view;
    }

    public void setupRecyclerView() {
        mAdapter = new VotingAnswerAdapter(mAnwers);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mAnswersRV.setLayoutManager(manager);
        mAnswersRV.setAdapter(mAdapter);
    }

    private void setupCounter() {
        CountDownTimer c = new CountDownTimer(mDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long second = (millisUntilFinished / 1000) % 60;
                long minute = (millisUntilFinished / (1000 * 60)) % 60;
                long hour = (millisUntilFinished / (1000 * 60 * 60)) % 24;
                mDurationCountText.setText(String.format("%02d:%02d:%02d", hour, minute, second));
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "TIME EXPIRED");
//                timeText.setText(R.string.done);
                adapter.setPollComplete();
            }
        };
        c.start();
    }


}
