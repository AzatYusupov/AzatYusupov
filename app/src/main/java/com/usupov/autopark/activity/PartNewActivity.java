package com.usupov.autopark.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import product.card.R;
import com.usupov.autopark.adapter.PartFoundAdapter;
import com.usupov.autopark.config.CategoryRestURIConstants;
import com.usupov.autopark.config.PartRestURIConstants;
import com.usupov.autopark.fragment.RecognizerSampleFragment;
import com.usupov.autopark.http.Headers;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.json.Car;
import com.usupov.autopark.json.Part;
import com.usupov.autopark.model.CarCategory;
import com.usupov.autopark.model.CarModel;
import com.usupov.autopark.model.CategoryPartModel;
import com.usupov.autopark.model.UserPartModel;
import com.usupov.autopark.service.SpeachRecogn;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class PartNewActivity extends BasicActivity implements RecognizerSampleFragment.EditNameDialogListener{

    static final int REQ_CODE_SPEECH_INPUT = 100;
    private static EditText numberPart;
    private static int carId;
    private final int RESULT_SPEECH = 200;
    private final int RESULT_CODE_ADD_PART = 300;

    private static int leftmargin = 20;
    private ProgressBar pbPart;
    private CarCategory one0;
    String carName;
    MyTask mt;
    private LinearLayout linearLayoutCatalog;
    private  LinearLayout listViewParts;
    private EditText editTextArticle;
    private DrawerLayout drawerLayout;
    private TextView articleError;

    private TextView textNext;

    public static int textNextId;

    public static Map<String, UserPartModel> selectedPartsMap;
    private boolean manualInsert;
    List<UserPartModel> partList;
    LinearLayout layoutSpeech;
    List<CarModel> carList;
    private ActionProcessButton addSelectedParts;
    private LinearLayout layoutManual;
    private ImageView microphoneSpeech;
    private ImageView clearBtnImage, voiceBtnImage;
    boolean isWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        partList = new ArrayList<>();
        selectedPartsMap = new HashMap<>();


        if (!getIntent().hasExtra("carName")) {
            LayoutInflater inflater = (LayoutInflater) this.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View contentView = inflater.inflate(R.layout.activity_part_new, null, false);
            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawerLayout.addView(contentView, 0);
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setCheckedItem(R.id.nav_part_add);

            pbPart = (ProgressBar) findViewById(R.id.pbParst);
            pbPart.setVisibility(View.VISIBLE);

            CarListTask carListTask = new CarListTask();
            carListTask.execute();

            carName = "";
            carId = 0;

            addSelectedParts = (ActionProcessButton) findViewById(R.id.addSelectedParts);
            addSelectedParts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PartNewActivity.this, CarListActivity.class);
                    Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(
                            getBaseContext(),android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                    startActivity(intent, bundle);
                    finish();
                }
            });
            manualInsert = true;
            textNext = (TextView)findViewById(R.id.nextText);
            textNextId = R.id.nextText;
        }
        else {
            setContentView(R.layout.activity_part_new);
            carName = getIntent().getExtras().getString("carName");
            carId = getIntent().getExtras().getInt("carId");
            initToolbar();
            textNext = (TextView) findViewById(R.id.nextText1);
            textNextId = R.id.nextText1;
        }

        layoutSpeech = (LinearLayout) findViewById(R.id.layoutSpeech);
        if (!carName.isEmpty())
            layoutSpeech.setVisibility(View.GONE);
        layoutManual = (LinearLayout)findViewById(R.id.layoutManual);
        microphoneSpeech = (ImageView) findViewById(R.id.microphoneSpeech);
        microphoneSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecognizerSampleFragment speechDialog = RecognizerSampleFragment.newInstance(R.string.yandex_speech_article);
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                speechDialog.show(transaction, "dialog");
            }
        });
        textNext.setVisibility(View.INVISIBLE);

        textNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson g = new Gson();
                List<UserPartModel> selectedParts = new ArrayList<>();
                long maxCarId = 0;
                for (UserPartModel part : selectedPartsMap.values()) {
                    maxCarId = Math.max(maxCarId, part.getCarId());
                }
                if (maxCarId==0)
                    return;
                for (UserPartModel part : selectedPartsMap.values()) {
                    if (part.getCarId()==maxCarId)
                        selectedParts.add(part);
                }

                String jsonListPart = g.toJson(selectedParts, new TypeToken<List<UserPartModel>>(){}.getType());
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                                    android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                Intent intent = new Intent(PartNewActivity.this, PartFoundActivity.class);
                UserPartModel part = selectedParts.get(0);
                intent.putExtra("parts", jsonListPart);
                intent.putExtra("car_full_name", CarModel.getFullName(part.getBrandName(), part.getModelName(), part.getYearName()));
                startActivityForResult(intent, RESULT_CODE_ADD_PART, bundle);

            }
        });

        articleError = (TextView) findViewById(R.id.errorArticle);
        articleError.setTextColor(getResources().getColor(R.color.squarecamera__red));
        initArticleEditText();
        initVoiceBtn();
        linearLayoutCatalog = (LinearLayout) findViewById(R.id.lvMain);
        listViewParts = (LinearLayout) findViewById(R.id.lvParts);


        one0 = new CarCategory("", 0, 0);
        TextView carNameText = (TextView) findViewById(R.id.part_car_name);
        carNameText.setText(carName);
        pbPart = (ProgressBar) findViewById(R.id.pbParst);
        pbPart.setVisibility(View.GONE);
        if (!manualInsert) {
            loadCateGoryVolleyTask();
        }

    }

    @Override
    public void onFinishEditDialog(String resultTextSpeech, String word) {
        if (resultTextSpeech.length() > 20)
            resultTextSpeech = resultTextSpeech.substring(0, 20);
        if (!word.isEmpty()) {
            editTextArticle.setText(word);
            isWord = true;
        }
        else {
            editTextArticle.setText(resultTextSpeech);
            isWord = false;
        }
    }

    class CarListTask extends  AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            carList = Car.getCarList(getApplicationContext());
            if (carList==null)
                return false;
            return true;
        }

        @Override
        protected void onPostExecute(Boolean ok) {
            super.onPostExecute(ok);
            if (!ok || carList.isEmpty()) {
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                startActivity(new Intent(PartNewActivity.this, CarListActivity.class), bundle);
                finish();
            }
            else
                pbPart.setVisibility(View.GONE);
        }
    }

    private void loadCateGoryVolleyTask() {

        String url = String.format(CategoryRestURIConstants.GET_TREE, carId);
        pbPart.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        go(one0, response);
                        pbPart.setVisibility(View.GONE);
                        LinearLayout lvMain = (LinearLayout) findViewById(R.id.lvMain);
                        one0.setLinearLayout(lvMain);
                        dfs(one0, true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null && (error.networkResponse.statusCode== HttpStatus.SC_UNAUTHORIZED || error.networkResponse.statusCode==HttpStatus.SC_INTERNAL_SERVER_ERROR)) {
                            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                                    android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                            Intent intent = new Intent(PartNewActivity.this, LoginActivity.class);
                            intent.putExtra("unauthorized", true);
                            startActivity(intent, bundle);
                            finishAffinity();
                        }
                        else
                            Toast.makeText(PartNewActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Headers.headerMap(getApplicationContext());
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    class MyTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String jSonStringCategory = getJSONStringCategory(carId);
            if (jSonStringCategory==null)
                return false;
            go(one0, jSonStringCategory);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean ok) {
            super.onPostExecute(ok);
            pbPart.setVisibility(View.GONE);
            if (!ok) {
                Toast.makeText(PartNewActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                return;
            }
            LinearLayout lvMain = (LinearLayout) findViewById(R.id.lvMain);
            one0.setLinearLayout(lvMain);
            dfs(one0, true);
        }
    }

    public void initArticleEditText() {
        editTextArticle = (EditText)findViewById(R.id.edittext_article_number);
//        editTextArticle.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
//        if (!manualInsert)
//            editTextArticle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_voice_black, 0);

        editTextArticle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            Timer timer = new Timer();
            final long DELAY = 500;

            @Override
            public void afterTextChanged(Editable s) {

                timer.cancel();
                timer = new Timer();

                timer.schedule(new MyTimerTask(), DELAY);
            }
        });
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showResult();
                }
            });
        }
    }

    private void showResult() {
        articleError.setText("");
        if (editTextArticle.getText()==null || editTextArticle.getText().length()==0) {
            editTextArticle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            clearBtnImage.setVisibility(View.GONE);
            if (carName.isEmpty())
                layoutSpeech.setVisibility(View.VISIBLE);
        }
        else {
            clearBtnImage.setVisibility(View.VISIBLE);
            layoutSpeech.setVisibility(View.GONE);
        }
        final String article = editTextArticle.getText()+"";
        int cntDigits = 0;
        for (int i = 0; i < article.length(); i++) {
            if (Character.isDigit(article.charAt(i)))
                cntDigits++;
        }
        if (cntDigits < article.length() / 2)
            isWord = true;
        else
            isWord = false;

        if (manualInsert) {
            if (article.isEmpty()) {
                layoutManual.setVisibility(View.GONE);
            }
            else {
                layoutManual.setVisibility(View.VISIBLE);
            }

        }

        String url = String.format(PartRestURIConstants.SEARCH, carId);
        if (isWord)
            url = String.format(PartRestURIConstants.SEARCH_BY_NAME, carId);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson g = new Gson();
                        List<UserPartModel> startsWithParts = g.fromJson(response, new TypeToken<List<UserPartModel>>(){}.getType());

                        selectedPartsMap.clear();

                        if (startsWithParts != null && startsWithParts.size() > 0) {
                            Collections.sort(startsWithParts);
                            linearLayoutCatalog.setVisibility(View.GONE);
                            listViewParts.setVisibility(View.VISIBLE);
                            listViewParts.removeAllViews();
                            LayoutInflater inflater = getLayoutInflater();

                            long lastCarId = 0;

                            if (startsWithParts.size() > 0)
                                lastCarId = startsWithParts.get(0).getCarId();
                            int begin = 0;
                            for (int i = 1; i <= startsWithParts.size(); i++) {
                                UserPartModel part = startsWithParts.get(Math.min(i, startsWithParts.size()-1));
                                if (i==startsWithParts.size() || part.getCarId() != lastCarId) {
                                    partList = new ArrayList<>();
                                    for (int j = begin; j < i; j++) {
                                        partList.add(startsWithParts.get(j));
                                    }
                                    PartFoundAdapter myAdapter = new PartFoundAdapter(PartNewActivity.this, partList, true);
                                    View partsOneCar = inflater.inflate(R.layout.item_part_new, listViewParts, false);
                                    UserPartModel firstPart = partList.get(0);
                                    ((TextView)partsOneCar.findViewById(R.id.carName)).setText(
                                            CarModel.getFullName(firstPart.getBrandName(), firstPart.getModelName(), firstPart.getYearName()));

                                    RecyclerView rvParts = (RecyclerView) partsOneCar.findViewById(R.id.rvParts);
                                    rvParts.setAdapter(myAdapter);
                                    rvParts.setLayoutManager(new StaggeredGridLayoutManager(1, 1));

                                    listViewParts.addView(partsOneCar);
                                    begin = i;
                                }
                                lastCarId = part.getCarId();
                            }
                        }
                        else {
                            if (!article.isEmpty())
                                articleError.setText(getString(R.string.article_not_found));
                            linearLayoutCatalog.setVisibility(View.VISIBLE);
                            listViewParts.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null && (error.networkResponse.statusCode== HttpStatus.SC_UNAUTHORIZED || error.networkResponse.statusCode==HttpStatus.SC_INTERNAL_SERVER_ERROR)) {
                            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                                    android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                            Intent intent = new Intent(PartNewActivity.this, LoginActivity.class);
                            intent.putExtra("unauthorized", true);
                            startActivity(intent, bundle);
                            finishAffinity();
                        }
                        else {
                            Toast.makeText(PartNewActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Headers.headerMap(getApplicationContext());
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("search", article.trim());
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    public void perform_action(View v) {
        Toast.makeText(PartNewActivity.this, ((TextView)v).getText(), Toast.LENGTH_LONG).show();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_part_new);
        toolbar.setTitle(getString(R.string.app_part_car));

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
    private void initVoiceBtn() {

        clearBtnImage = (ImageView) findViewById(R.id.clearBtnImage);
        clearBtnImage.setVisibility(View.GONE);
        clearBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextArticle.setText("");
            }
        });

        voiceBtnImage = (ImageView) findViewById(R.id.voiceBtnImage);
        voiceBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                microphoneSpeech.callOnClick();
            }
        });

    }
    private String getJSONStringCategory (int carId) {
        String url = String.format(CategoryRestURIConstants.GET_TREE, carId);
        HttpHandler handler = new HttpHandler();
        String result = handler.doHttpGet(url, this).getBodyString();
        return result;
    }

    private void go(CarCategory parentCat, String result) {
        try {
            if (result==null)
                return;
            JSONArray array = new JSONArray(result);
            if (array==null || array.length()==0)
                return;
            for (int i = 0; i < array.length(); i++) {
                int id = array.getJSONObject(i).getInt("id");
                String name = array.getJSONObject(i).getString("name");
                String children = array.getJSONObject(i).getString("child");
                int percent = array.getJSONObject(i).getInt("percent");
                CarCategory curCategory = new CarCategory(name, id, percent);
                go(curCategory, children);
                parentCat.getChildren().add(curCategory);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data
                            .getStringArrayListExtra("all_results");
                    EditText edt = (EditText) findViewById(R.id.edittext_article_number);
                    String edt_text = SpeachRecogn.vinSpeach(text, this).toUpperCase();
                    edt_text = SpeachRecogn.partToNormal(edt_text);
                    if (edt_text.length() > 20)
                        edt_text = edt_text.substring(0, 20);
                    edt.setText(edt_text);
                }
                break;
            }
            case RESULT_CODE_ADD_PART: {
                if (resultCode==RESULT_OK) {
                    finish();
                    startActivity(getIntent());
                }
                break;
            }
        }
    }
    private void dfs(final CarCategory item, boolean first) {
        if (item.isRealised())
            return;

        if (!item.hasChildren()) {

        }
        else {
            final LayoutInflater inflater = getLayoutInflater();
            for (final CarCategory child : item.getChildren()) {
                final View childView = inflater.inflate(R.layout.row_part, item.getLinearLayout(), false);
                TextView image = (TextView) childView.findViewById(R.id.partItemImage);
                if (first) {
//                    ImageView image = (ImageView)childView.findViewById(R.id.partItemImage);
//                    image.setImageResource(R.drawable.ic_action_do_photo);
                    if (child.getPercent() > 0)
                        image.setText(child.getPercent() + "%");
                    else
                        image.setText("");
                }
                else
                    image.setVisibility(View.INVISIBLE);

                final TextView text = (TextView) childView.findViewById(R.id.partItemName);
                text.setText(child.getName());
//                childView.setVisibility(View.VISIBLE);
                item.getLinearLayout().addView(childView);
                child.setView(childView);

                final LinearLayout linearLayoutChild = new LinearLayout(this);
                child.setLinearLayout(linearLayoutChild);

                final LinearLayout.LayoutParams leftMarginParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                leftMarginParams.leftMargin = leftmargin;
                child.getLinearLayout().setVisibility(View.GONE);


                final ImageView arrowImage = (ImageView) childView.findViewById(R.id.partItemArrow);
//                if (child.hasChildren())
                arrowImage.setImageResource(R.drawable.ic_action_arrow_not_opened);


                childView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dfs(child, false);
                        child.click();
                        if (!child.hasChildren()) {

                            if (child.isFirstClick()) {
                                child.getLinearLayout().setVisibility(View.VISIBLE);
                                if (child.getCntClicks()==1) {

                                    String url = String.format(PartRestURIConstants.GET_ALL, carId, child.getId());
                                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    Gson g = new Gson();
                                                    List<CategoryPartModel> partList = g.fromJson(response, new TypeToken<List<CategoryPartModel>>(){}.getType());
                                                    final LinearLayout.LayoutParams leftMarginParams = new LinearLayout.LayoutParams(
                                                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                    leftMarginParams.leftMargin = leftmargin ;

                                                    for (final CategoryPartModel c : partList) {

                                                        View view = inflater.inflate(R.layout.row_part, child.getLinearLayout(), false);
                                                        TextView text = (TextView) view.findViewById(R.id.partItemName);
                                                        text.setText(c.getTitle());
                                                        TextView image = (TextView) view.findViewById(R.id.partItemImage);
                                                        image.setVisibility(View.INVISIBLE);
                                                        child.getLinearLayout().addView(view, leftMarginParams);

                                                        text.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Intent intent = new Intent(PartNewActivity.this, PartFoundActivity.class);
                                                                UserPartModel part = new UserPartModel();
                                                                part.setCarId(carId);
                                                                part.setCategoryId(child.getId());
                                                                part.setTitle(c.getTitle());
                                                                part.setId(c.getId());
                                                                Gson g = new Gson();
                                                                List<UserPartModel> listPart = new ArrayList<>();
                                                                listPart.add(part);
                                                                intent.putExtra("parts", g.toJson(listPart, new TypeToken<List<UserPartModel>>(){}.getType()));
                                                                intent.putExtra("car_full_name", carName);
                                                                startActivityForResult(intent, RESULT_CODE_ADD_PART);
                                                            }
                                                        });
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    if (error.networkResponse != null && (error.networkResponse.statusCode== HttpStatus.SC_UNAUTHORIZED || error.networkResponse.statusCode==HttpStatus.SC_INTERNAL_SERVER_ERROR)) {
                                                        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                                                                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                                                        Intent intent = new Intent(PartNewActivity.this, LoginActivity.class);
                                                        intent.putExtra("unauthorized", true);
                                                        startActivity(intent, bundle);
                                                        finishAffinity();
                                                    }
                                                    else
                                                        Toast.makeText(PartNewActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
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
                            }
                            else {
                                child.getLinearLayout().setVisibility(View.GONE);
                            }
                        }
                        if (child.isFirstClick()) {
                            child.getLinearLayout().setVisibility(View.VISIBLE);
//                            if (child.hasChildren()) {
                                //To do ic_action_arrow_opened
                                arrowImage.setImageResource(R.drawable.ic_action_arrow_opened);
                                text.setTypeface(null, Typeface.BOLD);
//                            }
                        }
                        else {
//                            if (child.hasChildren())
                                arrowImage.setImageResource(R.drawable.ic_action_arrow_not_opened);
                            child.getLinearLayout().setVisibility(View.GONE);
                            text.setTypeface(null, Typeface.NORMAL);
                        }
                    }
                });
                child.getLinearLayout().setOrientation(LinearLayout.VERTICAL);
                item.getLinearLayout().addView(child.getLinearLayout(), leftMarginParams);
            }
        }
        item.setRealised();
    }
}
