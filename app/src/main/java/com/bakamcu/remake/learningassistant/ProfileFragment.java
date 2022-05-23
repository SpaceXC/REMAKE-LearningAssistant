package com.bakamcu.remake.learningassistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import cn.leancloud.LCUser;

public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView currentUser = requireView().findViewById(R.id.currentUser);
        Button logout = requireView().findViewById(R.id.button);
        currentUser.setText(LCUser.getCurrentUser().getUsername());
        logout.setOnClickListener(view1 -> {
            AlertDialog.Builder logoutDialogBuilder = new AlertDialog.Builder(requireActivity());
            logoutDialogBuilder.setTitle("登出账号");
            logoutDialogBuilder.setMessage("您确定要登出账号吗？");
            logoutDialogBuilder.setPositiveButton("确定", (dialog, which) -> {
                startActivity(new Intent(getContext(), LoginActivity.class));
                LCUser.logOut();
                requireActivity().finish();
            });
            logoutDialogBuilder.setNegativeButton("取消", (dialog, which) -> {

            });
            logoutDialogBuilder.create();
            logoutDialogBuilder.show();
        });
    }
}