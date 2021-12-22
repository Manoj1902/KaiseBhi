package com.traidev.kaisebhi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.traidev.kaisebhi.HomeNavigation.AddQuestion.Add_Queastion;
import com.traidev.kaisebhi.HomeNavigation.Notifications.Notifications;
import com.traidev.kaisebhi.HomeNavigation.home.FavoriteFragment;
import com.traidev.kaisebhi.HomeNavigation.home.HomeFragment;
import com.traidev.kaisebhi.HomeNavigation.home.MenuFragment;
import com.traidev.kaisebhi.HomeNavigation.home.MineQuestFragment;
import com.traidev.kaisebhi.Utility.DefaultResponse;
import com.traidev.kaisebhi.Utility.Network.RetrofitClient;
import com.traidev.kaisebhi.Utility.SharedPrefManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.traidev.kaisebhi.Utility.Network.RetrofitClient.BASE_URL;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FrameLayout MainFrame;

    BottomNavigationView bottomNavigation;

    ImageView floating_add_button;

    SharedPrefManager sharedPrefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        getDelegate().setLocalNightMode(
                AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_home);

        sharedPrefManager =  new SharedPrefManager(getApplication());
        floating_add_button = findViewById(R.id.floating_add_button);
        floating_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cart = new Intent(getApplicationContext(), Add_Queastion.class);
                startActivity(cart);

            }
        });

        MainFrame = findViewById(R.id.nav_host_fragment);

        bottomNavigation = findViewById(R.id.navigation);

        bottomNavigation.setSelectedItemId(R.id.nav_qna);
        bottomNavigation.setItemIconTintList(null);


        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.nav_menu:
                        changeFragment(new MenuFragment());
                        break;
                    case R.id.nav_star:
                        changeFragment(new FavoriteFragment());
                        break;
                    case R.id.nav_qna:
                        changeFragment(new HomeFragment());
                        break;
                    case R.id.nav_min_ques:
                        changeFragment(new MineQuestFragment());
                        break;
                    case R.id.nav_notify:
                        changeFragment(new Notifications());
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        bottomNavigation.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_menu:
                        changeFragment(new MenuFragment());
                        break;
                    case R.id.nav_star:
                        changeFragment(new FavoriteFragment());
                        break;
                    case R.id.nav_qna:
                        changeFragment(new HomeFragment());
                        break;
                    case R.id.nav_min_ques:
                        changeFragment(new MineQuestFragment());
                        break;
                    case R.id.nav_notify:
                        changeFragment(new Notifications());
                        break;
                }
            }
        });
    }


    private void changeFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(MainFrame.getId(), fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();

    }




}

