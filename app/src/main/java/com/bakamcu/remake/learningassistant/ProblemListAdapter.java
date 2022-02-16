package com.bakamcu.remake.learningassistant;

import static com.bakamcu.remake.learningassistant.AddProblem.TAG;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProblemListAdapter extends ListAdapter<Problem, ProblemListAdapter.ProblemViewHolder> {

    public ProblemListAdapter() {
        super(new DiffUtil.ItemCallback<Problem>() {
            @Override
            public boolean areItemsTheSame(@NonNull Problem oldItem, @NonNull Problem newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Problem oldItem, @NonNull Problem newItem) {
                return (oldItem.getProblemSource().equals(newItem.getProblemSource()));
            }
        });
    }

    @NonNull
    @Override
    public ProblemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater probInflater = LayoutInflater.from(parent.getContext());
        View itemView = probInflater.inflate(R.layout.cell_problem, parent, false);
        return new ProblemViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProblemViewHolder holder, int position) {
        holder.problemSource.setText(getItem(position).problemSource);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        String updateTime = format.format(new Date(getItem(position).addTime));
        holder.addTime.setText(getItem(position).subject + "," + updateTime + "入库");
        Log.d(TAG, "onBindViewHolder rating  " + getItem(position).problemSource + ":   " + getItem(position).probRate);
        if (getItem(position).probRate == 5) {
            holder.ratingPercent.setText("已完全掌握");
            holder.problemSource.setTextColor(Color.parseColor("#6DFF00"));
        } else {
            if (getItem(position).probRate == 0f) {
                holder.ratingPercent.setText("完全不懂");
                holder.problemSource.setTextColor(Color.parseColor("#FF2F00"));
            } else {
                holder.ratingPercent.setText("掌握了" + getItem(position).probRate * 20 + "%");
            }
        }
    }

    static class ProblemViewHolder extends RecyclerView.ViewHolder{
        TextView problemSource, addTime, ratingPercent;

        public ProblemViewHolder(@NonNull View itemView) {
            super(itemView);
            problemSource = itemView.findViewById(R.id.problemSourceText);
            addTime = itemView.findViewById(R.id.addTimeText);
            ratingPercent = itemView.findViewById(R.id.ratingPercentText);
        }
    }
}
