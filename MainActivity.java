package ua.com.sezone.full_screen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    boolean EDITED_LIST = false;
    static final String SAVED_TEXT = "saved_text";
    static final String STRINGS_LIST = "saved_list";

    ArrayList<Movie> list;
    AudioManager audioManager;
    ListView listView;
    BoxAdapter boxAdapter;
    Button mEdit;
    FloatingActionButton fab;
    Type type;
    SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        list = readArrayMovie();
        if ((ArrayList<Movie>) getIntent().getSerializableExtra(STRINGS_LIST) != null) {
            list = (ArrayList<Movie>) getIntent().getSerializableExtra(STRINGS_LIST);
        } else {
            list = readArrayMovie();
        }


        //button RESET
        mEdit = (Button) findViewById(R.id.main_button_edit);
        mEdit.setVisibility(View.INVISIBLE);
        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEdit.setVisibility(View.INVISIBLE);
                fab.hide();
                EDITED_LIST = false;
            }
        });//end button RESET

        fab = (FloatingActionButton) findViewById(R.id.setting_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EDITED_LIST) {
                    fab.show();
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("video/*");
                    startActivityForResult(Intent.createChooser(intent,
                            getResources().getText(R.string.chooser_video)), 3);
                } else {
                    fab.hide();
                    Snackbar.make(view, R.string.add_button, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });//end FloatingActionButton


        listView = (ListView) findViewById(R.id.listView);
        boxAdapter = new BoxAdapter(this, list);
        listView.setAdapter(boxAdapter);

        //Start videoPlayer
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, Player.class);
                Movie movie = list.get(position);
                String urlMovie = movie.getUrl();
                i.putExtra("urlMovie", "" + urlMovie);
                audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                //TODO сделать ползунок для предустановки громкости звука
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15, 15);
                startActivity(i);
            }
        });// end start videoPlayer

        // DELETE Movie
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (EDITED_LIST) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.attention)
                            .setMessage(R.string.deleteVideo)
                            .setIcon(R.drawable.ic_menu_delete)
                            .setCancelable(false)
                            .setNegativeButton(R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            list.remove(position);
                                            saveArray(list);
                                            boxAdapter.notifyDataSetChanged();
                                        }
                                    });
                    builder.setPositiveButton(R.string.back,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    // Возвращает "истину", чтобы завершить событие клика, чтобы
                    // onListItemClick больше не вызывался
                } else {
                    return true;
                }
                return true;
            }

        });//end DELETE Movie

    } //end of method onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        TextView title = new TextView(this);
        title.setText(R.string.attention);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
// title.setTextColor(getResources().getColor(R.color.greenBG));
        title.setTextSize(23);

        TextView msg = new TextView(this);
        msg.setText(R.string.editVideo);
        msg.setPadding(10, 10, 10, 10);
        msg.setGravity(Gravity.CENTER);
        msg.setTextSize(18);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            //  builder.setTitle(R.string.attention)
            builder.setCustomTitle(title);
            builder.setView(msg)
                    // .setMessage(R.string.editVideo)
                    .setIcon(R.drawable.ic_stat_name)
                    .setCancelable(false)
                    .setNegativeButton(R.string.yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    //TODO create image "EDITE MODE"
                                    EDITED_LIST = true;
                                    fab.show();
                                    mEdit.setVisibility(View.VISIBLE);
                                }
                            });
            builder.setPositiveButton(R.string.no,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            fab.hide();
                            EDITED_LIST = false;
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }// end onOptionsItemSelected

    //ADD Movie
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                if (null == data) return;

                String selectedVideoPath;
                Uri selectedVideoUri = data.getData();
                selectedVideoPath = VideoFilePath.getPath(getApplicationContext(), selectedVideoUri);

                //Extract name movie from path files
                int x = selectedVideoPath.indexOf('/');
                int y = selectedVideoPath.lastIndexOf('/') + 1;
                String nameMovie = selectedVideoPath.substring(0, x) + selectedVideoPath.substring(y);
                x = nameMovie.indexOf('.');
                y = nameMovie.length();
                nameMovie = nameMovie.substring(0, x) + nameMovie.substring(y);
                //end extract

                Movie movie = new Movie(nameMovie, selectedVideoPath);
                list.add(movie);
                saveArray(list);
                boxAdapter.notifyDataSetChanged();
            }
        }
    }//end ADD Movie

    //save ArrayList
    public void saveArray(ArrayList movieArrayList) {
        Gson gson = new Gson();
        type = new TypeToken<ArrayList<Movie>>() {
        }.getType();
        String json = gson.toJson(movieArrayList, type);

        sharedPref = this.getPreferences(this.MODE_PRIVATE);
        SharedPreferences.Editor editor = this.getPreferences(this.MODE_PRIVATE).edit();
        editor.putString(SAVED_TEXT, json);
      //  Log.d("MyLog", " saveArray " + arrayMovie.getMovieArrayList().toString());
        editor.commit();
    }//end save ArrayList

    //readArrayMovie
    public ArrayList readArrayMovie() {
        ArrayList<Movie> listRead = new ArrayList<>();
        String json;
        JSONArray jArr = null;
        sharedPref = getPreferences(this.MODE_PRIVATE);
        if ((json = sharedPref.getString(SAVED_TEXT, null)) != null) {

            try {
                jArr = new JSONArray(json);


                for (int i = 0; i < jArr.length(); i++) {
                    JSONObject jOb = jArr.getJSONObject(i);
                    Movie movie = new Movie();
                    movie.setName(jOb.getString("name"));
                    movie.setUrl(jOb.getString("url"));
                    listRead.add(movie);
                //    Log.d("MyLog", " readArrayMovie " + arrayMovie.getMovieArrayList().toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            ArrayMovies arrayMovie = new ArrayMovies();
            listRead = arrayMovie.getMovieArrayList();
        }
        //  Log.d(MY_LOG, listRead.toString());
        return listRead;
    }// end readArrayMovie

    //ACTIVITY Life cycle
    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveArray(list);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveArray(list);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        readArrayMovie();
    }

    @Override
    protected void onStart() {
        super.onStart();
        readArrayMovie();
    }

    @Override
    protected void onResume() {
        super.onResume();
        readArrayMovie();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveArray(list);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        readArrayMovie();
    }
    //end ACTIVITY Life cycle
}
