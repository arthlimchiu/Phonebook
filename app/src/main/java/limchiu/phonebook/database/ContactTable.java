package limchiu.phonebook.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Clarence on 8/6/2015.
 */
public class ContactTable {

    public static final String TABLE_CONTACT = "contacts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_IMAGE = "image";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_CONTACT
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_PHONE + " text, "
            + COLUMN_IMAGE + " text "
            + ")";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database) {
        // Put upgrade statements for this table here
    }
}
