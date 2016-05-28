package com.fintech.timpla.mojabanka;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.orm.SugarContext;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

public class DbProvider extends ContentProvider{

    private static final int MERCHANT = 100;
    private static final int MERCHANT_WITH_ID = 101;

    private static final String AUTHORITY = "com.fintech.timpla.mojabanka.provider";
    public static final String PATH_MERCHANT = "/merchant";
    private static final Uri BASE_MERCHANT_URI = Uri.parse(AUTHORITY + PATH_MERCHANT);
    public static final Uri MERCHANT_CONTENT_URI = Uri.parse("content://" + BASE_MERCHANT_URI);

    private static final String CONTENT_TYPE_MERCHANT =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_MERCHANT;
    private static final String CONTENT_ITEM_TYPE_MERCHANT =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_MERCHANT;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(AUTHORITY, PATH_MERCHANT, MERCHANT);
        matcher.addURI(AUTHORITY, PATH_MERCHANT + "/#", MERCHANT_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        return SugarContext.getSugarContext() != null;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor = null;
        int match = sUriMatcher.match(uri);

        switch (match) {

            case MERCHANT_WITH_ID: {
                retCursor = SugarRecord.getCursor(
                        Merchant.class, "id = ?",
                        new String[]{uri.getPathSegments().get(1)}, null, null, null);
                break;
            }

            case MERCHANT: {
                retCursor = SugarRecord.getCursor(Merchant.class, null, null, null, null, null);
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match){
            case MERCHANT_WITH_ID:{
                return CONTENT_ITEM_TYPE_MERCHANT;
            }
            case MERCHANT: {
                return CONTENT_TYPE_MERCHANT;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = sUriMatcher.match(uri);

        long id;
        switch (match){
            case MERCHANT: {

            }
            case MERCHANT_WITH_ID: {
                Merchant item = new Merchant(values);
                id = SugarRecord.save(item);
                break;
            }

            default: {
                throw new UnsupportedOperationException("Error inserting into " + uri);
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);

        int itemsDeleted;
        switch (match) {

            case MERCHANT: {
                itemsDeleted = SugarRecord.deleteAll(Merchant.class, selection, selectionArgs);
                break;
            }

            default: {
                throw new UnsupportedOperationException("Error deleting: " + uri);
            }
        }

        if (itemsDeleted > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return itemsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        int itemsUpdated = 0;

        switch (match) {
            case MERCHANT: {
                List<Merchant> list = SugarRecord.find(Merchant.class, selection, selectionArgs);
                for (Merchant item: list) {
                    if (item != null){
                        Merchant newItem = new Merchant(values);
                        newItem.setId(item.getId());
                        newItem.save();
                        itemsUpdated++;
                    }
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Error updating: " + uri);
            }
        }
        if (itemsUpdated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return itemsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int match = sUriMatcher.match(uri);
        int itemsInserted = 0;

        switch (match){
            case MERCHANT:{
                ArrayList<Merchant> list = new ArrayList<>() ;
                for (ContentValues value:values) {
                    list.add(new Merchant(value));
                }
                list.trimToSize();
                itemsInserted = list.size();
                SugarRecord.saveInTx(list);
                break;
            }

            default: throw new UnsupportedOperationException("Error bulk inserting into " + uri);
        }

        if (itemsInserted > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return itemsInserted;
    }
}
