package com.tiberiugaspar.mylauncher.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.tiberiugaspar.mylauncher.R;
import com.tiberiugaspar.mylauncher.model.Article;
import com.tiberiugaspar.mylauncher.util.NewsApiUtil;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private List<Article> articleList;
    private Context context;

    public ArticleAdapter(List<Article> articleList, Context context) {

        this.articleList = articleList;
        this.context = context;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_article, parent, false);

        return new ArticleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ArticleViewHolder holder, int position) {

        Article article = articleList.get(position);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(NewsApiUtil.getRandomDrawbleColor());
        requestOptions.error(NewsApiUtil.getRandomDrawbleColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();

        Glide.with(context)
                .load(article.getUrlToImage())
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.image);

        holder.title.setText(article.getTitle());
        holder.description.setText(article.getDescription());
        holder.source.setText(article.getSource().getName());
        holder.time.setText(String.format(" • %s", NewsApiUtil.DateToTimeFormat(article.getPublishedAt())));
        holder.publishDate.setText(NewsApiUtil.DateFormat(article.getPublishedAt()));
        holder.author.setText(article.getAuthor());

        holder.itemView.setOnClickListener(view -> {

            //when an article is clicked, open the web browser with the article's link
            String url = articleList.get(position).getUrl();
            Intent intent = new Intent(Intent.ACTION_VIEW);

            intent.setData(Uri.parse(url));

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder {

        TextView title, description, author, publishDate, source, time;
        ImageView image;
        ProgressBar progressBar;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            author = itemView.findViewById(R.id.author);
            publishDate = itemView.findViewById(R.id.publishedAt);
            source = itemView.findViewById(R.id.source);
            time = itemView.findViewById(R.id.time);
            image = itemView.findViewById(R.id.img);
            progressBar = itemView.findViewById(R.id.progress_load_photo);
        }
    }

}
