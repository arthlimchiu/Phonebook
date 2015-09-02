package limchiu.phonebook.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import limchiu.phonebook.R;
import limchiu.phonebook.database.ContactTable;
import limchiu.phonebook.provider.ContactContentProvider;

public class ContactAddActivity extends AppCompatActivity {

    private int PICK_IMAGE = 1;

    private long mId = -1;

    private EditText mName;
    private EditText mNumber;
    private TextInputLayout mNameLayout;
    private TextInputLayout mNumberLayout;
    private ImageView mImageView;

    private Uri mImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_add);

        mName = (EditText) findViewById(R.id.contact_add_name);
        mNumber = (EditText) findViewById(R.id.contact_add_number);
        mNameLayout = (TextInputLayout) findViewById(R.id.contact_add_name_layout);
        mNumberLayout = (TextInputLayout) findViewById(R.id.contact_add_number_layout);
        mImageView = (ImageView) findViewById(R.id.contact_add_image);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            mId = b.getLong("id", -1);
            mName.setText(b.getString("name"));
            mNumber.setText(b.getString("phone"));

            String image = b.getString("image");
            if (!TextUtils.isEmpty(image)) {
                mImage = Uri.parse(image);
                Glide.with(this).load(mImage).centerCrop().into(mImageView);
            }

            setTitle("Edit Contact");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.save:
                checkForErrors();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            mImage = data.getData();
            Glide.with(this).load(mImage).centerCrop().into(mImageView);
        }
    }

    private void checkForErrors() {
        String name = mName.getText().toString();
        String number = mNumber.getText().toString();

        if (TextUtils.isEmpty(name) || name.equals("")) {
            mNameLayout.setError("Please enter a name");
        } else if (TextUtils.isEmpty(number) || number.equals("")) {
            mNumberLayout.setError("Please enter a number");
        } else {
            if (mId == -1) {
                addContact(name, number);
            } else {
                editContact(name, number);
            }
        }
    }


    private void addContact(String name, String number) {
        ContentValues cv = new ContentValues();

        cv.put(ContactTable.COLUMN_NAME, name);
        cv.put(ContactTable.COLUMN_PHONE, number);

        if (mImage != null) {
            cv.put(ContactTable.COLUMN_IMAGE, mImage.toString());
        }

        getContentResolver().insert(ContactContentProvider.CONTENT_URI_CONTACTS, cv);

        finish();
    }

    private void editContact(String name, String number) {
        ContentValues cv = new ContentValues();

        cv.put(ContactTable.COLUMN_NAME, name);
        cv.put(ContactTable.COLUMN_PHONE, number);

        if (mImage != null) {
            cv.put(ContactTable.COLUMN_IMAGE, mImage.toString());
        }

        getContentResolver().update(Uri.withAppendedPath(ContactContentProvider.CONTENT_URI_CONTACTS, String.valueOf(mId)), cv, null, null);

        setResult(Activity.RESULT_OK);
        finish();
    }
}
