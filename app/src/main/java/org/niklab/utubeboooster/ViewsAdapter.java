package org.niklab.utubeboooster;

import androidx.annotation.Keep;

/**
 * Created by Muntasir Aonik on 3/3/2019.
 */

@Keep
public class ViewsAdapter {
    public String name,value,image;



    public ViewsAdapter(){

    }


    public ViewsAdapter(String name, String value, String image) {
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

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
