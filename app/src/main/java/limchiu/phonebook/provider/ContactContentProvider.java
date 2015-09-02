package limchiu.phonebook.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import limchiu.phonebook.database.ContactDatabaseHelper;
import limchiu.phonebook.database.ContactTable;

/**
 * Created by Clarence on 8/6/2015.
 */
public class ContactContentProvider extends ContentProvider {

    private ContactDatabaseHelper database;

    // Used for the UriMatcher
    private static final int CONTACTS = 1;
    private static final int CONTACT_ID = 2;

    private static final String AUTHORITY = "limchiu.phonebook.provider";
    private static final String PATH_CONTACTS = "contacts";

    public static final Uri CONTENT_URI_CONTACTS = Uri.parse("content://" + AUTHORITY + "/" + PATH_CONTACTS);

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, PATH_CONTACTS, CONTACTS);
        sUriMatcher.addURI(AUTHORITY, PATH_CONTACTS + "/#", CONTACT_ID);
    }

    @Override
    public boolean onCreate() {
        database = new ContactDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        int uriType = sUriMatcher.match(uri);
        switch (uriType) {
            case CONTACTS:
                builder.setTables(ContactTable.TABLE_CONTACT);
                break;
            case CONTACT_ID:
                // adding the ID to the original query
                builder.setTables(ContactTable.TABLE_CONTACT);
                builder.appendWhere(ContactTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        // Make sure that potential listeners  are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();

        long id;
        String path;

        switch (uriType) {
            case CONTACTS:
                id = db.insert(ContactTable.TABLE_CONTACT, null, contentValues);
                path = PATH_CONTACTS;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse(path + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        int rowsDeleted;

        String id;

        switch (uriType) {
            case CONTACTS:
                rowsDeleted = db.delete(ContactTable.TABLE_CONTACT, selection, selectionArgs);
                break;
            case CONTACT_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(ContactTable.TABLE_CONTACT, ContactTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(ContactTable.TABLE_CONTACT, ContactTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        int rowsUpdated;

        String id;

        switch (uriType) {
            case CONTACTS:
                rowsUpdated = db.update(ContactTable.TABLE_CONTACT, contentValues, selection, selectionArgs);
                break;
            case CONTACT_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(ContactTable.TABLE_CONTACT, contentValues, ContactTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsUpdated = db.update(ContactTable.TABLE_CONTACT, contentValues, ContactTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }
}
