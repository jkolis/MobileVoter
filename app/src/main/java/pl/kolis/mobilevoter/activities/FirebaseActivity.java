package pl.kolis.mobilevoter.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import pl.kolis.mobilevoter.R;
import pl.kolis.mobilevoter.utilities.Utils;

public class FirebaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;
    public GoogleApiClient mGoogleApiClient;
    public DatabaseReference mDatabase;
    public GoogleSignInOptions gso;
    public static final String TAG = FirebaseActivity.class.getName();
    public static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("643902035797-fcv7h4oaiqe445js1am82pu6b9jnv180.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Build GoogleApiClient with AppInvite API for receiving deep links AND for the firebase stuff
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(AppInvite.API)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();


    }

    public void signInToFirebase() {
        if (Utils.isOnline(getApplicationContext())) {
//            if (mAuth.getCurrentUser() != null) {
//                if (mAuth.getCurrentUser().isAnonymous()) {
                    Log.d(TAG, "Anonymously logged");
                    firebaseAuthAnonymous();

        } else {
            Snackbar.make(findViewById(R.id.activity_main), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            signInToFirebase();
                        }
                    }).show();
        }
    }


    /**
     * Connect to the Firebase database with anonymous account
     */
    public void firebaseAuthAnonymous() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        gatherDataFromFirebase(task);
                    }
                });


    }

    public void gatherDataFromFirebase(Task<AuthResult> task) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void saveVote(int position, HashMap<String, Integer> mVotes) {

    }
}

