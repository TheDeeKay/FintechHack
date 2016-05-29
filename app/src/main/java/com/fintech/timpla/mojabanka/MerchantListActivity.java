package com.fintech.timpla.mojabanka;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.orm.SugarRecord;

import java.util.List;

/**
 * An activity representing a list of Merchants. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MerchantDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MerchantListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_list);

        if (findViewById(R.id.merchant_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        if (!mTwoPane && getIntent().hasExtra(LoginActivity.MERCHANT_APPROVAL_EXTRA))
            launchDetails(
                    getIntent().getExtras().getString(LoginActivity.MERCHANT_APPROVAL_EXTRA),
                    false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setTitle(getTitle());
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.merchant_list);
        assert mRecyclerView != null;
        setupRecyclerView(mRecyclerView);

        fetchMerchants();
        fetchAuthorizations();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        List<Merchant> merchants = SugarRecord.listAll(Merchant.class);

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(merchants));
    }

    // Fetch all merchants present in the table
    private void fetchMerchants() {
        Backendless.Persistence.of(Merchant.class).find(
                new AsyncCallback<BackendlessCollection<Merchant>>() {

                    @Override
                    public void handleResponse(BackendlessCollection<Merchant> response) {

                        SugarRecord.saveInTx(response.getData());
                        setupRecyclerView(mRecyclerView);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                    }
                });
    }

    // Fetch all authorizations tied to our user
    private void fetchAuthorizations(){
        BackendlessDataQuery query = new BackendlessDataQuery();
        SharedPreferences sharedPref = getSharedPreferences(LoginActivity.LOGIN_PREFERENCES, MODE_PRIVATE);
        String whereClause = "BankAccount = '" + sharedPref.getString("BankAccount", "") + "'";
        query.setWhereClause(whereClause);
        Backendless.Persistence.of(Authorization.class).find(query,
                new AsyncCallback<BackendlessCollection<Authorization>>() {
                    @Override
                    public void handleResponse(BackendlessCollection<Authorization> response) {
                        SugarRecord.saveInTx(response.getData());
                        setupRecyclerView(mRecyclerView);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                    }
                });
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Merchant> mValues;

        public SimpleItemRecyclerViewAdapter(List<Merchant> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.merchant_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).getName());

            List<Authorization> authorization = SugarRecord.find(Authorization.class, "Merchant_Id = ?",
                    String.valueOf(holder.mItem.getObjectId()));

            // TODO ulepšati ovo
            if (authorization.size() < 1)
                holder.mAuthorizedView.setText("Nije");
            else
                holder.mAuthorizedView.setText("Jeste");

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    launchDetails(holder.mItem.getObjectId(), true);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mIdView;
            public final TextView mContentView;
            public final TextView mAuthorizedView;
            public Merchant mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (ImageView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
                mAuthorizedView = (TextView) view.findViewById(R.id.list_authorized);
            }

            @Override
            public java.lang.String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    private void launchDetails(String objectId, boolean backStack){
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(MerchantDetailFragment.ARG_ITEM_ID, objectId);
            MerchantDetailFragment fragment = new MerchantDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.merchant_detail_container, fragment)
                    .commit();
        } else {
            Context context = MerchantListActivity.this;
            Intent intent = new Intent(context, MerchantDetailActivity.class);
            intent.putExtra(MerchantDetailFragment.ARG_ITEM_ID, objectId);

            if (!backStack)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            context.startActivity(intent);

            if (!backStack)
                finish();
        }
    }
}
