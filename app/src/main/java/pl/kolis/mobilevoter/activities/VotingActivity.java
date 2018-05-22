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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import bluetooth.BluetoothServer;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.kolis.mobilevoter.R;
import pl.kolis.mobilevoter.fragment.StatsFragment;
import pl.kolis.mobilevoter.fragment.VoteFragment;
import pl.kolis.mobilevoter.utilities.Constants;

public class VotingActivity extends AppCompatActivity implements Handler.Callback {

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

    private ArrayList<String> mAnwers;
    private String mQuestion;
    private int mDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        ButterKnife.bind(this);

        Intent i = getIntent();
        mQuestion = i.getStringExtra(Constants.QUESTION);
        mAnwers = i.getStringArrayListExtra(Constants.ANSWERS);
        long dur = i.getLongExtra(Constants.DURATION, 0);
        mDuration = (int) dur;

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
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300); //todo tyle ile sesja
        startActivityForResult(discoverableIntent, Constants.DISCOVERABLE_REQ);
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
        StringBuilder sb = new StringBuilder();
        sb.append(mQuestion.length()).append(String.valueOf(mDuration).length()).append(mQuestion).append(mDuration);
        StringBuilder answers = new StringBuilder();
        for (String s : mAnwers) {
            answers.append(s).append(",");
        }
        sb.append(answers);
        mBluetoothServer = new BluetoothServer(mBluetoothAdapter, mHandler, this, sb.toString());
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
            switch (position) {
                case 0:
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList(Constants.ANSWERS, mAnwers);
                    bundle.putString(Constants.QUESTION, mQuestion);
                    bundle.putInt(Constants.DURATION, mDuration);
                    bundle.putBoolean(Constants.IS_CLIENT, true);
                    mVotingFragment = new VoteFragment();
                    mVotingFragment.setArguments(bundle);
                    return mVotingFragment;
                case 1:
                    return new StatsFragment();
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
        mBluetoothServer.send(mQuestion, Constants.QUESTION_MSG);
        StringBuilder answers = new StringBuilder();
        for (String s : mAnwers) {
            answers.append(s).append(",");
        }
        mBluetoothServer.send(answers.toString(), Constants.ANSWERS_MSG);
    }
}
