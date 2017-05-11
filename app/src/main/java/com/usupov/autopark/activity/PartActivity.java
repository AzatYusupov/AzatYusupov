package com.usupov.autopark.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.usupov.autopark.R;
import com.usupov.autopark.http.Config;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.CarCategory;
import com.usupov.autopark.service.SpeachRecogn;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Locale;

import static com.usupov.autopark.activity.CarNewActivity.REQUEST_ADD;
import static com.usupov.autopark.activity.CarNewActivity.RESULT_SPEECH;

public class PartActivity extends AppCompatActivity {

    static final int REQ_CODE_SPEECH_INPUT = 100;
    private static EditText numberPart;
    private static int carId;

    private static int leftmargin = 20;
    private ProgressBar pbPart;
    private FrameLayout part_frame_edit_steret_voicebtn;
    private CarCategory one0;
    Context context;
    String carName;
    MyTask mt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part);

        context = this;
        initTollbar();

        carName = getIntent().getExtras().getString("carName");
        carId = getIntent().getExtras().getInt("carId");

//        Toast.makeText(PartActivity.this, carId+"", Toast.LENGTH_LONG).show();
        one0 = new CarCategory("", 0, 0);
        TextView carNameText = (TextView) findViewById(R.id.part_car_name);
        carNameText.setText(carName);
        pbPart = (ProgressBar) findViewById(R.id.pbParst);
        part_frame_edit_steret_voicebtn = (FrameLayout) findViewById(R.id.part_frame_edit_steret_voicebtn);
        mt = new MyTask();
        mt.execute();
        iniSpeak();
        initVinEdittext();
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
            TextView carNameText = (TextView) findViewById(R.id.part_car_name);
            carNameText.setVisibility(View.VISIBLE);
            part_frame_edit_steret_voicebtn.setVisibility(View.VISIBLE);
            if (!ok) {
                Toast.makeText(PartActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                return;
            }
            LinearLayout lvMain = (LinearLayout) findViewById(R.id.lvMain);
            one0.setLinearLayout(lvMain);
            dfs(one0, true);
        }
    }
    private String getJSONStringCategory (int carId) {
        String url = Config.getUrlCar()+carId+"/"+Config.getpathCategory();
        System.out.println("URLLL="+url);
//        Toast.makeText(PartActivity.this, url, Toast.LENGTH_LONG).show();
        HttpHandler handler = new HttpHandler();
        String result = handler.ReadHttpResponse(url);
        return result;
    }
    public void initVinEdittext()
    {
        numberPart = (EditText) findViewById(R.id.edittext_part_number);
        numberPart.setBackgroundResource(R.drawable.vin_right_border);
        numberPart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (numberPart.getText()==null || numberPart.getText().length()==0)
                    numberPart.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_voice_black, 0);
                else
                    numberPart.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_close, 0);
                numberPart.setBackgroundResource(R.drawable.vin_right_border);
                dfssearch(one0, true, numberPart.getText().toString());
            }
        });
    }

    private void go(CarCategory parentCat, String result) {
        try {
            if (result.equals("null"))
                return;
            JSONArray array = new JSONArray(result);
            if (array== null || array.length()==0)
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

    private void initTollbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_part);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PartActivity.this, MainActivity.class));
                finish();
            }
        });
        toolbar.setTitle(getString(R.string.app_part));

    }

    private void iniSpeak() {
        final EditText edt = (EditText) findViewById(R.id.edittext_part_number);
        edt.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {

                    if(event.getRawX() >= (edt.getRight() - edt.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (edt.getText()==null || edt.getText().length()==0) {
                            Intent intent = new Intent(PartActivity.this, RecognizerSampleActivity.class);
                            startActivityForResult(intent, RESULT_SPEECH);
                        }
                        else {
                            edt.setText("");
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }
    private void promptSpeechInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data
                            .getStringArrayListExtra("all_results");
                    EditText edt = (EditText) findViewById(R.id.edittext_part_number);
                    String edt_text = SpeachRecogn.vinSpeach(text).toUpperCase();
                    if (edt_text.length() > 17)
                        edt_text = edt_text.substring(0, 17);
                    edt.setText(edt_text);
                }
                break;
            }
            case REQUEST_ADD: {
                if (resultCode==RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;
            }
        }
    }
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        switch (requestCode) {
//            case REQ_CODE_SPEECH_INPUT: {
//                if (resultCode == RESULT_OK && null != data) {
//                    ArrayList<String> result = data
//                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    numberPart = (EditText)findViewById(R.id.edittext_part_number);
//                    numberPart.setText(result.get(0));
//                }
//                break;
//            }
//
//        }
//    }

    private void dfs(final CarCategory item, boolean first) {
        if (item.isRealised())
            return;
        item.setRealised();
        if (!item.hasChildren()) {

        }
        else {
            final LayoutInflater inflater = getLayoutInflater();
            for (final CarCategory child : item.getChildren()) {
                View childView = inflater.inflate(R.layout.row_part, item.getLinearLayout(), false);
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
                child.setTextView(text);
                child.getTextView().setText(child.getCategoryName());
//                childView.setVisibility(View.VISIBLE);
                item.getLinearLayout().addView(childView);
                child.setView(childView);

                final LinearLayout linearLayoutChild = new LinearLayout(this);
                child.setLinearLayout(linearLayoutChild);

                LinearLayout.LayoutParams leftMarginParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                leftMarginParams.leftMargin = leftmargin;
                child.getLinearLayout().setVisibility(View.GONE);
                final ImageView arrowImage = (ImageView) childView.findViewById(R.id.partItemArrow);
                child.setOpenandcloseimg(arrowImage);
                if (child.hasChildren())
                    child.getOpenandcloseimg().setImageResource(R.drawable.ic_action_arrow_not_opened);

                childView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        child.click();
                        Log.d("mylogs", "" + child.getCategoryName());
                        if (!child.hasChildren()) {
                            Intent intent = new Intent(PartActivity.this, PartsInActivity.class);
                            intent.putExtra("carId", carId);
                            intent.putExtra("categoryId", child.getCategoryId());
                            intent.putExtra("categoryName", child.getCategoryName());
                            intent.putExtra("car_full_name", carName);
                            startActivity(intent);
                        }
                        if (child.isFirstClick()) {
                            Log.d("mylogs", "1" + child.getCategoryName());
                            child.getLinearLayout().setVisibility(View.VISIBLE);
                            if (child.hasChildren()) {
                                child.getOpenandcloseimg().setImageResource(R.drawable.ic_action_arrow_opened);
                                child.getTextView().setTypeface(null, Typeface.BOLD);
                            }
                        }
                        else {
                            Log.d("mylogs", "2" + child.getCategoryName());
                            if (child.hasChildren())
                                child.getOpenandcloseimg().setImageResource(R.drawable.ic_action_arrow_not_opened);
                            child.getLinearLayout().setVisibility(View.GONE);
                            child.getTextView().setTypeface(null, Typeface.NORMAL);
                        }
//                        Toast.makeText(PartActivity.this, child.getCategoryName()+" touched", Toast.LENGTH_LONG).show();
                    }
                });
                dfs(child, false);
                child.getLinearLayout().setOrientation(LinearLayout.VERTICAL);
                item.getLinearLayout().addView(child.getLinearLayout(), leftMarginParams);
            }
        }
    }
    public boolean dfssearch(final CarCategory item, boolean first, String search) {
        boolean have = false;
        if(!item.hasChildren()) {
            String categoryname = item.getCategoryName();
            categoryname.toLowerCase();
            search.toLowerCase();
            if(search.equals("")) {
                return true;
            }
            have = (categoryname.indexOf(search) != -1);
            return have;
        }
        else {
            item.getLinearLayout().setVisibility(View.VISIBLE);
            if(!first) {
                item.getOpenandcloseimg().setImageResource(R.drawable.ic_action_arrow_opened);
                item.setFirstClick(true);
                item.getTextView().setTypeface(null, Typeface.BOLD);
            }
            for (final CarCategory child : item.getChildren()) {
                if (dfssearch(child, false, search)) {
                    have = true;
                    child.getView().setVisibility(View.VISIBLE);
                }
                else
                    child.getView().setVisibility(View.GONE);
            }
            if(!have) {
                item.getLinearLayout().setVisibility(View.GONE);
                if(!first) {
                    item.getOpenandcloseimg().setImageResource(R.drawable.ic_action_arrow_not_opened);
                    item.click();
                    item.getTextView().setTypeface(null, Typeface.NORMAL);
                }
            }
            if(search.equals("") && !first) {
                item.getLinearLayout().setVisibility(View.GONE);
                item.getOpenandcloseimg().setImageResource(R.drawable.ic_action_arrow_not_opened);
                item.click();
                item.getTextView().setTypeface(null, Typeface.NORMAL);
            }
        }
        return have;
    }
}
