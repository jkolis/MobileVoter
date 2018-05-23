package pl.kolis.mobilevoter.activities;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bluetooth.BluetoothClient;
import pl.kolis.mobilevoter.R;
import pl.kolis.mobilevoter.database.entity.Question;
import pl.kolis.mobilevoter.fragment.StatsFragment;
import pl.kolis.mobilevoter.fragment.VoteFragment;
import pl.kolis.mobilevoter.utilities.Constants;

public class ClientVotingActivity extends FirebaseActivity implements Handler.Callback {

    private static final String TAG = ClientVotingActivity.class.getName();

    private ClientVotingActivity.SectionsPagerAdapter mSectionsPagerAdapter;

    private VoteFragment mVotingFragment;

    private ViewPager mViewPager;
    private Handler mHandler;
    private List<String> mAnwers;
    private String mQuestion;
    private long mDuration;
    private String mPollId;
    private StatsFragment mStatsFragment;
    private int[] mVotesArray;
    private Map<String, Integer> mVoters;
    private HashMap<String, Integer> mVotes;
    private BluetoothClient mBluetoothClient;
    private Date mExpireTime = Calendar.getInstance().getTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        mHandler = new Handler(this);

        Intent i = new Intent(ClientVotingActivity.this, DeviceListActivity.class);
        startActivityForResult(i, Constants.PICK_DEVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new ClientVotingActivity.SectionsPagerAdapter(
                getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PICK_DEVICE && resultCode == RESULT_OK) {
            BluetoothDevice device = data.getParcelableExtra(Constants.DEVICE);

            Log.d(TAG, "You choose " + device.getName());
            mBluetoothClient = new BluetoothClient(device, mHandler);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mBluetoothClient.cancel();
    }

    @Override
    public boolean handleMessage(Message message) {
//        Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_LONG).show();
//        String answers = new String((byte[]) message.obj).trim();
//        if (message.what == Constants.MESSAGE_READ) {
//            switch (message.arg1) {
//                case Constants.QUESTION_MSG:
//                    mQuestion = new String((byte[]) message.obj);
//                    break;
//                case Constants.ANSWERS_MSG:
//                    String[] answersArray = answers.split(",");
//                    mAnwers = new ArrayList<>(Arrays.asList(answersArray));
//            }
//        }
//        String msg = new String((byte[]) message.obj).trim();
//        int flag = Integer.valueOf(msg.substring(0, 1));
//        switch (flag) {
//            case Constants.POLL_MSG:
//                dealWithPoll(msg);
//                return false;
//            case Constants.VOTE_MSG:
//                int position = Integer.valueOf(msg.substring(1));
//                onVoted(position);
//
//        }
        mPollId = new String((byte[]) message.obj).trim();
        setupPoll(mPollId);

        return false;
    }

    private void setupPoll(String pollId) {
        mDatabase = FirebaseDatabase.getInstance().getReference("poll/" + pollId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Question poll = dataSnapshot.getValue(Question.class);
                mAnwers = poll.getAnswers();
                mQuestion = poll.getText();
                mDuration = poll.getDuration();
                mVoters = poll.getVoters();
                mVotes = poll.getVotes();

                Toast.makeText(getApplicationContext(), poll.getText(), Toast.LENGTH_LONG).show();
                if (poll.getExpireTime() != null) {
                    try {
                        mExpireTime.setTime(java.text.DateFormat.getDateTimeInstance().parse(poll.getExpireTime()).getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                mVotesArray = new int[mAnwers.size()];
                if (mVotes != null) {
                    for (Map.Entry<String, Integer> item : poll.getVotes().entrySet()) {
                        int index = Integer.valueOf(item.getKey().substring(1));
                        mVotesArray[index] = item.getValue();
                    }
                } else {
                    mVotesArray[0] = 0;
                }

                mStatsFragment.updateView(mVotesArray);
                Date now = Calendar.getInstance().getTime();
                if (mExpireTime != null) {
                    mDuration = mExpireTime.getTime() - now.getTime();
                }
                String user = mAuth.getCurrentUser().getUid();
                boolean checked = false;
                if (mVoters != null && mVoters.containsKey(user)) {
                    checked = true;
                }
                mVotingFragment.setView(mQuestion, mAnwers, mDuration, mVotes, checked);
                mVotingFragment.setupCounter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled");
            }
        });
    }


    @Override
    public void saveVote(int position, HashMap<String, Integer> votes) {
        mDatabase.child("Voters").child(mAuth.getCurrentUser().getUid()).setValue(position);
        mDatabase.child("voters").child(mAuth.getCurrentUser().getUid()).setValue(position);
        int value = votes.get("P" + position);
        mDatabase.child("votes").child("P" + String.valueOf(position)).setValue(value);
    }

    private void dealWithPoll(String msg) {
//        int q = Integer.valueOf(msg.substring(1, 2));
//        int d = Integer.valueOf(msg.substring(2, 3));
//        mQuestion = msg.substring(3, q + 3);
//        mDuration = Integer.valueOf(msg.substring(q + 3, q + 3 + d));
//        String answers = msg.substring(q + 3 + d);
//        String[] answersArray = answers.split(",");
//        mAnwers = new List<>(Arrays.asList(answersArray));
//        Log.d(TAG, "message " + msg);
//        mVotingFragment.setView(mQuestion, mAnwers, mDuration);
//        mVotingFragment.setupCounter();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList(Constants.ANSWERS, (ArrayList) mAnwers);
                    bundle.putString(Constants.QUESTION, mQuestion);
                    bundle.putInt(Constants.DURATION, (int) mDuration);
                    mVotingFragment = new VoteFragment();
                    mVotingFragment.setArguments(bundle);
                    return mVotingFragment;
                case 1:
                    bundle = new Bundle();
                    mStatsFragment = new StatsFragment();
                    bundle.putIntArray(Constants.VOTES_ARRAY, mVotesArray);
                    mStatsFragment.setArguments(bundle);
                    return mStatsFragment;
            }
            return new VoteFragment();
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }

}

