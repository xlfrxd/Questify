package com.example.questifyv1.post;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questifyv1.R;
import com.example.questifyv1.activity.DetailActivity;
import com.example.questifyv1.activity.MainActivity;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.title.setText(post.getTitle());
        holder.dueDate.setText(post.getDueDate());
        holder.username.setText("@" + post.getUsername());
        holder.price.setText("PHP " + post.getPrice());
        holder.image.setImageResource(post.getImageResource());
        holder.desc.setText(post.getDesc());
        holder.dibsBy.setText(post.getDibsBy());
        holder.status.setText(post.getStatus());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("title", post.getTitle());
            intent.putExtra("dueDate", post.getDueDate());
            intent.putExtra("username", post.getUsername());
            intent.putExtra("price", post.getPrice());
            intent.putExtra("imageResource", post.getImageResource());
            intent.putExtra("desc", post.getDesc());
            intent.putExtra("dibsBy",post.getDibsBy());
            intent.putExtra("status", post.getStatus());
            // Get currently signed in user
            intent.putExtra("userSession", MainActivity.getUserSession());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView title, dueDate, username, price, desc, dibsBy, status;
        ImageView image;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            dueDate = itemView.findViewById(R.id.dueDate);
            username = itemView.findViewById(R.id.username);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.image);
            desc = itemView.findViewById(R.id.desc);
            dibsBy = itemView.findViewById(R.id.dibsBy);
            status = itemView.findViewById(R.id.status);
        }
    }
}
