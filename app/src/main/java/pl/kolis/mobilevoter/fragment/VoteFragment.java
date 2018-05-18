package pl.kolis.mobilevoter.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.kolis.mobilevoter.R;
import pl.kolis.mobilevoter.adapter.VotingAnswerAdapter;
import pl.kolis.mobilevoter.utilities.Constants;


public class VoteFragment extends Fragment {

    @BindView(R.id.recyclerview_answers)
    RecyclerView mAnswersRV;

    private VotingAnswerAdapter mAdapter;
    private ArrayList<String> mAnwers;
    private String mQuestion;

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
        setupRecyclerView();
        return view;
    }

    public void setupRecyclerView() {
        mAdapter = new VotingAnswerAdapter(mAnwers);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mAnswersRV.setLayoutManager(manager);
        mAnswersRV.setAdapter(mAdapter);
    }


}
