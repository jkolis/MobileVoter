package pl.kolis.mobilevoter.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.kolis.mobilevoter.R;
import pl.kolis.mobilevoter.activities.FirebaseActivity;
import pl.kolis.mobilevoter.adapter.VotingAnswerAdapter;
import pl.kolis.mobilevoter.utilities.Constants;


public class VoteFragment extends Fragment {

    private static final String TAG = VoteFragment.class.getName();
    @BindView(R.id.recyclerview_answers)
    RecyclerView mAnswersRV;
    @BindView(R.id.duration_count)
    TextView mDurationCountText;
    @BindView(R.id.question_text)
    TextView mQuestionText;

    private VotingAnswerAdapter mAdapter;
    private List<String> mAnwers;
    private String mQuestion;
    private long mDuration;

    private Handler mHandler;
    private HashMap<String, Integer> mVotes;


    public VoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voting_vote, container, false);
        ButterKnife.bind(this, view);
        if (getArguments().getBoolean(Constants.IS_CLIENT)) {
            mAnwers = getArguments().getStringArrayList(Constants.ANSWERS);
            mQuestion = getArguments().getString(Constants.QUESTION);
            mDuration = getArguments().getInt(Constants.DURATION);
            mQuestionText.setText(mQuestion);
            setupRecyclerView();
        }


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mVotes = new HashMap<>();
        if (mAnwers != null) {
            for (int i = 0; i < mAnwers.size(); i++) {
                mVotes.put("P"+String.valueOf(i), 0);
            }
        }
    }

    public void setupRecyclerView() {
        mAdapter = new VotingAnswerAdapter(mAnwers, getContext(), this);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mAnswersRV.setLayoutManager(manager);
        mAnswersRV.setAdapter(mAdapter);
//        ItemClickSupport.addTo(mAnswersRV)
//                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
//                    @Override
//                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
//                        Log.d(TAG, "Clicked position: " + position);
//                    }
//                });
    }

    public void setupCounter() {
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
                mDurationCountText.setText("TIME'S UP");
//                adapter.setPollComplete();
            }
        };
        c.start();
    }

    public void saveVote(int position) {
        String posString = String.valueOf( "P" + position);
//        Integer value = mVotes.get(posString) + 1;
//        if (value == null) {
//            posString = "P" + posString;
//        }
        if(mVotes.get(posString) != null) {
            mVotes.put(posString, mVotes.get(posString) + 1);
        } else {
            mVotes.put(posString, 1);
        }

        FirebaseActivity fa = (FirebaseActivity) getActivity();
        fa.saveVote(position, mVotes);
    }

    public void setView(String question, List<String> anwers, long duration, HashMap<String, Integer> votes) {
        mQuestion = question;
        mAnwers = anwers;
        mDuration = duration;
        mQuestionText.setText(mQuestion);
        mVotes = votes;
        setupRecyclerView();
    }

}
