package org.niklab.utubeboooster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

/**
 * Created by Muntasir Aonik on 2/7/2019.
 */

public class MpagerAdapter extends PagerAdapter {

    private int [] layouts;
    private LayoutInflater layoutInflater;

    public MpagerAdapter(int [] layouts, Context context){

        this.layouts = layouts;
        Context context1 = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = layoutInflater.inflate(layouts[position],container,false);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object ;
        container.removeView(view);
    }
}
