package com.fintech.timpla.mojabanka;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.List;

/**
 * An activity representing a list of Merchants. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MerchantDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MerchantListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

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

                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setTitle(getTitle());
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.merchant_list);
        assert mRecyclerView != null;
        setupRecyclerView();

        // Fetch all merchants present in the table
        Backendless.Persistence.of(Merchant.class).find(
                new AsyncCallback<BackendlessCollection<Merchant>>() {

                    @Override
                    public void handleResponse(BackendlessCollection<Merchant> response) {

                        List<Merchant> list = response.getData();

                        for (Merchant item: list
                             ) {
                            ContentValues cv = new ContentValues();
                            cv.put(Merchant.NAME, item.getName());
                            cv.put(Merchant.APIKEY, item.getAPIKey());
                            getContentResolver().insert(Merchant.buildMerchantUri(), cv);
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        // TODO handle a fault here
                    }
                });

        if (findViewById(R.id.merchant_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView() {

        mRecyclerView.setAdapter(new SimpleItemRecyclerViewAdapter());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Merchant.buildMerchantUri(),
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ((SimpleItemRecyclerViewAdapter)mRecyclerView.getAdapter()).swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((SimpleItemRecyclerViewAdapter)mRecyclerView.getAdapter()).swapCursor(null);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private Cursor mValues = null;

        public SimpleItemRecyclerViewAdapter() {
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.merchant_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            mValues.moveToPosition(position);
            holder.mItem = new Merchant(
                    mValues.getString(mValues.getColumnIndex(Merchant.NAME)),
                    mValues.getString(mValues.getColumnIndex(Merchant.APIKEY)));
            holder.mIdView.setText(String.valueOf(holder.mItem.getId()));
            holder.mContentView.setText(holder.mItem.getName());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putLong(MerchantDetailFragment.ARG_ITEM_ID, holder.mItem.getId());
                        MerchantDetailFragment fragment = new MerchantDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.merchant_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MerchantDetailActivity.class);
                        intent.putExtra(MerchantDetailFragment.ARG_ITEM_ID, holder.mItem.getId());

                        context.startActivity(intent);
                    }
                }
            });}

            public void swapCursor(Cursor newCursor){
                mValues = newCursor;
                notifyDataSetChanged();
            }

        @Override
        public int getItemCount() {
            if (mValues == null)
                return -1;
            return mValues.getCount();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public Merchant mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
