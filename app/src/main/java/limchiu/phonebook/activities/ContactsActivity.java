package limchiu.phonebook.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;

import limchiu.phonebook.R;
import limchiu.phonebook.adapters.ContactsAdapter;
import limchiu.phonebook.database.ContactTable;
import limchiu.phonebook.provider.ContactContentProvider;


public class ContactsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private int CONTACTS_LOADER = 1;

    private ListView mContactList;
    private ContactsAdapter mAdapter;
    private CursorLoader mCursorLoader;

    private String selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContactList = (ListView) findViewById(R.id.contacts_list);

        mContactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(ContactsActivity.this, ContactDetailsActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fab.show(true);
            }
        }, 300);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ContactsActivity.this, ContactAddActivity.class));
            }
        });

        getSupportLoaderManager().initLoader(CONTACTS_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_contacts, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                displayResults(newText);
                Log.i("ContactsActivity", newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{ContactTable.COLUMN_ID,
                ContactTable.COLUMN_NAME,
                ContactTable.COLUMN_PHONE,
                ContactTable.COLUMN_IMAGE};
        mCursorLoader = new CursorLoader(this, ContactContentProvider.CONTENT_URI_CONTACTS, projection, selection, null, "name asc");

        return mCursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter = new ContactsAdapter(this, data);
        mContactList.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    private void displayResults(String text) {
        selection = ContactTable.COLUMN_NAME + " LIKE '%" + text + "%'";
        getSupportLoaderManager().restartLoader(CONTACTS_LOADER, null, this);
    }
}
