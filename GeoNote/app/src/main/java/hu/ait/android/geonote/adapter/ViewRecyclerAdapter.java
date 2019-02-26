package hu.ait.android.geonote.adapter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hu.ait.android.geonote.MainActivity;
import hu.ait.android.geonote.R;
import hu.ait.android.geonote.ViewActivity;
import hu.ait.android.geonote.data.Post;

public class ViewRecyclerAdapter extends RecyclerView.Adapter<ViewRecyclerAdapter.ViewHolder> {


    private Context context;
    private List<Post> postList;
    private List<String> postKeys;
    private String uId;
    private int lastPosition = -1;

    public ViewRecyclerAdapter(Context context, String uId) {
        this.context = context;
        this.uId = uId;
        this.postList = new ArrayList<Post>();
        this.postKeys = new ArrayList<String>();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_post, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.tvAuthor.setText(context.getString(R.string.user,
                postList.get(holder.getAdapterPosition()).getAuthor()));
        holder.tvTitle.setText(context.getString(R.string.post_title,
                postList.get(holder.getAdapterPosition()).getTitle()));
        holder.tvBody.setText(
                postList.get(holder.getAdapterPosition()).getBody());
        holder.address.setText(context.getString(R.string.near, geocode(
                postList.get(holder.getAdapterPosition()).getLat(), postList.get(holder.getAdapterPosition()).getLon())));
        holder.date.setText(context.getString(R.string.written,
                postList.get(holder.getAdapterPosition()).getDate()));

        setAnimation(holder.itemView, position);

        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(postList.get(holder.getAdapterPosition()).getUid())) {
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removePost(holder.getAdapterPosition());
                }
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(postList.get(holder.getAdapterPosition()).

    getImageUrl()))

    {
        Glide.with(context).load(
                postList.get(holder.getAdapterPosition()).getImageUrl()
        ).into(holder.ivImage);
        holder.ivImage.setVisibility(View.VISIBLE);
    } else

    {
        holder.ivImage.setVisibility(View.GONE);
    }

    setAnimation(holder.itemView, position);
}

    public String geocode(Double lat, Double lon) {
        String place = "";

        try {
            Geocoder gc = new Geocoder(context, Locale.getDefault());
            List<Address> addrs = null;
            addrs = gc.getFromLocation(lat, lon, 1);

            place = addrs.get(0).getAddressLine(0) + "";

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return place;
    }

    public void removePost(int index) {
        FirebaseDatabase.getInstance().getReference("posts").child(
                postKeys.get(index)).removeValue();
        postList.remove(index);
        postKeys.remove(index);
        notifyItemRemoved(index);
    }

    public void removePostByKey(String key) {
        int index = postKeys.indexOf(key);
        if (index != -1) {
            postList.remove(index);
            postKeys.remove(index);
            notifyItemRemoved(index);
        }
    }


    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context,
                    android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void addPost(Post newPost, String key) {
        postList.add(newPost);
        postKeys.add(key);
        notifyDataSetChanged();
    }

public static class ViewHolder extends RecyclerView.ViewHolder {

    public TextView tvAuthor;
    public TextView tvTitle;
    public TextView tvBody;
    public at.markushi.ui.CircleButton btnDelete;
    public ImageView ivImage;
    public TextView address;
    public TextView date;

    public ViewHolder(View itemView) {
        super(itemView);
        tvAuthor = itemView.findViewById(R.id.tvAuthor);
        tvTitle = itemView.findViewById(R.id.tvTitle);
        tvBody = itemView.findViewById(R.id.tvBody);
        btnDelete = itemView.findViewById(R.id.btnDelete);
        ivImage = itemView.findViewById(R.id.ivImage);
        address = itemView.findViewById(R.id.address);
        date = itemView.findViewById(R.id.date);
    }
}
}

