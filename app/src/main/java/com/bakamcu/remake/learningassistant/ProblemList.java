package com.bakamcu.remake.learningassistant;

import static com.bakamcu.remake.learningassistant.AddProblem.TAG;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.LCException;
import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import cn.leancloud.LCUser;
import cn.leancloud.types.LCNull;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class ProblemList extends Fragment {

    ProblemListAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    MutableLiveData<List<Problem>> problemList = new MutableLiveData<>();
    RecyclerView recyclerView;
    private ProblemsListViewModel viewModel;
    private List<Problem> allProblems = new ArrayList<>();
    private boolean undoAction;
    LinearLayoutManager recyclerViewManager;
    String queryName = "";
    Spinner spinnerQueryName;
    TextInputEditText searchText;
    String query = "";

    public ProblemList() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.problem_list_action_bar, menu);
    }

    @SuppressLint("NonConstantResourceId")
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
                        LCQuery<LCObject> lcquery = new LCQuery<>("Problems");
                        lcquery.whereEqualTo("user", LCUser.getCurrentUser());
                        lcquery.findInBackground().subscribe(new Observer<List<LCObject>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(List<LCObject> lcObjects) {
                                try {
                                    LCObject.deleteAll(lcObjects);
                                    RefreshList(queryName, query);
                                } catch (LCException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
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
        spinnerQueryName = requireView().findViewById(R.id.spinner);
        spinnerQueryName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (spinnerQueryName.getSelectedItem().toString()) {
                    case "错题来源":
                        queryName = "problemSource";
                        break;
                    case "科目名称":
                        queryName = "subject";
                        break;
                    case "入库时间":
                        queryName = "addTime";
                        break;
                    case "题目详情":
                        queryName = "problem";
                        break;
                    case "错误答案":
                        queryName = "wrongAnswer";
                        break;
                    case "正确答案":
                        queryName = "correctAnswer";
                        break;
                    case "错误原因":
                        queryName = "reason";
                        break;
                }
                Log.d(TAG, "onItemSelected: " + queryName);
                RefreshList(queryName, query);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        searchText = requireView().findViewById(R.id.searchText);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                query = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                RefreshList(queryName, query);
            }
        });
        adapter = new ProblemListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerViewManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(recyclerViewManager);

        swipeRefreshLayout = requireView().findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            RefreshList(queryName, query);
        });
        problemList.observe(requireActivity(), problems -> {
            int temp = adapter.getItemCount();
            allProblems = problemList.getValue();
            if (temp != problems.size()) {
                if (temp < problems.size() && !undoAction) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(0);
                        }
                    });
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
            //final Drawable background = new ColorDrawable(Color.LTGRAY);

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog alertDialog = LoadingDialog();
                alertDialog.show();
                final Problem problemToDelete = allProblems.get(viewHolder.getAdapterPosition());
                viewModel.deleteProbs(problemToDelete);
                //deleteProb(problemToDelete.problemID);
                LCObject problem = LCObject.createWithoutData("Problems", problemToDelete.problemID);
                problem.deleteInBackground().subscribe(new Observer<LCNull>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(LCNull response) {
                        RefreshList(queryName, query);
                        alertDialog.dismiss();
                        Snackbar.make(requireActivity().findViewById(R.id.constraintLayout), "您删除了：" + problemToDelete.getProblemSource(), Snackbar.LENGTH_LONG)
                                .setAction("撤销", v -> {
                                    undoAction = true;
                                    //viewModel.insertProbs(problemToDelete);
                                    alertDialog.show();
                                    LCObject problemLC = viewModel.BuildLeancloudObject(problemToDelete);
                                    problemLC.put("addTime", problemToDelete.updateTimeString);
                                    problemLC.put("addTimeStamp", problemToDelete.updateTimeStamp);
                                    problemLC.saveInBackground().subscribe(new Observer<LCObject>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onNext(LCObject lcObject) {
                                            alertDialog.dismiss();
                                            problemToDelete.setProblemID(lcObject.getObjectId());
                                            RefreshList(queryName, query);
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            alertDialog.dismiss();
                                            Toast.makeText(requireActivity(), "撤销失败！原因：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });

                                })
                                .show();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("failed to delete a todo: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });


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
                    //background.setBounds(backLeft, backTop, backRight, backBottom);
                    iconLeft = itemView.getLeft() + iconMargin;
                    iconRight = iconLeft + icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                } else if (dX < 0) {
                    backRight = itemView.getRight();
                    backLeft = itemView.getRight() + (int) dX;
                    //background.setBounds(backLeft, backTop, backRight, backBottom);
                    iconRight = itemView.getRight() - iconMargin;
                    iconLeft = iconRight - icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                } else {
                    //background.setBounds(0, 0, 0, 0);
                    icon.setBounds(0, 0, 0, 0);
                }
                //background.draw(c);
                icon.draw(c);
            }

        }).attachToRecyclerView(recyclerView);


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ProblemList");
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        RefreshList(queryName, query);
    }

    public void RefreshList(String queryName, String query) {
        viewModel.getAllProblems(queryName, query, problemList);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(0);
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public AlertDialog LoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View dialogView = View.inflate(requireActivity(), R.layout.loading_dialog, null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        return alertDialog;
    }
}