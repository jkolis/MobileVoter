package pl.kolis.mobilevoter.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.kolis.mobilevoter.R;
import pl.kolis.mobilevoter.fragment.VoteFragment;

public class VotingAnswerAdapter extends RecyclerView.Adapter<VotingAnswerAdapter.ViewHolder> {

    private List<String> mAnswers;
    private int mSelectedPosition;
    private Context mContext;
    private String TAG = VotingAnswerAdapter.class.getName();
    private boolean mVoted;
    private VoteFragment mFragment;

    public VotingAnswerAdapter(List<String> answers, Context context, VoteFragment fragment) {
        mAnswers = answers;
        mContext = context;
        mVoted = false;
        mSelectedPosition = -1;
        mFragment = fragment;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final String TAG = ViewHolder.class.getName();
        @BindView(R.id.choice_card_view)
        CardView card;
        @BindView(R.id.answer)
        TextView mAnswer;
        //        @BindView(R.id.vote_count)
//        TextView voteCount;
        @BindView(R.id.checkmark)
        ImageView checkmark;

        public ViewHolder(Context context, View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Position clicked " + getAdapterPosition());
                    if (mSelectedPosition != getAdapterPosition() && !mVoted) {
                        mSelectedPosition = getAdapterPosition();
                        notifyItemChanged(mSelectedPosition);
                        mFragment.saveVote(mSelectedPosition);
                    }
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v = inflater.inflate(R.layout.voting_answer_card, parent, false);
        return new VotingAnswerAdapter.ViewHolder(parent.getContext(), v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mAnswer.setText(mAnswers.get(position));
//        holder.
        Log.d(TAG, "onBindView Holder");
        if (mSelectedPosition == position) {
            holder.card.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            mVoted = true;
        }

    }


    @Override
    public int getItemCount() {
        return mAnswers.size();
    }
}
