package com.traidev.kaisebhi.HomeNavigation.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.traidev.kaisebhi.R;
import com.traidev.kaisebhi.Utility.Main_Interface;
import com.traidev.kaisebhi.Utility.Network.RetrofitClient;
import com.traidev.kaisebhi.Utility.SharedPrefManager;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<QuestionsModel> questions;
    private QuestionsAdapter adapter;
    private Main_Interface main_interface;
    private GifImageView progressImg;
    private ShimmerFrameLayout shimmerFrameLayout;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_favorites, container, false);

        shimmerFrameLayout = root.findViewById(R.id.SearchloadingShimmer);

        recyclerView = root.findViewById(R.id.allquestions);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        fetchQuestions();

        return root;
    }

    public void fetchQuestions()
    {
        shimmerFrameLayout.startShimmerAnimation();

        SharedPrefManager sh = new SharedPrefManager(getActivity());

        main_interface = RetrofitClient.getApiClient().create(Main_Interface.class);

        Call<List<QuestionsModel>> call = main_interface.getallQuestions(sh.getsUser().getUid(),"favorites");

        call.enqueue(new Callback<List<QuestionsModel>>() {
            @Override
            public void onResponse(Call<List<QuestionsModel>> call, Response<List<QuestionsModel>> response) {

                questions = response.body();
                adapter = new QuestionsAdapter(questions,getActivity());
                recyclerView.setAdapter(adapter);

                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<QuestionsModel>> call, Throwable t) {

            }
        });
    }





}
