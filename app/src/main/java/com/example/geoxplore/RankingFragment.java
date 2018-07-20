package com.example.geoxplore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.geoxplore.api.ApiUtils;
import com.example.geoxplore.api.model.UserStatsRanking;
import com.example.geoxplore.api.service.UserService;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class RankingFragment extends Fragment {
    public static final String TAG = "ranking_fragment";

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    public RankingFragment() {
    }

    @SuppressWarnings("unused")
    public static RankingFragment newInstance(int columnCount) {
        RankingFragment fragment = new RankingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking_list, container, false);
        List<UserStatsRanking> userStatsRankingList = new LinkedList<UserStatsRanking>();

        userStatsRankingList.add(new UserStatsRanking("error", 0, 0, 0));
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            ApiUtils
                    .getService(UserService.class)
                    .getRanking(getArguments().getString(Intent.EXTRA_USER))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn(x -> userStatsRankingList )
                    .subscribe(x ->
                        recyclerView.setAdapter(new MyRankingRecyclerViewAdapter(x, getArguments().getString(Intent.EXTRA_USER)))
                    );
        }
        return view;
    }
}
