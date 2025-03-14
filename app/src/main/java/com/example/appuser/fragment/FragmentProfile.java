package com.example.appuser.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.appuser.R;
import com.example.appuser.activity.EditUserActivity;
import com.example.appuser.activity.OrderDetailsActivity;
import com.example.appuser.activity.StartActivity2;
import com.example.appuser.activity.UpdatePasswordActivity;
import com.example.appuser.utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.paperdb.Paper;

public class FragmentProfile extends Fragment implements View.OnClickListener {
    ImageView avatar, settings;
    CardView order, edituser, logout, changePass;
    TextView nameCustomer;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        avatar = view.findViewById(R.id.avatar);
        order = view.findViewById(R.id.order);
        edituser = view.findViewById(R.id.edituser);
        settings = view.findViewById(R.id.settings);
        logout = view.findViewById(R.id.logout);
        changePass = view.findViewById(R.id.changePass);
        nameCustomer = view.findViewById(R.id.nameCustomer);
        if (Paper.book().read("user_current") != null) {
            initView(view);
            initControl(view);
        } else {
            nameCustomer.setText("Customer");
            order.setVisibility(View.GONE);
            edituser.setVisibility(View.GONE);
            logout.setVisibility(View.GONE);
            changePass.setVisibility(View.GONE);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("702415756462-o2ol1s5qoj9470q23676cgekbce9df2n.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
    }

    private void initControl(View view) {
        avatar.setImageDrawable(getResources().getDrawable(R.drawable.avatar_person));
        order.setOnClickListener(this);
        edituser.setOnClickListener(this);
        settings.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    private void initView(View view) {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            nameCustomer.setText(user.getDisplayName());
            Glide.with(view.getContext()).load(user.getPhotoUrl()).into(avatar);
        }
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
        } else if (id == R.id.changePass) {
            Intent changePass = new Intent(v.getContext(), UpdatePasswordActivity.class);
            startActivity(changePass);
        } else if (id == R.id.logout) {
            Utils.user = null;
            Paper.book().delete("user_current");
            FirebaseAuth.getInstance().signOut();

            if (googleSignInClient != null) {
                googleSignInClient.signOut();
            }

            Intent logout = new Intent(getActivity(), StartActivity2.class);
            startActivity(logout);
            requireActivity().finish();
        }

    }
}
