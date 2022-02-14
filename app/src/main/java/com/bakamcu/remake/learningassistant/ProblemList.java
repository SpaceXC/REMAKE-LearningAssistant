package com.bakamcu.remake.learningassistant;

import static com.bakamcu.remake.learningassistant.AddProblem.TAG;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.util.List;


public class ProblemList extends Fragment {

    private ProblemsListViewModel viewModel;
    ProblemListAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    LiveData<List<Problem>> problemList;
    TextInputEditText search;
    RecyclerView recyclerView;

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
        viewModel = new ViewModelProvider(this).get(ProblemsListViewModel.class);
        FloatingActionButton addBtn = getView().findViewById(R.id.addProblem);
        recyclerView = getView().findViewById(R.id.recyclerView);
        search = getView().findViewById(R.id.search);
        adapter = new ProblemListAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        problemList = viewModel.getAllProblemsLive();
        problemList.observe(getViewLifecycleOwner(), problems -> {
            adapter.submitList(problems);
            recyclerView.smoothScrollToPosition(0);
        });

        addBtn.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), AddProblem.class);
            startActivity(intent);
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                problemList = viewModel.findProblemWithPattern(charSequence.toString());
                problemList.observe(getViewLifecycleOwner(), new Observer<List<Problem>>() {
                    @Override
                    public void onChanged(List<Problem> problems) {
                        adapter.submitList(problems);

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Log.d(TAG, "onViewCreated: " + viewModel.getAllProblemsLive().getValue());



        swipeRefreshLayout = getView().findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshList();
            }
        });
    }

    public void RefreshList(){
        Log.d(TAG, "RefreshList: ");
        adapter.submitList(viewModel.getAllProblemsLive().getValue());
        recyclerView.scrollToPosition(0);
        swipeRefreshLayout.setRefreshing(false);
    }
}