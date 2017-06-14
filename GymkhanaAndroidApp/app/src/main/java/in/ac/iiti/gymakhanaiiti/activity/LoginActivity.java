package in.ac.iiti.gymakhanaiiti.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import in.ac.iiti.gymakhanaiiti.R;
import in.ac.iiti.gymakhanaiiti.views.PanningView;
import in.ac.iiti.gymakhanaiiti.other.SharedPref;
import in.ac.iiti.gymakhanaiiti.other.Vars;

public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = Vars.globalTag;
    private static final int RC_SIGN_IN = 007;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private LinearLayout btnSignIn;
    private LinearLayout btnGuestSignIn;

    private PanningView panningView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //setting status bar color to black as default is orange
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }


        panningView = (PanningView) findViewById(R.id.panningView);
        panningView.startPanning();

        btnSignIn = (LinearLayout) findViewById(R.id.btn_sign_in);
        btnGuestSignIn = (LinearLayout)findViewById(R.id.guest_sign_in);
        btnSignIn.setOnClickListener(this);
        btnGuestSignIn.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    private void signIn() {
        if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.KITKAT_WATCH)
        {
            //Toast.makeText(this,"Login with mail is not supported in kitkat yet.",Toast.LENGTH_SHORT).show();
            guestSignIn();
        }
        signOut();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void signOut() {
        try {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            updateUI(false);
                        }
                    });
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(Vars.globalTag, "handleSignInResult:" + result.isSuccess());
        Status status = result.getStatus();
        if(status == null)
            Log.d(TAG,"status is null");
        else{
            String stat = status.toString();
            Log.d(TAG,stat);

        }
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            String personName = acct.getDisplayName();
            Uri photoUrl = acct.getPhotoUrl();

            String personPhotoUrl;
            if(photoUrl!=null)
            {
                personPhotoUrl = photoUrl.toString();
            }else
            {
                personPhotoUrl = null;
            }
            String email = acct.getEmail();
            if(email!=null&&!email.contains("@iiti.ac.in"))
            {
                Toast.makeText(this,"Please login with IITI mail.", Toast.LENGTH_LONG).show();
                return;
            }



            SharedPref.saveString(getApplicationContext(), Vars.name,personName);
            SharedPref.saveString(getApplicationContext(),Vars.email,email);
            SharedPref.saveString(getApplicationContext(),Vars.picUrl,personPhotoUrl);
            SharedPref.saveBoolean(getApplicationContext(),Vars.isGuest,false);
            SharedPref.saveBoolean(getApplicationContext(),Vars.isLoggedIn,true);
            //starting main activity with name,email and picUrl as extras;
            Intent mainWithData = new Intent(this,MainActivity.class);
            Toast.makeText(this,"Logged In",Toast.LENGTH_LONG).show();
            startActivity(mainWithData);
            finish();
        } else {
            Log.d(Vars.globalTag,"Could Not login");

            SharedPref.saveBoolean(getApplicationContext(),Vars.isLoggedIn,false);
        }
    }
   private void guestSignIn()
   {
       SharedPref.saveString(getApplicationContext(), Vars.name,"   User");
       SharedPref.saveString(getApplicationContext(),Vars.email,"");
       SharedPref.saveString(getApplicationContext(),Vars.picUrl,null);
       SharedPref.saveBoolean(getApplicationContext(),Vars.isGuest,true);
       SharedPref.saveBoolean(getApplicationContext(),Vars.isLoggedIn,true);
       //starting main activity with name,email and picUrl as extras;
       Intent mainWithData = new Intent(this,MainActivity.class);
       //Toast.makeText(this,"Logged In",Toast.LENGTH_LONG).show();
       startActivity(mainWithData);
       finish();
   }
    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
           /* case R.id.btn_sign_in:
                signIn();    TODO fix this
                break;*/
            case R.id.guest_sign_in:
                guestSignIn();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnSignIn.setVisibility(View.GONE);
        } else {
            btnSignIn.setVisibility(View.VISIBLE);
        }
    }
}