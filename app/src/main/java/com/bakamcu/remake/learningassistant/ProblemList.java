package com.bakamcu.remake.learningassistant;

import static com.bakamcu.remake.learningassistant.AddProblem.TAG;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import cn.leancloud.LCUser;


public class ProblemList extends Fragment {

    ProblemListAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    LiveData<List<Problem>> problemList;
    RecyclerView recyclerView;
    private ProblemsListViewModel viewModel;
    private List<Problem> allProblems;
    private boolean undoAction;

    public ProblemList() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.problem_list_action_bar, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setMaxWidth(1000);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String pattern = newText.trim();
                problemList.removeObservers(getViewLifecycleOwner());      //先移除之前在onActivityCreated中添加的observer
                problemList = viewModel.findProblemWithPattern(pattern);
                problemList.observe(getViewLifecycleOwner(), problems -> {
                    int temp = adapter.getItemCount();
                    allProblems = problems;
                    if (temp != problems.size()) {
                        adapter.submitList(problems);
                    }
                });
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearData:
                if (adapter.getItemCount() == 0) {
                    Toast.makeText(getContext(), "列表中没有数据", Toast.LENGTH_SHORT).show();
                    return false;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("清空数据");
                builder.setMessage("您确定要清空全部吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.deleteAllProbs();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create();
                builder.show();
                break;
            case R.id.logout:
                AlertDialog.Builder logoutDialogBuilder = new AlertDialog.Builder(requireActivity());
                logoutDialogBuilder.setTitle("登出账号");
                logoutDialogBuilder.setMessage("您确定要登出账号吗？");
                logoutDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        LCUser.logOut();
                        requireActivity().finish();
                    }
                });
                logoutDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                logoutDialogBuilder.create();
                logoutDialogBuilder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
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
        FloatingActionButton addBtn = requireView().findViewById(R.id.addProblem);
        recyclerView = requireView().findViewById(R.id.recyclerView);
        adapter = new ProblemListAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        problemList = viewModel.getAllProblemsLive();
        problemList.observe(requireActivity(), problems -> {
            int temp = adapter.getItemCount();
            allProblems = problems;
            if (temp != problems.size()) {
                if (temp < problems.size() && !undoAction) {
                    recyclerView.smoothScrollToPosition(0);
                }
                undoAction = false;
                adapter.submitList(problems);
            }
        });

        addBtn.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), AddProblem.class);
            startActivity(intent);
        });


        Log.d(TAG, "onViewCreated: " + viewModel.getAllProblemsLive().getValue());

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START | ItemTouchHelper.END) {
            final Drawable icon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_delete_24);
            final Drawable background = new ColorDrawable(Color.LTGRAY);

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final Problem problemToDelete = allProblems.get(viewHolder.getAdapterPosition());
                viewModel.deleteProbs(problemToDelete);
                Snackbar.make(requireActivity().findViewById(R.id.constraintLayout), "您删除了：" + problemToDelete.getProblemSource(), Snackbar.LENGTH_LONG)
                        .setAction("撤销", v -> {
                            undoAction = true;
                            viewModel.insertProbs(problemToDelete);
                        })
                        .show();

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                assert icon != null;
                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;

                int iconLeft, iconRight, iconTop, iconBottom;
                int backTop, backBottom, backLeft, backRight;
                backTop = itemView.getTop();
                backBottom = itemView.getBottom();
                iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                iconBottom = iconTop + icon.getIntrinsicHeight();
                if (dX > 0) {
                    backLeft = itemView.getLeft();
                    backRight = itemView.getLeft() + (int) dX;
                    background.setBounds(backLeft, backTop, backRight, backBottom);
                    iconLeft = itemView.getLeft() + iconMargin;
                    iconRight = iconLeft + icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                } else if (dX < 0) {
                    backRight = itemView.getRight();
                    backLeft = itemView.getRight() + (int) dX;
                    background.setBounds(backLeft, backTop, backRight, backBottom);
                    iconRight = itemView.getRight() - iconMargin;
                    iconLeft = iconRight - icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                } else {
                    background.setBounds(0, 0, 0, 0);
                    icon.setBounds(0, 0, 0, 0);
                }
                background.draw(c);
                icon.draw(c);
            }

        }).attachToRecyclerView(recyclerView);

        swipeRefreshLayout = requireView().findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::RefreshList);
    }


    public void RefreshList() {
        Log.d(TAG, "RefreshList: ");
        adapter.submitList(viewModel.getAllProblemsLive().getValue());
        recyclerView.scrollToPosition(0);
        swipeRefreshLayout.setRefreshing(false);
    }
}