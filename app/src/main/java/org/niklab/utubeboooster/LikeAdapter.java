package org.niklab.utubeboooster;


import androidx.annotation.Keep;

@Keep
public class LikeAdapter {

    public String name,value,image;


    public LikeAdapter(){

    }

    public LikeAdapter(String name, String value, String image) {
        this.name = name;
        this.value = value;
        this.image = image;
    }


    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getImage() {
        return image;
    }
}
