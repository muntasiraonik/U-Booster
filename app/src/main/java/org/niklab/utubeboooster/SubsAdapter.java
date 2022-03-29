package org.niklab.utubeboooster;

import androidx.annotation.Keep;

@Keep
public class SubsAdapter {
    public String name,value,image;

    public SubsAdapter(){

    }

    public SubsAdapter(String name, String value, String image) {
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
