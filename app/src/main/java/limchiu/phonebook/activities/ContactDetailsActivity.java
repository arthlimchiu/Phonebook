package limchiu.phonebook.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;

import limchiu.phonebook.R;
import limchiu.phonebook.database.ContactTable;
import limchiu.phonebook.provider.ContactContentProvider;

public class ContactDetailsActivity extends AppCompatActivity {

    private int EDIT_CONTACT = 1;

    private long mId;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private TextView mPhoneView;
    private TextView mNameView;
    private ImageView mImageView;

    private String mName;
    private String mPhone;
    private String mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        Intent intent = getIntent();
        mId = intent.getLongExtra("id", -1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mPhoneView = (TextView) findViewById(R.id.contact_details_phone);
        mNameView = (TextView) findViewById(R.id.contact_details_name);
        mImageView = (ImageView) findViewById(R.id.contact_details_image);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_call);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + mPhone));
                startActivity(intent);
            }
        });

        populateDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit:
                Intent intent = new Intent(this, ContactAddActivity.class);
                Bundle b = new Bundle();
                b.putLong("id", mId);
                b.putString("name", mName);
                b.putString("phone", mPhone);
                b.putString("image", mImage);
                intent.putExtras(b);
                startActivityForResult(intent, EDIT_CONTACT);
                return true;
            case R.id.delete:
                getContentResolver().delete(Uri.withAppendedPath(ContactContentProvider.CONTENT_URI_CONTACTS, String.valueOf(mId)), null, null);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            populateDetails();
        }
    }

    private void populateDetails() {
        String[] projection = new String[]{ContactTable.COLUMN_IMAGE, ContactTable.COLUMN_NAME, ContactTable.COLUMN_PHONE};
        Cursor cursor = getContentResolver().query(Uri.withAppendedPath(ContactContentProvider.CONTENT_URI_CONTACTS, String.valueOf(mId)), projection, null, null, null);

        cursor.moveToNext();

        mImage = cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_IMAGE));
        mName = cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_NAME));
        mPhone = cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_PHONE));

        cursor.close();

        if (TextUtils.isEmpty(mImage)) {
            Glide.with(this).load(R.mipmap.ic_launcher).into(mImageView);
        } else {
            Glide.with(this).load(Uri.parse(mImage)).into(mImageView);
        }

        mCollapsingToolbarLayout.setTitle(mName);
        mPhoneView.setText(mPhone);
        mNameView.setText(mName);
    }
}
