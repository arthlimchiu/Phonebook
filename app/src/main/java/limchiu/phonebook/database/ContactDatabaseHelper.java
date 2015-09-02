package limchiu.phonebook.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Clarence on 8/6/2015.
 */
public class ContactDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contactsdb.db";
    private static final int DATABASE_VERSION = 1;

    public ContactDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // If you have a lot of tables, just call their onCreate methods here
        ContactTable.onCreate(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // If you have a lot of tables, just call their onUpgrade methods here

    }
}
