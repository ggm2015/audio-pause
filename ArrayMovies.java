package ua.com.sezone.full_screen;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ggm on 18.12.15.
 */
public class ArrayMovies implements Serializable {

    private String nameMovie;
    private String urlMovie;
    private ArrayList<Movie> movieArrayList;

    public ArrayList<Movie> getMovieArrayList() {
        return movieArrayList;
    }

    public void setMovieArrayList(ArrayList<Movie> movieArrayList) {
        this.movieArrayList = movieArrayList;
    }



    public ArrayMovies() {
        this.movieArrayList = new ArrayList<>();
    }

    public void add(String name, String url) {
        Movie movie = new Movie(name, url);
        movieArrayList.add(movie);
    }

    public void add(Movie movie) {
        movieArrayList.add(movie);
    }

    public void remove(int id) {
        movieArrayList.remove(id);
    }

    public void getName(int position){
        nameMovie = movieArrayList.get(position).getName();
    }

    public void getUrl(int position){
        urlMovie = movieArrayList.get(position).getUrl();
    }


    public void fill(){
/*        Movie movie0 = new Movie();
        movie0.setName("Neolux");
        movie0.setUrl(Uri.parse("android.resource://ua.com.sezone.full_screen/raw/neolux"));
        movieArrayList.add(movie0);
 */   }

}
