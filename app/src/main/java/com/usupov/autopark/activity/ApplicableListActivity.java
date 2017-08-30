package com.usupov.autopark.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import productcard.ru.R;
import com.usupov.autopark.adapter.ApplicableAdapter;
import com.usupov.autopark.json.Part;
import com.usupov.autopark.model.CatalogYear;

import java.util.ArrayList;
import java.util.List;

public class ApplicableListActivity extends AppCompatActivity {

    List<CatalogYear> applyList;
    long partId;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicable_list);

        initToolbar();

        initApplyList();

    }

    private void initApplyList() {

        partId = getIntent().getExtras().getLong("partId");
        applyList = new ArrayList<>();

        MyTask taks = new MyTask();
        taks.execute();
    }

    private void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_applicable);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            applyList = Part.getApplicableList(partId, getApplicationContext());
            List<CatalogYear> newApplyList = new ArrayList<>();
            //to do list as Audi Audi A1 2001-2010, 2013
            int last = 0;
            for (int i = 0; i < applyList.size(); i++) {
                last = i;
                while (last + 1 < applyList.size() && applyList.get(last+1).getModelId()==applyList.get(i).getModelId()
                        && (applyList.get(last+1).getName().equals(applyList.get(last).getName()) || Integer.parseInt(applyList.get(last+1).getName())==Integer.parseInt(applyList.get(last).getName()) + 1)) {
                    last++;
                }
                CatalogYear newYear = applyList.get(i);
                if (last != i)
                    newYear.setName(newYear.getName()+"-"+applyList.get(last).getName());
                newApplyList.add(newYear);
                i = last;
            }
            applyList = newApplyList;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            findViewById(R.id.pbApplicable).setVisibility(View.GONE);
            findViewById(R.id.scrollApplicable).setVisibility(View.VISIBLE);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvApplycable);
            recyclerView.setNestedScrollingEnabled(false);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getBaseContext(), 1);
            recyclerView.setLayoutManager(mLayoutManager);
            adapter = new ApplicableAdapter(getBaseContext(), applyList);
            recyclerView.setAdapter(adapter);
        }
    }
}
