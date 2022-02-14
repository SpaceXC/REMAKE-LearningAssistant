package com.bakamcu.remake.learningassistant;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.bakamcu.remake.learningassistant.databinding.ActivityAddProblemBinding;


public class AddProblem extends AppCompatActivity {
    final static String TAG = "TAG";    //日志的标签

    ActivityAddProblemBinding binding;  //DataBinding视图

    String problemImagePath = "";
    String wrongAnswerImagePath = "";
    String correctAnswerImagePath = "";

    ProblemsListViewModel viewModel;    //错题列表Viewmodel

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_problem);  //DataBinding加载视图
        viewModel = new ViewModelProvider(this).get(ProblemsListViewModel.class);   //获取Viewmodel

        //----------------------------UI交互监听设置区域----------------------------
        binding.ratingBar2.setOnRatingBarChangeListener((ratingBar, v, b) -> {
            binding.ratingPercent.setText(v * 20 + "%");    //改变textview里的掌握百分比
        });

        binding.submit.setOnClickListener( view -> {
            RadioButton subjectButton = findViewById(binding.subjects.getCheckedRadioButtonId());
            if(subjectButton == null || TextUtils.isEmpty(binding.problemSrc.getText().toString())){
                Toast.makeText(AddProblem.this, "请填写带星号的信息！", Toast.LENGTH_SHORT).show();
                return;
            }
            String subject = subjectButton.getText().toString();
            Problem problem = new Problem(subject,
                    binding.problemSrc.getText().toString().trim(),
                    binding.problem.getText().toString().trim(),
                    binding.wrongAnswer.getText().toString().trim(),
                    binding.correctAnswer.getText().toString().trim(),
                    problemImagePath,
                    wrongAnswerImagePath,
                    correctAnswerImagePath,
                    binding.reason.getText().toString().trim(),
                    System.currentTimeMillis(),
                    false,
                    binding.ratingBar2.getRating());
            AddProblemToDB(problem);
        });
        //---------------------------UI交互监听设置区域结束---------------------------
    }

    void AddProblemToDB(Problem problem){
        viewModel.insertProbs(problem);
        finish();
    }
}