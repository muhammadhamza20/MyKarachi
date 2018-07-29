package com.example.muhammadhamza.mykarachi;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CustomisedFragmentPageAdapter extends FragmentPagerAdapter {

    private Context context;
    public CustomisedFragmentPageAdapter(FragmentManager fm, Context mContext) { super(fm); context = mContext; }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FeedsFragment();
        } else if (position == 1){
            return new UserExperincedUpdatesFragment();
        } else {
            return new MapFragment();
        }    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return context.getResources().getString(R.string.feeds_heading);
        } else if (position == 1){
            return context.getResources().getString(R.string.user_experinced_heading);
        } else{
            return context.getResources().getString(R.string.map_heading);
        }
    }
}
