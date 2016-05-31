package com.fintech.timpla.mojabanka;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.orm.SugarRecord;

import java.util.List;

/**
 * A fragment representing a single Merchant detail screen.
 * This fragment is either contained in a {@link MerchantListActivity}
 * in two-pane mode (on tablets) or a {@link MerchantDetailActivity}
 * on handsets.
 */
public class MerchantDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_USER_NAME = "user_name";

    /**
     * The dummy content this fragment is presenting.
     */
    private Merchant mItem;
    private TextView mAuthorizedView;
    private ProgressDialog mProgressDialog;
    private Button mButton;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MerchantDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.merchant_detail, container, false);

        mAuthorizedView = (TextView) rootView.findViewById(R.id.merchant_detail);
        mButton = (Button) rootView.findViewById(R.id.detail_approve_button);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Molimo sačekajte...");
        mProgressDialog.show();
        initLayout();

        return rootView;
    }

    private void getAuthorizationData(String objectId){
            SharedPreferences sharedPref = getActivity().getSharedPreferences(
                    LoginActivity.LOGIN_PREFERENCES, Context.MODE_PRIVATE
            );
            String bankAccount = sharedPref.getString("BankAccount", "");

            BackendlessDataQuery query = new BackendlessDataQuery();
            String whereClause =
                    "MerchantId = '" + objectId + "' AND BankAccount = '" + bankAccount + "'";

            query.setWhereClause(whereClause);
            Backendless.Persistence.of(Authorization.class).find(query,
                    new AsyncCallback<BackendlessCollection<Authorization>>() {
                        @Override
                        public void handleResponse(final BackendlessCollection<Authorization> response) {

                            if (response != null && response.getData().size() > 0) {
                                mAuthorizedView.setText("Odobren");
                                mButton.setText("Ukloni");

                                mButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Authorization auth = response.getData().get(0);
                                        unauthorize(auth);
                                    }
                                });
                            }
                            else {
                                mAuthorizedView.setText("Nije odobren");
                                mButton.setText("Odobri");

                                mButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        authorize();
                                    }
                                });
                            }
                            mProgressDialog.dismiss();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                        }
                    });
        }


    private void initLayout(){

        if (getActivity() instanceof MerchantListActivity)
            ((MerchantListActivity) getActivity()).setupRecyclerView(((MerchantListActivity) getActivity()).mRecyclerView);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = null;
            List<Merchant> list = SugarRecord.find(Merchant.class, "object_Id = ?", getArguments().getString(ARG_ITEM_ID));
            if (list != null && list.size() > 0)
                mItem = list.get(0);
            else
                fetchMerchantData(getArguments().getString(ARG_ITEM_ID));

            getAuthorizationData(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null && mItem != null) {
                appBarLayout.setTitle(mItem.getName());
            }
        }
    }

    private void fetchMerchantData(String objectId){
        BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause("objectId = '" + objectId +"'");
        Backendless.Persistence.of(Merchant.class).find(query,
                new AsyncCallback<BackendlessCollection<Merchant>>() {
                    @Override
                    public void handleResponse(BackendlessCollection<Merchant> response) {
                        if (response != null && response.getData().size() > 0)
                            SugarRecord.save(response.getData().get(0));
                        initLayout();
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(MerchantDetailFragment.this.getActivity(), "Nema interneta",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void authorize(){
        mProgressDialog.show();
        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                LoginActivity.LOGIN_PREFERENCES, Context.MODE_PRIVATE
        );
        Authorization auth = new Authorization();
        auth.setUserName(getArguments().getString(ARG_USER_NAME));
        auth.setBankAccount(sharedPref.getString("BankAccount", ""));
        auth.setCipheredBankAccount(sharedPref.getString("BankAccount", ""));
        auth.setMerchantId(mItem.getObjectId());

        Backendless.Persistence.of(Authorization.class).save(auth,
                new AsyncCallback<Authorization>() {
                    @Override
                    public void handleResponse(Authorization response) {
                        SugarRecord.save(response);
                        initLayout();
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        mProgressDialog.dismiss();
                    }
                });
    }

    private void unauthorize(final Authorization auth){
        mProgressDialog.show();
        Backendless.Persistence.of(Authorization.class).remove(auth,
                new AsyncCallback<Long>() {
                    @Override
                    public void handleResponse(Long response) {
                        SugarRecord.delete(auth);
                        initLayout();
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        mProgressDialog.dismiss();
                    }
                });
    }
}
