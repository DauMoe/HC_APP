package com.example.hc_app.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.hc_app.Fragments.ExerciseFragment;
import com.example.hc_app.Fragments.HistoryFragment;
import com.example.hc_app.Fragments.StepFragment;
import com.example.hc_app.Fragments.ProfileFragment;

public class FragmentAdapter extends FragmentStatePagerAdapter {
    private int numofPager = 4;
    public FragmentAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new StepFragment();
            case 1:
                return new HistoryFragment();
            case 2:
                return new ExerciseFragment();
            case 3:
                return new ProfileFragment();
            default:
                return new StepFragment();
        }
    }

    @Override
    public int getCount() {
        return numofPager;
    }
}
