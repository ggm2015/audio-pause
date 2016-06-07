package ua.com.sezone.full_screen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ggm on 18.12.15.
 */
public class BoxAdapter extends BaseAdapter {


    Bitmap bitmap;
    Movie movie;
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Movie> movieArrayList;
    MediaMetadataRetriever retriever;

    public BoxAdapter(Context context, ArrayList<Movie> movieArrayList) {
        this.context = context;
        this.movieArrayList = movieArrayList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }

    @Override
    public int getCount() {
        return movieArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return movieArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        bitmap = null;
        ViewHolder holder;
        View view = convertView;
        String nameMovie = "";
        retriever = new MediaMetadataRetriever();

        //attach java variable at the xml variable
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item, null, true);
            holder = new ViewHolder();
            holder.textView = (TextView) view.findViewById(R.id.tvNameMovie);
            holder.imageView = (ImageView) view.findViewById(R.id.ivImage);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }// end of attach java variable at the xml variable


        //get image from box adapter
        try {
            movie = (Movie) getItem(position);
           /* if (movie == null) {
                Log.d("MyLog", "null");
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.boxadapter_image_);
                nameMovie = context.getString(R.string.add_name_movie);
            } else {*/
            //  Log.d("MyLog", "No null");
            nameMovie = movie.getName();
            String uriVideo = movie.getUrl();
            //   Log.d("MyLog","video="+uriVideo);
            try {
                retriever.setDataSource(context, Uri.parse(uriVideo));
                bitmap = retriever.getFrameAtTime(3000000);
                //      Log.d("MyLog","bitmap");
            } catch (IllegalArgumentException e) {
                //    Log.d("MyLog","e.printStackTrace()");
                e.printStackTrace();
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.boxadapter_image);
            } catch (SecurityException e) {
                e.printStackTrace();
                //   Log.d("MyLog","SecurityException");
            }
        } catch (NullPointerException e) {
            //  Log.d("MyLog", " find NullPointerException");
        }// end getting image from boxadapter_image_ file


        //set data at java variable
        holder.textView.setText(nameMovie);
        holder.imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 178, 100, false));
        //end set data at java variable

        return view;
    }
}
