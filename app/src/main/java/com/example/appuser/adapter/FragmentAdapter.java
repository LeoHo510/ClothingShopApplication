package com.example.appuser.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.appuser.fragment.FragmentBag;
import com.example.appuser.fragment.FragmentFavorite;
import com.example.appuser.fragment.FragmentHome;
import com.example.appuser.fragment.FragmentProfile;
import com.example.appuser.fragment.FragmentShop;

public class FragmentAdapter extends FragmentStatePagerAdapter {
    public FragmentAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return new FragmentHome();
            case 1: return new FragmentShop();
            case 2: return new FragmentFavorite();
            case 3: return new FragmentBag();
            case 4: return new FragmentProfile();
            default: return new FragmentShop();
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
