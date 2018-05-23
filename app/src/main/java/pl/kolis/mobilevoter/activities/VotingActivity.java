package pl.kolis.mobilevoter.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bluetooth.BluetoothServer;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.kolis.mobilevoter.R;
import pl.kolis.mobilevoter.database.entity.Question;
import pl.kolis.mobilevoter.fragment.StatsFragment;
import pl.kolis.mobilevoter.fragment.VoteFragment;
import pl.kolis.mobilevoter.utilities.Constants;

public class VotingActivity extends FirebaseActivity implements Handler.Callback {

    private static final String TAG = VotingActivity.class.getName();
    private Handler mHandler;
    @BindView(R.id.fab_start)
    FloatingActionButton startFab;

    private BluetoothAdapter mBluetoothAdapter;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_TURNING_OFF) {
                    showBTSnackbar();
//                    Toast.makeText(getApplicationContext(), "Turn on BT!", Toast.LENGTH_LONG).show();
                }
            }
        }
    };
    private VoteFragment mVotingFragment;
    private BluetoothServer mBluetoothServer;
    private StatsFragment mStatsFragment;

    private void showBTSnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), "Turn on BT!", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Turn on", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOnBluetooth();
            }
        });
        snackbar.show();
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private List<String> mAnwers;
    private String mQuestion;
    private long mDuration;
    private String mPollId;
    private Map<String, Integer> mVoters;
    private Map<String, Integer> mVotes;
    private int[] mVotesArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        ButterKnife.bind(this);
//        millisFromNo

        Intent i = getIntent();
        mQuestion = i.getStringExtra(Constants.QUESTION);
        mAnwers = i.getStringArrayListExtra(Constants.ANSWERS);
        long dur = i.getLongExtra(Constants.DURATION, 600000);
        mDuration = (int) dur;
        mPollId = i.getStringExtra(Constants.POLL_ID);
        mVotesArray = new int[mAnwers.size()];

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mHandler = new Handler(this);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }


    @OnClick(R.id.fab_start)
    public void startSession(View view) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not supported on this device. " +
                    "Application is terminated", Toast.LENGTH_LONG).show();
            return; //TODO
        }
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Log.d(TAG, "BT not visible");
//            showBTSnackbar();
            requestVisibility();
            beginSession();
        }
        if (mBluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            //TODO
            Log.d(TAG, "BT not visible");
            view.setVisibility(View.GONE);
            beginSession();
        }
    }

    private void turnOnBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
        IntentFilter filterBT = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver, filterBT); //TODO context
        //TODO unregister
//            BluetoothServer bluetoothHost = new BluetoothServer(mBluetoothAdapter); //TODO to przy naciśnieciu start session
    }

    private void requestVisibility() {
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1200); //todo tyle ile sesja
        startActivityForResult(discoverableIntent, Constants.DISCOVERABLE_REQ);
    }

    @Override
    protected void onStart() {
        super.onStart();
        signInToFirebase();
    }

    @Override
    public void gatherDataFromFirebase(Task<AuthResult> task) {
        mDatabase = FirebaseDatabase.getInstance().getReference("poll/" + mPollId);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Question poll = dataSnapshot.getValue(Question.class);
                Toast.makeText(getApplicationContext(), poll.getText(), Toast.LENGTH_LONG).show();
                mAnwers = poll.getAnswers();
                mQuestion = poll.getText();
                mDuration = poll.getDuration();
                mVoters = poll.getVoters();
                mVotes = poll.getVotes();
                if (mVotes != null) {
                    for (Map.Entry<String, Integer> item : poll.getVotes().entrySet()) {
                        int index = Integer.valueOf(item.getKey().substring(1));
                        mVotesArray[index] = item.getValue();
                    }
                } else {
                    mVotesArray[0] = 0;
                }

                mStatsFragment.updateView(mVotesArray);
//                countVotes(poll.getVotes());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void countVotes(Map<Integer, Integer> votes) {

    }

    @Override
    public void saveVote(int position, HashMap<String, Integer> votes) {
        mDatabase = FirebaseDatabase.getInstance().getReference("poll/" + mPollId);
        mDatabase.child("voters").child(mAuth.getCurrentUser().getUid()).setValue(position);
        int value = votes.get("P"+String.valueOf(position));
        mDatabase.child("votes").child("P"+String.valueOf(position)).setValue(value);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            Toast.makeText(getApplicationContext(), "BT succeed", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == Constants.DISCOVERABLE_REQ && resultCode == RESULT_OK) {
            beginSession();
        }
    }

    private void beginSession() {
//        StringBuilder sb = new StringBuilder();
//        sb.append(mQuestion.length()).append(String.valueOf(mDuration).length()).append(mQuestion).append(mDuration);
//        StringBuilder answers = new StringBuilder();
//        for (String s : mAnwers) {
//            answers.append(s).append(",");
//        }
//        sb.append(answers);
        mBluetoothServer = new BluetoothServer(mBluetoothAdapter, mHandler, this, mPollId);
        mVotingFragment.setupCounter();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_voting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mBluetoothServer.cancel();
    }

    @Override
    public boolean handleMessage(Message message) {
//        String msg = "";
        Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_LONG).show();
        if (message.what == Constants.MESSAGE_WRITE) {
            switch (message.arg1) {
                //TODO
            }
//            msg = new String((byte[])message.obj);
        }
//        Log.d(TAG, "message " + msg);
        return false;
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Bundle bundle;
            switch (position) {
                case 0:
                    bundle = new Bundle();
                    bundle.putStringArrayList(Constants.ANSWERS, (ArrayList<String>) mAnwers);
                    bundle.putString(Constants.QUESTION, mQuestion);
                    bundle.putInt(Constants.DURATION, (int) mDuration);
                    bundle.putBoolean(Constants.IS_CLIENT, true);
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

    public void onNewVoter() {
//        Toast.makeText(getApplicationContext(), "New voter", Toast.LENGTH_LONG).show();
//        mBluetoothServer.send(mQuestion, Constants.QUESTION_MSG);
//        StringBuilder answers = new StringBuilder();
//        for (String s : mAnwers) {
//            answers.append(s).append(",");
//        }
//        mBluetoothServer.send(answers.toString(), Constants.ANSWERS_MSG);
    }
}
