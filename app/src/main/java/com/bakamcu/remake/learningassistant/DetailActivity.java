package com.bakamcu.remake.learningassistant;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bakamcu.remake.learningassistant.databinding.ActivityDetailBinding;
import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {
    Problem problem;
    ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_detail);
        binding = DataBindingUtil.setContentView(DetailActivity.this, R.layout.activity_detail);
        problem = (Problem) getIntent().getSerializableExtra("problem");

        binding.probSourceText.setText(problem.problemSource);
        binding.subject.setText(problem.subject);
        binding.problemText.setText(problem.problem);
        binding.wrongAnwerText.setText(problem.wrongAnswer);
        binding.correctAnwerText.setText(problem.correctAnswer);
        binding.reasonText.setText(problem.reason);
        binding.ratingBar.setRating(Float.parseFloat(problem.probRate));
        if (!TextUtils.isEmpty(problem.problemImgPath)) {
            Glide.with(this)
                    .load(Uri.parse(problem.problemImgPath))
                    .dontAnimate()
                    .into(binding.problemImg);
        }
        if (!TextUtils.isEmpty(problem.wrongAnswerImgPath)) {
            Glide.with(this)
                    .load(Uri.parse(problem.wrongAnswerImgPath))
                    .dontAnimate()
                    .into(binding.wrongAnwerImg);
        }
        if (!TextUtils.isEmpty(problem.correctImgPath)) {
            Glide.with(this)
                    .load(Uri.parse(problem.correctImgPath))
                    .dontAnimate()
                    .into(binding.correctAnswerImg);
        }
    }
}