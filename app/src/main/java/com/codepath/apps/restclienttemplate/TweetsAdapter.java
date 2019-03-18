package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

import butterknife.InjectView;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    @InjectView(R.id.ivProfileImage)
    ImageView ivProfileImage;
    @InjectView(R.id.tvScreenName)
    TextView tvScreenName;
    @InjectView(R.id.tvBody)
    TextView tvBody;
    @InjectView(R.id.tvcreatedAt)
    TextView tvcreatedAt;
    @InjectView(R.id.tvHeart)
    ImageView tvHeart;
    @InjectView(R.id.tvRetweet)
    ImageView tvRetweet;
    @InjectView(R.id.tvReply)
    ImageView tvReply;
    @InjectView(R.id.tvreTweetCount)
    TextView tvreTweetCount;
    @InjectView(R.id.tvFavoriteCount)
    TextView tvFavoriteCount;


    private Context context;
    private List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweetx, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.tvBody.setText(tweet.body);
        holder.tvScreenName.setText(tweet.user.screenName);
        holder.tvcreatedAt.setText(tweet.getFormattedTimestamp());
        holder.tvtweetRepostCount.setText(tweet.reTweetCount);
        holder.tvfavoriteCount.setText(tweet.favoriteCount);

        Glide.with(context).load(tweet.user.profileImageUrl).apply(new RequestOptions().transform(new RoundedCornersTransformation(100, 10))).into(holder.ivProfileImage);

        if(tweet.hasMedia) {
            switch (tweet.mediaType) {
                case "photo":
                    holder.tvMediaContent.setVisibility(View.VISIBLE);
                    Glide.with(context).load(tweet.mediaUrl).into(holder.tvMediaContent);
                    break;
                case "video":
                    holder.tvMediaContentVideo.setVisibility(View.VISIBLE);
                    holder.tvMediaContentVideo.setVideoURI(Uri.parse(tweet.mediaUrl));
                    break;
                case "animated_gif":
                    holder.tvMediaContentVideo.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }

        holder.tvHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!holder.heartClick) {
                    Glide.with(context).load(R.drawable.ic_vector_heart).into(holder.tvHeart);
                    holder.tvHeart.setColorFilter(Color.rgb(238, 45, 17));
                    holder.heartClick = true;
                }
                else {
                    Glide.with(context).load(R.drawable.ic_vector_heart_stroke).into(holder.tvHeart);
                    holder.tvHeart.setColorFilter(Color.rgb(214, 209, 211));
                    holder.heartClick = false;
                }
            }
        });

        holder.tvReTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!holder.reTweetClick) {
                    Glide.with(context).load(R.drawable.ic_vector_retweet).into(holder.tvReTweet);
                    holder.tvReTweet.setColorFilter(Color.rgb(102, 255, 51));
                    holder.reTweetClick = true;
                }
                else {
                    Glide.with(context).load(R.drawable.ic_vector_retweet_stroke).into(holder.tvReTweet);
                    holder.tvReTweet.setColorFilter(Color.rgb(214, 209, 211));
                    holder.reTweetClick = false;
                }
            }
        });

        holder.tvReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!holder.messageClick) {
                    Glide.with(context).load(R.drawable.ic_vector_messages).into(holder.tvReply);
                    holder.tvReply.setColorFilter(Color.rgb(0, 0, 0));
                    holder.messageClick = true;
                }
                else {
                    Glide.with(context).load(R.drawable.ic_vector_messages_stroke).into(holder.tvReply);
                    holder.tvReply.setColorFilter(Color.rgb(214, 209, 211));
                    holder.messageClick = false;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addTweets(List<Tweet> tweetList) {
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }

    //Define the viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileImage;
        public TextView tvScreenName;
        public TextView tvBody;
        public TextView tvcreatedAt;
        public TextView tvtweetRepostCount;
        public TextView tvfavoriteCount;
        public ImageView tvMediaContent;
        public VideoView tvMediaContentVideo;

        public ImageView tvHeart;
        public ImageView tvReTweet;
        public ImageView tvReply;

        public boolean heartClick = false;
        public boolean reTweetClick = false;
        public boolean messageClick = false;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvcreatedAt = itemView.findViewById(R.id.tvcreatedAt);
            tvtweetRepostCount = itemView.findViewById(R.id.tvreTweetCount);
            tvfavoriteCount = itemView.findViewById(R.id.tvFavoriteCount);
            tvMediaContent = itemView.findViewById(R.id.tvMediaContent);
            tvMediaContentVideo = itemView.findViewById(R.id.tvMediaContentVideo);
            tvHeart = itemView.findViewById(R.id.tvHeart);
            tvReTweet = itemView.findViewById(R.id.tvRetweet);
            tvReply = itemView.findViewById(R.id.tvReply);
        }
    }
}
