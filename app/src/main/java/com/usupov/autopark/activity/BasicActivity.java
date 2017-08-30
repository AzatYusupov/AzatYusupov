package com.usupov.autopark.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import productcard.ru.R;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.json.UserJson;
import com.usupov.autopark.model.UserModel;


public class BasicActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    MenuItem selectedMenuItem = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        initToolbar();
        initAccountDetails();

    }

    private void initAccountDetails() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        ImageView accountImage = (ImageView) headerView.findViewById(R.id.image_account);
        TextView accountName = (TextView) headerView.findViewById(R.id.name_account);
        TextView accountEmail = (TextView) headerView.findViewById(R.id.email_account);


        final UserModel user = UserJson.getUser(getApplicationContext());

        if (user==null) {
            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                    android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
            startActivity(new Intent(BasicActivity.this, LoginActivity.class), bundle);
            finishAffinity();
        }
        else {
            accountName.setText(user.getName());
            accountEmail.setText(user.getEmail());
        }
        accountImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BasicActivity.this, UpdateActivity.class);
                Gson g = new Gson();
                intent.putExtra("user", g.toJson(user, UserModel.class));
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                startActivity(intent, bundle);
                finishAffinity();
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

                MenuItem item = selectedMenuItem;
                if (item==null)
                    return;
                final int id = item.getItemId();
                if (item.isChecked()) {
                    return;
                }

                item.setChecked(true);

                Class targetClass = null;
                switch (id) {
                    case  R.id.nav_car_add :
                        targetClass = CarNewActivity.class; break;
                    case R.id.nav_part_add :
                        targetClass = PartNewActivity.class; break;
                    case R.id.nav_parts :
                        targetClass = PartListActivity.class; break;
                    case R.id.nav_car_list :
                        targetClass = CarListActivity.class; break;
                    case R.id.nav_logout :
                        HttpHandler.removeAutToken(getApplicationContext()); targetClass = LoginActivity.class; break;
                    case R.id.nav_settings :
                        break;
                    default:
                        break;
                }

                if (targetClass != null) {
                    Intent intent = new Intent(BasicActivity.this, targetClass);
                    Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                            android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                    startActivity(intent, bundle);
                    finish();
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        selectedMenuItem = item;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return false;
    }

}
