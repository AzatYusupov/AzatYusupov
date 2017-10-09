package com.usupov.autopark.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import product.card.R;

import com.usupov.autopark.config.AdsURIConstants;
import com.usupov.autopark.config.UserURIConstants;
import com.usupov.autopark.http.Headers;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.UserModel;

import org.apache.http.HttpStatus;

import java.util.Map;


public class BasicActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



    public static String userName, userEmail;

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

    private TextView accountName, accountEmail;

    private void initAccountDetails() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        FloatingActionButton accountFab = (FloatingActionButton) headerView.findViewById(R.id.image_account);
        accountName = (TextView) headerView.findViewById(R.id.name_account);
        accountEmail = (TextView) headerView.findViewById(R.id.email_account);

        if (userName==null)
            loadUser();
        else {
            accountName.setText(userName);
            accountEmail.setText(userEmail);
        }

        accountFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BasicActivity.this, UpdateActivity.class);
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                startActivity(intent, bundle);
                finishAffinity();
            }
        });


    }

    private void loadUser() {
        String url = UserURIConstants.USER_INFO;
        StringRequest stringReques = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UserModel user = new Gson().fromJson(response, UserModel.class);
                        userName = user.getName();
                        userEmail = user.getEmail();
                        accountName.setText(userName);
                        accountEmail.setText(userEmail);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        if (error.networkResponse != null && (error.networkResponse.statusCode==HttpStatus.SC_UNAUTHORIZED || error.networkResponse.statusCode==HttpStatus.SC_INTERNAL_SERVER_ERROR)) {
//                            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
//                                    android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
//                            Intent intent = new Intent(BasicActivity.this, LoginActivity.class);
//                            intent.putExtra("unauthorized", true);
//                            startActivity(intent, bundle);
//                            finishAffinity();
//                        }
//                        else
//                            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Headers.headerMap(getApplicationContext());
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringReques);
    }

    Class targetClass = null;

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
                        userName = null;
                        userEmail = null;
                        HttpHandler.removeAutToken(getApplicationContext());
                        targetClass = LoginActivity.class; break;
                    case R.id.nav_settings :
                        break;
                    case R.id.nav_adds : {
                        new AlertDialog.Builder(BasicActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Объявления")
                                .setMessage(getString(R.string.want_ads))
                                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String url = AdsURIConstants.DO_ANNOUNCE;
                                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        Toast.makeText(BasicActivity.this, getString(R.string.success_ads), Toast.LENGTH_LONG).show();
                                                        targetClass = PartListActivity.class;
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        System.out.println(error.getMessage()+" 745645646");
                                                        if (error.networkResponse != null && (error.networkResponse.statusCode== HttpStatus.SC_UNAUTHORIZED || error.networkResponse.statusCode==HttpStatus.SC_INTERNAL_SERVER_ERROR)) {
                                                            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                                                                    android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                                                            Intent intent = new Intent(BasicActivity.this, LoginActivity.class);
                                                            intent.putExtra("unauthorized", true);
                                                            startActivity(intent, bundle);
                                                            finishAffinity();
                                                        }
                                                        else if (error.networkResponse != null && error.networkResponse.statusCode==HttpStatus.SC_FORBIDDEN) {
                                                            Toast.makeText(BasicActivity.this, getString(R.string.fill_data_ads), Toast.LENGTH_LONG).show();
                                                            targetClass = UpdateActivity.class;
                                                        }
                                                        else if (error.networkResponse != null && error.networkResponse.statusCode==HttpStatus.SC_NO_CONTENT) {
                                                            Toast.makeText(BasicActivity.this, getString(R.string.add_part_ads), Toast.LENGTH_LONG).show();
                                                            targetClass = PartListActivity.class;
                                                        }
                                                        else {
                                                            Toast.makeText(BasicActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                                                            targetClass = PartListActivity.class;
                                                        }
                                                    }
                                                }
                                        ) {
                                            @Override
                                            public Map<String, String> getHeaders() throws AuthFailureError {
                                                return Headers.headerMap(getApplicationContext());
                                            }
                                        };
                                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                        requestQueue.add(stringRequest);
                                    }
                                })
                                .setNegativeButton("Нет", null)
                                .show();
                        break;
                    }
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
