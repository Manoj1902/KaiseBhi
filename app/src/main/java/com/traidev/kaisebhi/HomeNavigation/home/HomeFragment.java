package com.traidev.kaisebhi.HomeNavigation.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.traidev.kaisebhi.ActivityForFrag;
import com.traidev.kaisebhi.R;
import com.traidev.kaisebhi.Utility.Main_Interface;
import com.traidev.kaisebhi.Utility.Network.RetrofitClient;
import com.traidev.kaisebhi.Utility.SharedPrefManager;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private List<QuestionsModel> questions;
    private QuestionsAdapter adapter;
    private Main_Interface main_interface;
    private ShimmerFrameLayout shimmerFrameLayout;
    int totalItems,currentItems,scrollOutItems=0;

    SwipeRefreshLayout refreshQuesitons;
    int currentPage = 1;
    ProgressBar loadMoreProgress;
    Boolean isScrolling = false;

    NestedScrollView nestRecy;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        refreshQuesitons = root.findViewById(R.id.refreshQuesitons);

        shimmerFrameLayout = root.findViewById(R.id.SearchloadingShimmer);
        loadMoreProgress = root.findViewById(R.id.loadMoreProgress);
        nestRecy = root.findViewById(R.id.nestRecy);

        root.findViewById(R.id.searchQues).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent seac = new Intent(getContext(), ActivityForFrag.class);
                seac.putExtra("Frag","searchQ");
                getActivity().startActivity(seac);

            }
        });


        refreshQuesitons.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchQuestions(1);
            }
        });

        recyclerView = root.findViewById(R.id.allquestions);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        fetchQuestions(currentPage);

        nestRecy.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if(scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                    currentPage+=1;
                    loadMoreProgress.setVisibility(View.VISIBLE);
                    fetchMore();
                }

            }
        });

        return root;
    }



    public void fetchQuestions(int cPage)
    {
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();
        recyclerView.setAdapter(null);

        SharedPrefManager sh = new SharedPrefManager(getActivity());
        main_interface = RetrofitClient.getApiClient().create(Main_Interface.class);
        Call<List<QuestionsModel>> call = main_interface.getallQuestionsHome(sh.getsUser().getUid(),String.valueOf(cPage));

        call.enqueue(new Callback<List<QuestionsModel>>() {
            @Override
            public void onResponse(Call<List<QuestionsModel>> call, Response<List<QuestionsModel>> response) {

                questions = response.body();
                adapter = new QuestionsAdapter(questions,getActivity());
                recyclerView.setAdapter(adapter);

                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
                refreshQuesitons.setRefreshing(false);

            }

            @Override
            public void onFailure(Call<List<QuestionsModel>> call, Throwable t) {
                refreshQuesitons.setRefreshing(false);

            }
        });
    }


    public void fetchMore()
    {

        SharedPrefManager sh = new SharedPrefManager(getActivity());

        main_interface = RetrofitClient.getApiClient().create(Main_Interface.class);

        Call<List<QuestionsModel>> call = main_interface.getallQuestionsHome(sh.getsUser().getUid(),String.valueOf(currentPage));

        call.enqueue(new Callback<List<QuestionsModel>>() {
            @Override
            public void onResponse(Call<List<QuestionsModel>> call, Response<List<QuestionsModel>> response) {

                questions.addAll(response.body());

                adapter = new QuestionsAdapter(questions,getActivity());
                recyclerView.setAdapter(adapter);

                loadMoreProgress.setVisibility(View.GONE);


            }

            @Override
            public void onFailure(Call<List<QuestionsModel>> call, Throwable t) {

            }
        });
    }



}
