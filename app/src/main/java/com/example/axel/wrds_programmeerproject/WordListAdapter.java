package com.example.axel.wrds_programmeerproject;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by axel on 10-1-17.
 */

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView nWords;
        private TextView desc;

        private TextView date;
        private TextView creator;
        private TextView language;

        public ViewHolder(View v) {
            super(v);

            title = (TextView) v.findViewById(R.id.title_textView);
            nWords = (TextView) v.findViewById(R.id.nWords_textView);
            desc = (TextView) v.findViewById(R.id.desc_textView);

            date = (TextView) v.findViewById(R.id.date_textView);
            creator = (TextView) v.findViewById(R.id.creator_textView);
            language = (TextView) v.findViewById(R.id.language_textView);

        }
    }

    @Override
    public WordListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(WordListAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
