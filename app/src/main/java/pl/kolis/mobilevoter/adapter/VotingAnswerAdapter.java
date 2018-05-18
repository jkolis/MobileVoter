package pl.kolis.mobilevoter.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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

public class VotingAnswerAdapter extends RecyclerView.Adapter<VotingAnswerAdapter.ViewHolder> {

    public List<String> mAnswers;

    public VotingAnswerAdapter(ArrayList<String> answers) {
        mAnswers = answers;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

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
    }

    @Override
    public int getItemCount() {
        return mAnswers.size();
    }
}
