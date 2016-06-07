package ua.com.sezone.full_screen;

import java.io.Serializable;

/**
 * Created by ggm on 18.12.15.
 */
public class Movie implements Serializable{
    private String name;
    private String  url;

    public Movie(String name,String  url) {
        this.name = name;
        this.url = url;
    }

    public Movie() {
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "name = "+name+ "\t"+"url = "+url+"\n";
    }

}


