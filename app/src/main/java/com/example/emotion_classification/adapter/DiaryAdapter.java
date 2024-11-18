package com.example.emotion_classification.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emotion_classification.R;
import com.example.emotion_classification.model.Diary;

import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {

    private List<Diary> diaryList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Diary diary);
    }
    public DiaryAdapter(List<Diary> diaryList, OnItemClickListener onItemClickListener) {
        this.diaryList = diaryList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_item, parent, false);
        return new DiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        Diary diary = diaryList.get(position);
        holder.tvDate.setText(diary.getDate());
        holder.tvTitle.setText(diary.getTitle());
        holder.tvContent.setText(diary.getContent());
        holder.ivEmotionIcon.setImageResource(diary.getEmotionIconResId());
        // 아이템 클릭 리스너 연결
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(diary);
            }
        });
    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }

    static class DiaryViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTitle, tvContent;
        ImageView ivEmotionIcon;

        DiaryViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            ivEmotionIcon = itemView.findViewById(R.id.ivEmotionIcon);
        }
    }
}