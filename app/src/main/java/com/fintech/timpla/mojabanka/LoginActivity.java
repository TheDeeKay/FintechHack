package com.fintech.timpla.mojabanka;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

public class LoginActivity extends AppCompatActivity {

    private RelativeLayout mImage;

    public static final String MERCHANT_ID_APPROVAL_EXTRA = "MerchantApproval";
    public static final String MERCHANT_USERNAME_EXTRA = "MerchantUsername";

    public static final String LOGIN_PREFERENCES = "login_pref";

    private ProgressDialog mProgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final RelativeLayout loadingImage = (RelativeLayout) findViewById(R.id.loading_image);
        mImage = loadingImage;
        loadingImage.setVisibility(View.INVISIBLE);

        /*
        Check whether the user is already logged in with valid credentials
         */
        SharedPreferences loginPref = getSharedPreferences(LOGIN_PREFERENCES, MODE_PRIVATE);
        if (loginPref.getString("EMail", null) != null) {

            loadingImage.setVisibility(View.VISIBLE);

            BackendlessDataQuery query = new BackendlessDataQuery();

            String email = loginPref.getString("EMail", "");
            String password = loginPref.getString("Password", "");
            String bankAccount = loginPref.getString("BankAccount", "");

            query.setWhereClause("EMail = '" + email + "' AND Password = '" + password
            + "' AND BankAccount = '" + bankAccount + "'");

            Backendless.Persistence.of(BankUser.class).find(query,
                    new AsyncCallback<BackendlessCollection<BankUser>>() {
                        @Override
                        public void handleResponse(BackendlessCollection<BankUser> response) {
                            if (response != null && response.getData().size() > 0)
                                loginSuccess();
                            else
                                loadingImage.setVisibility(View.INVISIBLE);

                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(LoginActivity.this,
                                    "Problem sa internet konekcijom", Toast.LENGTH_LONG).show();
                            loadingImage.setVisibility(View.INVISIBLE);
                        }
                    });
        }

        Button loginButton = (Button) findViewById(R.id.login_button);

        final EditText textEmail = (EditText) findViewById(R.id.login_email);
        final EditText textPassword = (EditText) findViewById(R.id.login_password);

        assert loginButton != null;
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                mProgDialog = new ProgressDialog(LoginActivity.this);
                mProgDialog.show();

                v.setClickable(false);

                final String email = String.valueOf(textEmail.getText());
                final String password = String.valueOf(textPassword.getText());

                /*
                Check for basic email and password validity
                 */
                if (!validateEmail(email)) {
                    loginFailed(v, "E-mail nije validan");
                    return;
                }

                if (!validatePassword(password)) {
                    loginFailed(v, "Lozinka je previše kratka.");
                    return;
                }

                BackendlessDataQuery query = new BackendlessDataQuery();
                String whereClause = "EMail = '" + email + "' AND Password = '" + password + "'";
                query.setWhereClause(whereClause);

                /*
                Check whether the credentials just entered are registered
                 */
                Backendless.Persistence.of(BankUser.class).find(query,
                        new AsyncCallback<BackendlessCollection<BankUser>>() {
                            @Override
                            public void handleResponse(BackendlessCollection<BankUser> response) {

                                String bankAccount;

                                if (response != null && response.getData().size() > 0)
                                    bankAccount = response.getData().get(0).getBankAccount();
                                else {
                                    loginFailed(v, "Neispravan e-mail ili lozinka");
                                    return;
                                }

                                SharedPreferences sharedPreferences = getSharedPreferences(
                                        LOGIN_PREFERENCES, MODE_PRIVATE);
                                sharedPreferences.edit().putString("EMail", email)
                                        .putString("Password", password)
                                        .putString("BankAccount", bankAccount).apply();

                                loginSuccess();
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Log.e("Login Failed", fault.getMessage());
                                loginFailed(v, "Konekcija sa serverom neuspešna");
                            }
                        });
            }
        });
    }

    /*
    Checks for basic email validity
     */
    public boolean validateEmail(String email){
        if (email == null || email.isEmpty())
            return false;

        int monkeyIndex = email.indexOf('@');

        if (monkeyIndex == -1 || email.indexOf('@', monkeyIndex+1) != -1)
            return false;

        int dotIndex = email.indexOf('.');
        if (dotIndex == -1 || email.indexOf('.', dotIndex+1) != -1)
            return false;

        if((monkeyIndex == 0) || (dotIndex - monkeyIndex < 2) || (dotIndex == email.length() - 1))
            return false;

        return true;
    }

    /*
    Checks for basic password validity
     */
    public boolean validatePassword(String password){
        if (password == null || password.length() < 6)
            return false;

        return true;
    }

    /*
    The login has failed, reactivate button and inform user of the problem
     */
    private void loginFailed(View button, String message){
        button.setClickable(true);
        mProgDialog.dismiss();

        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }

    /*
    The login is successful, end this activity and send the user directly to the list activity
     */
    private void loginSuccess(){

        if (mImage != null)
            mImage.setVisibility(View.INVISIBLE);

        if (getIntent() != null && getIntent().getExtras() != null
                && getIntent().hasExtra(MERCHANT_ID_APPROVAL_EXTRA) && getIntent().hasExtra(MERCHANT_USERNAME_EXTRA)) {

            FragmentManager fm = getSupportFragmentManager();
            MerchantDetailFragment fragment = new MerchantDetailFragment();
            Bundle arguments = new Bundle();
            arguments.putString(MerchantDetailFragment.ARG_ITEM_ID, getIntent().getStringExtra(MERCHANT_ID_APPROVAL_EXTRA));
            arguments.putString(MerchantDetailFragment.ARG_USER_NAME, getIntent().getStringExtra(MERCHANT_USERNAME_EXTRA));
            fragment.setArguments(arguments);
            fm.beginTransaction().add(R.id.login_root, fragment).commit();

        }
        else {
            Intent intent;

            intent = new Intent(this, MerchantListActivity.class);

            if (getIntent() != null && getIntent().getExtras() != null
                    && getIntent().hasExtra(MERCHANT_ID_APPROVAL_EXTRA) && getIntent().hasExtra(MERCHANT_USERNAME_EXTRA)) {

                FragmentManager fm = getSupportFragmentManager();
                MerchantDetailFragment fragment = new MerchantDetailFragment();
                Bundle arguments = new Bundle();
                arguments.putString(MerchantDetailFragment.ARG_ITEM_ID, getIntent().getStringExtra(MERCHANT_ID_APPROVAL_EXTRA));
                arguments.putString(MerchantDetailFragment.ARG_USER_NAME, getIntent().getStringExtra(MERCHANT_USERNAME_EXTRA));
                fragment.setArguments(arguments);
                fm.beginTransaction().add(R.id.login_root, fragment).commit();

            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
            finish();
        }
    }
}
