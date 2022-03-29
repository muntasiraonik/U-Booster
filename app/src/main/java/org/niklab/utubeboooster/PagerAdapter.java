package org.niklab.utubeboooster;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ViewsFragment tab1 = new ViewsFragment();
                return tab1;
            case 1:
                SubsFragment tab2 = new SubsFragment();
                return tab2;

            case 2:
                LikesFragment tab3 = new LikesFragment();
                return  tab3;


            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
