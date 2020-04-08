package com.example.dileit.view.adapter.viewpager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.dileit.R;

import java.util.List;

public class LeitnerItemTranslationViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private List<String> mStrings;

    public LeitnerItemTranslationViewPagerAdapter(Context context, List<String> strings) {
        mContext = context;
        mStrings = strings;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mStrings.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_leitner_review_translation , container , false);
        TextView textView = view.findViewById(R.id.tv_item_view_pager_review_translation);
        textView.setText(mStrings.get(position));
        container.addView(view);
        return view;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0 :
                return "Translation";
            case 1:
                return "Second Translation";
            default:
                return "Translation";
        }
    }
}
