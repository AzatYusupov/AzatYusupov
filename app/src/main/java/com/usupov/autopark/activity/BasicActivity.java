package com.usupov.autopark.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.usupov.autopark.R;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.json.UserJson;
import com.usupov.autopark.model.UserModel;


public class BasicActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        View headerView = navigationView.getHeaderView(0);
        ImageView accountImage = (ImageView) headerView.findViewById(R.id.image_account);
        TextView accountName = (TextView) headerView.findViewById(R.id.name_account);
        TextView accountEmail = (TextView) headerView.findViewById(R.id.email_account);


        final UserModel user = UserJson.getUser(getApplicationContext());

        if (user==null) {
            startActivity(new Intent(BasicActivity.this, LoginActivity.class));
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
                startActivity(intent);
                finishAffinity();
            }
        });
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
        // Handle navigation view item clicks here.

        final int id = item.getItemId();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (id == R.id.nav_car_add) {

                    startActivity(new Intent(BasicActivity.this, CarNewActivity.class));
                }
                else if (id == R.id.nav_part_add) {

                    startActivity(new Intent(BasicActivity.this, PartActivity.class));
                }
                else if (id == R.id.nav_parts) {

                    startActivity(new Intent(BasicActivity.this, PartListActivity.class));
                }
                else if (id == R.id.nav_car_list) {

                    startActivity(new Intent(BasicActivity.this, CarListActivity.class));
                }
                else if (id == R.id.nav_settings) {

                }
                else if (id == R.id.nav_logout) {
                    HttpHandler.removeAutToken(getApplicationContext());
                    startActivity(new Intent(BasicActivity.this, LoginActivity.class));
                }
                finish();
            }
        }, 300);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
