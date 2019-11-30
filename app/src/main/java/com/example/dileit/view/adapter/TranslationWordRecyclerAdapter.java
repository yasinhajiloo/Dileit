package com.example.dileit.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dileit.R;
import com.example.dileit.model.TranslationWord;

import java.util.List;

public class TranslationWordRecyclerAdapter extends RecyclerView.Adapter<TranslationWordRecyclerAdapter.ViewHolder> {

    List<TranslationWord> mList;

    public void setData(List<TranslationWord> data){
        mList = data;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_translation_word , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

     class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        RecyclerView mRecyclerView;
         ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_translation_word_item);
            mRecyclerView = itemView.findViewById(R.id.rv_nested_example_words);
        }

        void bindData(TranslationWord word){
             mTextView.setText(word.getTranslatedWord());
        }
    }
}
