package com.example.appuser.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.appuser.R;
import com.example.appuser.activity.EditUserActivity;
import com.example.appuser.activity.OrderDetailsActivity;
import com.example.appuser.activity.StartActivity2;
import com.example.appuser.retrofit.ApiClothing;
import com.example.appuser.retrofit.RetrofitClient;
import com.example.appuser.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import io.paperdb.Paper;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class FragmentProfile extends Fragment implements View.OnClickListener {
    ImageView avatar, order, edituser, settings, logout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initControl(view);
    }

    private void initControl(View view) {
        Glide.with(view.getContext())
                .load("https://static.vecteezy.com/system/resources/previews/019/896/008/original/male-user-avatar-icon-in-flat-design-style-person-signs-illustration-png.png")
                .apply(RequestOptions.circleCropTransform())
                .into(avatar);
        order.setOnClickListener(this);
        edituser.setOnClickListener(this);
        settings.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    private void initView(View view) {
        avatar = view.findViewById(R.id.avatar);
        order = view.findViewById(R.id.order);
        edituser = view.findViewById(R.id.edituser);
        settings = view.findViewById(R.id.settings);
        logout = view.findViewById(R.id.logout);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.order) {
            Intent order = new Intent(v.getContext(), OrderDetailsActivity.class);
            startActivity(order);
        } else if (id == R.id.edituser) {
            Intent editUser = new Intent(v.getContext(), EditUserActivity.class);
            startActivity(editUser);
        } else if (id == R.id.settings) {
            // Handle settings click
        } else if (id == R.id.logout) {
            Paper.book().delete("user_current");
            FirebaseAuth.getInstance().signOut();

            Intent logout = new Intent(getActivity(), StartActivity2.class);
            startActivity(logout);

            requireActivity().finish();
        }
    }
}
