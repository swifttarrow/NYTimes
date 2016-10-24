package com.example.swifttarrow.nytimessearch;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.swifttarrow.nytimessearch.activities.ArticleActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by swifttarrow on 10/20/2016.
 */

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    private Context mContext;
    private List<Article> mArticles;

    public ArticlesAdapter(Context context, List<Article> articles){
        mContext = context;
        this.mArticles = articles;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView tvTitle;
        public ImageView ivImage;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            itemView.setOnClickListener(this);
        }

        // Handles the row being being clicked
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                Intent i = new Intent(mContext, ArticleActivity.class);
                Article article = mArticles.get(position);
                i.putExtra("article", article);
                mContext.startActivity(i);
            }
        }
    }

    private Context getContext(){
        return mContext;
    }

    @Override
    public ArticlesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View articlesView = inflater.inflate(R.layout.item_article_result, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(articlesView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ArticlesAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Article article = mArticles.get(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.tvTitle;
        textView.setText(article.getHeadline());
        ImageView imageView = viewHolder.ivImage;
        imageView.setImageResource(0);

        String thumbnail = article.getThumbNail();

        if (!TextUtils.isEmpty(thumbnail)){
            Picasso.with(getContext()).load(thumbnail).fit().into(imageView);
        } else {
            Picasso.with(getContext()).load(R.drawable.news_icon).fit().into(imageView);
        }

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    public void addAll(List<Article> articles){
        int insertPos = articles.size();
        this.mArticles.addAll(articles);
        notifyItemRangeInserted(insertPos, articles.size());
    }

    public void clear(){
        int itemCount = getItemCount();
        this.mArticles.clear();
        notifyItemRangeRemoved(0, itemCount);
    }
}
