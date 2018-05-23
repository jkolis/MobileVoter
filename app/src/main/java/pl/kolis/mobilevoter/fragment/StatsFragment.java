package pl.kolis.mobilevoter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.kolis.mobilevoter.R;
import pl.kolis.mobilevoter.utilities.Constants;


public class StatsFragment extends Fragment {

    PieChart mPieChart;
    private int[] mVotesArray;

    public StatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voting_stats, container, false);
//        ButterKnife.bind(this, container);

        mVotesArray = getArguments().getIntArray(Constants.VOTES_ARRAY);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mPieChart = (PieChart) view.findViewById(R.id.pie_chart);    }



    public void updateView(int[] votes) {
        mVotesArray = votes;
        if (!(votes.length == 1 && votes[0] == 0)) {
            setView();
        }
    }

    private void setView() {
        List<PieEntry> entries = new ArrayList<>();

        for (Integer vote : mVotesArray) {
            entries.add(new PieEntry(vote));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Votes");

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        mPieChart.setData(data);
        mPieChart.invalidate();
    }
}
