package com.bakamcu.remake.learningassistant;

import android.annotation.SuppressLint;
import android.content.Intent;
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

    @SuppressLint("SetTextI18n")
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
        binding.detailRatingPercentText.setText(Float.parseFloat(problem.probRate) * 20 + "%");
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
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .into(binding.wrongAnwerImg);
        }
        if (!TextUtils.isEmpty(problem.correctImgPath)) {
            Glide.with(this)
                    .load(Uri.parse(problem.correctImgPath))
                    .dontAnimate()
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .into(binding.correctAnswerImg);
        }


        //----------------------
        binding.edit.setOnClickListener(view -> {
            Intent intent = new Intent(DetailActivity.this, UpdateProblemActivity.class);
            intent.putExtra("problem", problem);
            startActivity(intent);
            finish();
        });
    }
}