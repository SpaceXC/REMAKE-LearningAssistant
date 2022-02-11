package com.bakamcu.remake.learningassistant;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProblemList extends Fragment {

    private ProblemsListViewModel mViewModel;

    public static ProblemList newInstance() {
        return new ProblemList();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.problem_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProblemsListViewModel.class);
        FloatingActionButton addBtn = getView().findViewById(R.id.addProblem);

        addBtn.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), AddProblem.class);
            startActivity(intent);
        });
    }
}