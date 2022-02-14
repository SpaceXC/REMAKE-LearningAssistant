package com.bakamcu.remake.learningassistant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProblemListAdapter extends ListAdapter<Problem, ProblemListAdapter.ProblemViewHolder> {
    private List<Problem> allProbList = new ArrayList<>();

    private Context context;

    public ProblemListAdapter(Context context) {
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
        this.context = context;
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
        if(getItem(position).probRate == 1f){
            holder.ratingPercent.setText("已完全掌握");
            holder.problemSource.setTextColor(Color.parseColor("#00FF24"));
        }else{
            if(getItem(position).probRate == 0f){
                holder.ratingPercent.setText("完全不懂");
                holder.problemSource.setTextColor(Color.parseColor("#FF2F00"));
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
