package com.bakamcu.remake.learningassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.RatingBar;

import com.bakamcu.remake.learningassistant.databinding.ActivityAddProblemBinding;


public class AddProblem extends AppCompatActivity {
    ActivityAddProblemBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_add_problem);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_problem);
        //--------------------------------
        binding.ratingBar2.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                binding.ratingPercent.setText(String.valueOf(v * 20) + "%");
            }
        });
    }
}