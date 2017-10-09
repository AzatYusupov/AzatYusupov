package com.usupov.autopark.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import product.card.R;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.usupov.autopark.adapter.UserPartListAdapter;
import com.usupov.autopark.config.PartRestURIConstants;
import com.usupov.autopark.http.Headers;
import com.usupov.autopark.json.Car;
import com.usupov.autopark.model.CarModel;
import com.usupov.autopark.model.UserPartModel;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PartListActivity extends BasicActivity {

    private RecyclerView rvUserPartList;
    public static List<UserPartModel> userPartList;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_part_list, null, false);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.addView(contentView, 0);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_parts);

        initFabUserPart();

        initRecyclerView();
        loadUserParts();
    }

    private void loadUserParts() {
        progressBar = (ProgressBar) findViewById(R.id.pbPartList);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.VISIBLE);
        String url = PartRestURIConstants.GET_USER_PARTS;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson g = new Gson();
                        userPartList = g.fromJson(response, new TypeToken<List<UserPartModel>>(){}.getType());
                        progressBar.setVisibility(View.GONE);
                        findViewById(R.id.scrollPartList).setVisibility(View.VISIBLE);
                        initUserPartList();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null && (error.networkResponse.statusCode== HttpStatus.SC_UNAUTHORIZED || error.networkResponse.statusCode==HttpStatus.SC_INTERNAL_SERVER_ERROR)) {
                            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                                    android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                            Intent intent = new Intent(PartListActivity.this, LoginActivity.class);
                            intent.putExtra("unauthorized", true);
                            startActivity(intent, bundle);
                            finishAffinity();
                        }
                        else
                            Toast.makeText(PartListActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void initUserPartList() {
        UserPartListAdapter adapter = new UserPartListAdapter(this, userPartList);
        rvUserPartList.setAdapter(adapter);
    }
    private void initRecyclerView() {
        rvUserPartList  = (RecyclerView) findViewById(R.id.list_user_part);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        rvUserPartList.setLayoutManager(mLayoutManager);
        rvUserPartList.setItemAnimator(new DefaultItemAnimator());
        rvUserPartList.setNestedScrollingEnabled(false);
    }
    private void initFabUserPart() {
        FloatingActionButton fabUserPart = (FloatingActionButton) findViewById(R.id.fab_new_user_part);
        fabUserPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<CarModel> carList = Car.getCarList(getApplicationContext());
                if (carList==null || carList.size()==0) {
                    Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                            android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                    startActivity(new Intent(PartListActivity.this, CarListActivity.class), bundle);
                    finish();
                }
                else {
                    Intent intent = new Intent(PartListActivity.this, PartNewActivity.class);
                    Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                            android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                    startActivity(intent, bundle);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrParts(newText);
                return false;
            }
        });
        return true;
    }

    private void filtrParts(String matchString) {
        if (userPartList==null || userPartList.isEmpty())
            return;
        matchString = matchString.trim().toLowerCase();
        List<UserPartModel> filteredPartList = new ArrayList<>();
        for (UserPartModel part : userPartList) {
            String article = part.getArticle().toLowerCase();
            String partName = part.getPartName();
            String carName = part.getModelName()+" "+part.getYearName();
            if (!carName.startsWith(part.getBrandName()))
                carName = part.getBrandName() + carName;
            carName = carName.toLowerCase();
            if (article.indexOf(matchString) >= 0 || partName.indexOf(matchString) >= 0 || carName.indexOf(matchString) >= 0)
                filteredPartList.add(part);
        }
        UserPartListAdapter adapter = new UserPartListAdapter(this, filteredPartList);
        rvUserPartList.setAdapter(adapter);
    }
}
