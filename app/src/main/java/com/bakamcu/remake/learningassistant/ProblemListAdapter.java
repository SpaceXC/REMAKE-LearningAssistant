package com.bakamcu.remake.learningassistant;

import static com.bakamcu.remake.learningassistant.AddProblem.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class ProblemListAdapter extends ListAdapter<Problem, ProblemListAdapter.ProblemViewHolder> {

    public ProblemListAdapter(Context context) {
        super(new DiffUtil.ItemCallback<Problem>() {
            @Override
            public boolean areItemsTheSame(@NonNull Problem oldItem, @NonNull Problem newItem) {
                return oldItem.getProblemID() == newItem.getProblemID();
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
        final ProblemViewHolder holder = new ProblemViewHolder(itemView);

        return new ProblemViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProblemViewHolder holder, int position) {
        holder.problemSource.setText(getItem(position).problemSource);

        holder.addTime.setText(getItem(position).subject + "," + getItem(position).getUpdateTimeString() + "入库");
        Log.d(TAG, "onBindViewHolder rating  " + getItem(position).problemSource + ":   " + getItem(position).probRate);
        float probRate = Float.parseFloat(getItem(position).probRate);
        if (probRate == 5) {
            holder.ratingPercent.setText("已完全掌握");
        } else {
            if (probRate == 0f) {
                holder.ratingPercent.setText("完全不懂");
            } else {
                holder.ratingPercent.setText("掌握了" + probRate * 20 + "%");
            }
        }
        holder.itemView.setOnClickListener(view -> {
            Intent detail = new Intent(holder.itemView.getContext(), DetailActivity.class);
            detail.putExtra("problem", getItem(position));
            holder.itemView.getContext().startActivity(detail);
        });
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
