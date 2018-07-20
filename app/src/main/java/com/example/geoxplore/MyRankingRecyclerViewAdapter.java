package com.example.geoxplore;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geoxplore.api.ApiUtils;
import com.example.geoxplore.api.model.UserStatsRanking;
import com.example.geoxplore.api.service.UserService;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class MyRankingRecyclerViewAdapter extends RecyclerView.Adapter<MyRankingRecyclerViewAdapter.ViewHolder> {

    private List<UserStatsRanking> usersStats;
    private String user;

    public MyRankingRecyclerViewAdapter(List<UserStatsRanking> items, String user) {
        usersStats = items;
        items.add(0, new UserStatsRanking("s",0,0,0));
        this.user = user;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ranking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        UserStatsRanking userStatsRanking = usersStats.get(position);
        String username = userStatsRanking.getUsername();
        int openedChests = userStatsRanking.getOpenedChests();
        int level = userStatsRanking.getLevel();

        if(position>0){
            holder.mRank.setText(String.valueOf(position));
            holder.mName.setText(username);
            holder.mLevel.setText(String.valueOf(level));
            holder.mOpenedChest.setText(String.valueOf(openedChests));
            holder.setReward(position-1);
            holder.mAvatar.setVisibility(View.VISIBLE);
            holder.mReward.setVisibility(View.VISIBLE);

        }
        else{
            holder.mRank.setText("#");
            holder.mName.setText("username");
            holder.mLevel.setText("level");
            holder.mOpenedChest.setText("opened");
            holder.mReward.setVisibility(View.INVISIBLE);
            holder.mAvatar.setVisibility(View.INVISIBLE);
        }


        ApiUtils
                .getService(UserService.class)
                .getUserAvatar(user, username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(x->{
                    Toast.makeText(holder.itemView.getContext(), x.getMessage(), Toast.LENGTH_LONG).show();
                    return null;
                })
                .subscribe(bodyResponse -> {
                    if(bodyResponse.isSuccessful()){
                        if(bodyResponse.body()!=null){
                            Bitmap bm = BitmapFactory.decodeStream(bodyResponse.body().byteStream());
                            holder.mAvatar.setImageBitmap(bm);
                        }

                    }
                });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersStats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mRank, mName, mLevel, mOpenedChest;
        public final ImageView mReward;
        public final CircleImageView  mAvatar;



        public ViewHolder(View view) {
            super(view);
            mView = view;
            mRank = (TextView) view.findViewById(R.id.tv_rank);
            mName = (TextView) view.findViewById(R.id.tv_name);
            mLevel = (TextView) view.findViewById(R.id.tv_level);
            mOpenedChest = (TextView) view.findViewById(R.id.tv_opened_chest);
            mReward = (ImageView) view.findViewById(R.id.iv_reward);
            mAvatar = (CircleImageView) view.findViewById(R.id.user_image);
        }

        public void setReward(final int position){
            Resources res = this.itemView.getResources();
            switch(position){
                case 0: mReward.setImageDrawable(res.getDrawable(R.drawable.gold_medal)); break;
                case 1: mReward.setImageDrawable(res.getDrawable(R.drawable.silver_medal)); break;
                case 2: mReward.setImageDrawable(res.getDrawable(R.drawable.bronze_medal)); break;
                default: mReward.setImageDrawable(null);
            }

        }

        @Override
        public String toString() {
            return super.toString();
        }
    }


}
