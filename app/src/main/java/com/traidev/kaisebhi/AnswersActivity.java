package com.traidev.kaisebhi;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.traidev.kaisebhi.HomeNavigation.home.AnswersAdapter;
import com.traidev.kaisebhi.HomeNavigation.home.AnswersModel;
import com.traidev.kaisebhi.Utility.DefaultResponse;
import com.traidev.kaisebhi.Utility.Main_Interface;
import com.traidev.kaisebhi.Utility.Network.RetrofitClient;
import com.traidev.kaisebhi.Utility.SharedPrefManager;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.traidev.kaisebhi.Utility.Network.RetrofitClient.BASE_URL;

public class AnswersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<AnswersModel> answers;
    private AnswersAdapter adapter;
    SharedPrefManager sh;

    EditText msg;
    ImageView sendBtn;
    CircleImageView userImage;
    private ShimmerFrameLayout shimmerFrameLayout;

    ImageView pro,questionimg,shareBtn,ansImg;
    TextView Title,Desc,totalAns,userHead;
    CheckBox favBtn,likeBtn;
    ProgressBar loadAns;

    String Qid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        getDelegate().setLocalNightMode(
                AppCompatDelegate.MODE_NIGHT_NO);
        shimmerFrameLayout = findViewById(R.id.SearchloadingShimmer);

        pro = findViewById(R.id.userPro);
        questionimg = findViewById(R.id.quesImage);
        userImage = findViewById(R.id.addMsgImage);
        loadAns = findViewById(R.id.loadAns);
        shareBtn = findViewById(R.id.shareQuestion);
        ansImg = findViewById(R.id.answers);
        likeBtn = findViewById(R.id.like);
        favBtn = findViewById(R.id.addFav);
        userHead = findViewById(R.id.userHeader);
        Title = findViewById(R.id.quesTitle);
        Desc = findViewById(R.id.quesDesc);
        totalAns = findViewById(R.id.totalAns);

        msg = findViewById(R.id.msg);
        sendBtn = findViewById(R.id.sendMsg);

        sh = new SharedPrefManager(getApplication());
        Glide.with(getApplicationContext()).load(BASE_URL + "user/" + sh.getsUser().getProfile()).dontAnimate().centerInside().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).placeholder(R.drawable.profile).into(userImage);

        recyclerView = findViewById(R.id.allanswers);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            Qid = extras.getString("key");
            Title.setText(extras.getString("title"));
            Desc.setText(extras.getString("desc"));
            userHead.setText(extras.getString("user"));
            totalAns.setText(extras.getString("tans"));
            likeBtn.setChecked(extras.getBoolean("tlikes"));


            if(extras.getString("qimg").length()>0)
            {
                questionimg.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext()).load(BASE_URL + "qimg/"+ extras.getString("qimg")).fitCenter().into(questionimg);
            }
            Glide.with(getApplicationContext()).load(BASE_URL + "user/"+ extras.getString("userpic")).placeholder(R.drawable.profile).fitCenter().into(pro);


            fetchAnsers();
        }


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = msg.getText().toString();

                if(!text.isEmpty()) {

                    String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

                    Pattern p = Pattern.compile(URL_REGEX);
                    Matcher m = p.matcher(text);//replace with string to compare
                    if(m.find()) {
                        Toast.makeText(getApplicationContext(),"Url or any Heperlink is not allowed!",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Pattern mo = Pattern.compile("(0/91)?[7-9][0-9]{9}");
                   Matcher mm = mo.matcher(text);
                    if(mm.find()) {
                        Toast.makeText(getApplicationContext(),"Any Number Sequence / Phone number not allowed!",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    msg.setText("");

                    sendBtn.setEnabled(false);

                    sendBtn.setVisibility(View.GONE);
                    loadAns.setVisibility(View.VISIBLE);
                    Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().addAns(text,Qid,sh.getsUser().getUid());
                    call.enqueue(new Callback<DefaultResponse>() {
                        @Override
                        public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {

                            Toast.makeText(getApplicationContext(),"Answer Added!",Toast.LENGTH_LONG).show();
                            sendBtn.setEnabled(true);
                            fetchAnsers();
                            loadAns.setVisibility(View.GONE);
                            sendBtn.setVisibility(View.VISIBLE);

                        }

                        @Override
                        public void onFailure(Call<DefaultResponse> call, Throwable t) {

                            Toast.makeText(getApplicationContext(),"There is and error posting answer! ",Toast.LENGTH_SHORT).show();
                            loadAns.setVisibility(View.GONE);
                            sendBtn.setEnabled(true);
                            sendBtn.setVisibility(View.VISIBLE);
                        }
                    });
                }else{
                    msg.setError("Type Answer First!");
                }

            }
        });



        SharedPrefManager sh  = new SharedPrefManager(this);
        TextView toolbar = findViewById(R.id.textHeader);

    }



    public void fetchAnsers()
    {

        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();
        Main_Interface main_interface = RetrofitClient.getApiClient().create(Main_Interface.class);

        Call<List<AnswersModel>> call = main_interface.getAnswers(Qid,SharedPrefManager.getInstance(getApplication()).getsUser().getUid());

        call.enqueue(new Callback<List<AnswersModel>>() {
            @Override
            public void onResponse(Call<List<AnswersModel>> call, Response<List<AnswersModel>> response) {

                if(response.code() != 404)
                {
                    answers = response.body();
                    adapter = new AnswersAdapter(answers,getApplicationContext());
                    recyclerView.setAdapter(adapter);
                }
                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<List<AnswersModel>> call, Throwable t) {

            }
        });

    }


    public void backtoActivity(View view) {
        super.onBackPressed();
    }
}
