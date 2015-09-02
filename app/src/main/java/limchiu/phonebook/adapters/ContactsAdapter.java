package limchiu.phonebook.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;

import limchiu.phonebook.R;
import limchiu.phonebook.database.ContactTable;

/**
 * Created by Clarence on 8/6/2015.
 */
public class ContactsAdapter extends CursorAdapter {


    public ContactsAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);

        ViewHolder holder = new ViewHolder();
        holder.mImage = (ImageView) v.findViewById(R.id.item_contact_image);
        holder.mName = (TextView) v.findViewById(R.id.item_contact_name);
        holder.mNumber = (TextView) v.findViewById(R.id.item_contact_number);

        v.setTag(holder);

        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        String image = cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_IMAGE));
        String name = cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_NAME));
        String number = cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_PHONE));

        if (TextUtils.isEmpty(image)) {
            holder.mImage.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(context).load(Uri.parse(image)).centerCrop().into(holder.mImage);
        }

        holder.mName.setText(name);
        holder.mNumber.setText(number);
    }

    class ViewHolder {
        ImageView mImage;
        TextView mName;
        TextView mNumber;
    }
}
