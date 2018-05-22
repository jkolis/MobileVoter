package pl.kolis.mobilevoter.activities;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import bluetooth.BluetoothClient;
import bluetooth.MyBluetoothManager;
import pl.kolis.mobilevoter.R;
import pl.kolis.mobilevoter.fragment.StatsFragment;
import pl.kolis.mobilevoter.fragment.VoteFragment;
import pl.kolis.mobilevoter.utilities.Constants;

public class ClientVotingActivity extends AppCompatActivity implements Handler.Callback {

    private static final String TAG = ClientVotingActivity.class.getName();

    private ClientVotingActivity.SectionsPagerAdapter mSectionsPagerAdapter;

    private VoteFragment mVotingFragment;

    private ViewPager mViewPager;
    private Handler mHandler;
    private ArrayList<String> mAnwers;
    private String mQuestion;
    private int mDuration;

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
            new BluetoothClient(device, mHandler);
        }
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
        String msg = new String((byte[]) message.obj).trim();
        int q = Integer.valueOf(msg.substring(0, 1));
        int d = Integer.valueOf(msg.substring(1, 2));
        mQuestion = msg.substring(2, q + 2);
        mDuration = Integer.valueOf(msg.substring(q + 2, q + 2 + d));
        String answers = msg.substring(q + 2 + d);
        String[] answersArray = answers.split(",");
        mAnwers = new ArrayList<>(Arrays.asList(answersArray));
        Log.d(TAG, "message " + msg);
        mVotingFragment.setView(mQuestion, mAnwers, mDuration);
        mVotingFragment.setupCounter();
        return false;
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
                    bundle.putStringArrayList(Constants.ANSWERS, mAnwers);
                    bundle.putString(Constants.QUESTION, mQuestion);
                    bundle.putInt(Constants.DURATION, mDuration);
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

}

