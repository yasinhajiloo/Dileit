package com.example.dileit.view.adapter.viewpager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.dileit.view.fragment.wordinfo.leitnersetup.TranslationDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class AddNewLeitnerViewPagerAdapter extends FragmentPagerAdapter {
    List<String> titles = new ArrayList<>();
    List<Fragment> mFragments = new ArrayList<>();

    public AddNewLeitnerViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (titles.size() > 0)
            return titles.get(position);
        else return null;
    }

    public void addData(String title, Fragment fragment) {
        mFragments.add(fragment);
        titles.add(title);
        notifyDataSetChanged();
    }

}
