package com.fintech.timpla.mojabanka;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public static final java.lang.String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Merchant mItem;
    private TextView mAuthorizedView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MerchantDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initLayout();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.merchant_detail, container, false);

        mAuthorizedView = (TextView) rootView.findViewById(R.id.merchant_detail);

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
                        public void handleResponse(BackendlessCollection<Authorization> response) {

                            mAuthorizedView.setText(
                                    String.valueOf(response != null && response.getData().size() > 0));
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                        }
                    });
        }


    private void initLayout(){
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
}
