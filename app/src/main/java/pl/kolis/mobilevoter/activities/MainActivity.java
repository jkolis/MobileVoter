package pl.kolis.mobilevoter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.kolis.mobilevoter.R;

public class MainActivity extends FirebaseActivity {

    @BindView((R.id.new_session_btn)) Button mNewSessionBtn;
    @BindView(R.id.join_session_btn) Button joinSessionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        signInToFirebase();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            Snackbar.make(findViewById(R.id.activity_main),
                    "Logged in as " + user.getUid().substring(0, 5), Snackbar.LENGTH_SHORT).show();
        }
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                    if (user.getEmail() != null) {
//                        Snackbar.make(findViewById(R.id.activity_main),
//                                "Logged in as " + user.getEmail(), Snackbar.LENGTH_SHORT).show();
//                    } else {
                        Snackbar.make(findViewById(R.id.activity_main),
                                "Logged in as " + user.getUid().substring(0,5), Snackbar.LENGTH_SHORT).show();
//                    }
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @OnClick(R.id.new_session_btn)
    public void newSession() {
        Intent intent = new Intent(MainActivity.this, CreateSessionActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.join_session_btn)
    public void joinSession() {
        Intent i = new Intent(MainActivity.this, ClientVotingActivity.class);
        startActivity(i);
    }
}

