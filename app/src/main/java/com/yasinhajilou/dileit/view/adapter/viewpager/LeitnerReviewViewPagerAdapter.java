package com.yasinhajilou.dileit.view.adapter.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class LeitnerReviewViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments = new ArrayList<>();

    public LeitnerReviewViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }


    public void addData(List<Fragment> fragments){
        mFragments.addAll(fragments);
        notifyDataSetChanged();
    }

    public void addReportView(Fragment fragment){
        mFragments.add(fragment);
        notifyDataSetChanged();
    }

    public void removeData(Fragment fragment){
        mFragments.remove(fragment);
        notifyDataSetChanged();
    }
}
