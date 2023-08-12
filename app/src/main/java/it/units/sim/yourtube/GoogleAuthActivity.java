package it.units.sim.yourtube;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class GoogleAuthActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
//    private static final int RC_SIGN_IN = 3;
    private GoogleAccountCredential credential;
    private static final String[] YOUTUBE_API_SCOPES = { YouTubeScopes.YOUTUBE_READONLY };
    private GoogleCredentialManager credentialManager;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private SharedPreferences defaultSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_auth);
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        credentialManager = GoogleCredentialManager.getInstance();
        credential = GoogleAccountCredential
                .usingOAuth2(getApplicationContext(), Arrays.asList(YOUTUBE_API_SCOPES))
                .setBackOff(new ExponentialBackOff());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestScopes(new Scope(YouTubeScopes.YOUTUBE_READONLY))
                .build();

        ActivityResultLauncher<Intent> signInActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleSignInResult
        );

        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.newGoogleSignInButton).setOnClickListener(v -> {
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            signInActivityLauncher.launch(signInIntent);
//            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

    }

    private void handleSignInResult(ActivityResult result) {
        Intent data = result.getData();
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            String idToken = account.getIdToken();
            localLogin(account.getEmail());
            remoteLoginAndOpenMainActivity(idToken);
        } catch (ApiException e) {
            // GoogleSignInStatusCodes class for more info.
            Log.w(TAG, "signInResult: failed code = " + e.getStatusCode());
            switch (e.getStatusCode()) {
                case CommonStatusCodes.CANCELED:
                    Log.d(TAG, "Sign In canceled");
                    break;
                case CommonStatusCodes.NETWORK_ERROR:
                    Log.d(TAG, "Network error.");
                    break;
                default:
                    Log.d(TAG, "Couldn't get credential from result." + e.getLocalizedMessage());
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing GoogleSignInAccount (already logged user)
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser currentFirebaseUser = mAuth.getCurrentUser();
        if (currentFirebaseUser != null) {
            System.out.println("firebase logged");
            System.out.println(currentFirebaseUser.getEmail());
        }
        if (account != null) {
            System.out.println(" already logged in ");
            String accountName = account.getEmail();
            credential.setSelectedAccountName(accountName);
            credentialManager.setCredential(credential);
            openMainActivity();
        }
    }

    private void openMainActivity() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void localLogin(String accountName) {
        credential.setSelectedAccountName(accountName);
        credentialManager.setCredential(credential);
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.apply();
    }

    private void remoteLoginAndOpenMainActivity(String idToken) {
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        openMainActivity();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signIn : failure", task.getException());
                        Toast.makeText(
                                this,
                                "Authentication process encountered an error",
                                Toast.LENGTH_SHORT).show();
                        // TODO: Snackbar
                    }
                });
    }

}