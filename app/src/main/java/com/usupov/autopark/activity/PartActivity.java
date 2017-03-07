package com.usupov.autopark.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.usupov.autopark.R;
import com.usupov.autopark.http.Config;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.CarCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class PartActivity extends AppCompatActivity {

    static final int REQ_CODE_SPEECH_INPUT = 100;
    private static EditText numberPart;
    private static int carId;

    private static int leftmargin = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part);

        initTollbar();

        String carName = getIntent().getExtras().getString("carName");
        carId = getIntent().getExtras().getInt("carId");

//        Toast.makeText(PartActivity.this, carId+"", Toast.LENGTH_LONG).show();

        TextView carNameText = (TextView) findViewById(R.id.part_car_name);
        carNameText.setText(carName);

//        CarCategory one1 = new CarCategory("Внутренняя отделка");
//        CarCategory one2 = new CarCategory("Двигатель");
//        CarCategory one3 = new CarCategory("Кузов");
//        CarCategory one4 = new CarCategory("Подвеска амортизации");
//        CarCategory one5 = new CarCategory("Подвеска оси/система подвески колес");
//            CarCategory one11 = new CarCategory("Балка моста/подвески оси");
//            CarCategory one12 = new CarCategory("Подвеска поперечного рычага");
//                CarCategory one13 = new CarCategory("Подвеска/крепление ходовой части");
//                CarCategory one14 = new CarCategory("Рычаг (поперечный, диагональный, продольный)");
//                    CarCategory one15 = new CarCategory("Рычаг независимой подвески колеса, подвеска колеса");
//        CarCategory one6 = new CarCategory("Подготовка топливной системы");
//        CarCategory one7 = new CarCategory("Ременный привод");
//        CarCategory one8 = new CarCategory("Рулевое управление");
//        CarCategory one9 = new CarCategory("Система зажигания, накаливания");
//        CarCategory one10 = new CarCategory("Система охлаждения");
//
//        one5.getChildren().add(one11);
//        one5.getChildren().add(one12);
//        one12.getChildren().add(one13);
//        one12.getChildren().add(one14);
//        one14.getChildren().add(one15);
//
//        CarCategory one0 = new CarCategory("");
//        one0.getChildren().add(one1);
//        one0.getChildren().add(one2);
//        one0.getChildren().add(one3);
//        one0.getChildren().add(one4);
//        one0.getChildren().add(one5);
//        one0.getChildren().add(one6);
//        one0.getChildren().add(one7);
//        one0.getChildren().add(one8);
//        one0.getChildren().add(one9);
//        one0.getChildren().add(one10);


        String jSonStringCategory = getJSONStringCategory(carId);
        if (jSonStringCategory==null) {
            Toast.makeText(PartActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        CarCategory one0 = new CarCategory("", 0);
        go(one0, jSonStringCategory);

        LinearLayout lvMain = (LinearLayout) findViewById(R.id.lvMain);
        dfs(lvMain, one0, true);

//        iniSpeak();
    }

    private String getJSONStringCategory (int carId) {
        String url = Config.getUrlCar()+carId+"/"+Config.getpathCategory();
        Toast.makeText(PartActivity.this, url, Toast.LENGTH_LONG).show();
        HttpHandler handler = new HttpHandler();
        String result = handler.ReadHttpResponse(url);
        return result;
    }

    private void go(CarCategory parentCat, String result) {
        try {
            JSONArray array = new JSONArray(result);
            if (array==null || array.length()==0)
                return;
            for (int i = 0; i < array.length(); i++) {
                int id = array.getJSONObject(i).getInt("id");
                String name = array.getJSONObject(i).getString("name");
                String children = array.getJSONObject(i).getString("child");
                CarCategory curCategory = new CarCategory(name, id);
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

//    private void iniSpeak() {
//
//
//        numberPart = (EditText) findViewById(R.id.edittext_part_number);
//
//        numberPart.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                final int DRAWABLE_LEFT = 0;
//                final int DRAWABLE_TOP = 1;
//                final int DRAWABLE_RIGHT = 2;
//                final int DRAWABLE_BOTTOM = 3;
//
//                if(event.getAction() == MotionEvent.ACTION_UP) {
//
//                    if(event.getRawX() >= (numberPart.getRight() - numberPart.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
//                        Intent intent = new Intent(
//                                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
//                        try {
//                            promptSpeechInput();
//                        } catch (ActivityNotFoundException a) {
//                            Toast t = Toast.makeText(getApplicationContext(),
//                                    "Opps! Your device doesn't support Speech to Text",
//                                    Toast.LENGTH_SHORT);
//                            t.show();
//                        }
//
//                        return true;
//
//                    }
//
//                }
//
//                return false;
//            }
//        });
//    }
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

    private void dfs(LinearLayout linLayout, CarCategory item, boolean first) {
        if (!item.hasChildren()) {

        }
        else {

            final LayoutInflater inflater = getLayoutInflater();
            for (final CarCategory child : item.getChildren()) {

                View childView = inflater.inflate(R.layout.row_part, linLayout, false);
                if (first) {
                    ImageView image = (ImageView)childView.findViewById(R.id.partItemImage);
                    image.setImageResource(R.drawable.ic_action_do_photo);
                }

                final TextView text = (TextView)childView.findViewById(R.id.partItemName);
                text.setText(child.getCategoryName());
//                childView.setVisibility(View.VISIBLE);

                linLayout.addView(childView);

                final LinearLayout linearLayoutChildren = new LinearLayout(this);

                LinearLayout.LayoutParams leftMarginParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                leftMarginParams.leftMargin = leftmargin;
                linearLayoutChildren.setVisibility(View.GONE);

                final ImageView arrowImage = (ImageView) childView.findViewById(R.id.partItemArrow);
                if (child.hasChildren())
                    arrowImage.setImageResource(R.drawable.ic_action_arrow_not_opened);

                childView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        child.click();
                        if (!child.hasChildren()) {
                            Intent intent = new Intent(PartActivity.this, PartsInActivity.class);
                            intent.putExtra("carId", carId);
                            intent.putExtra("categoryId", child.getCategoryId());
                            intent.putExtra("categoryName", child.getCategoryName());
                            startActivity(intent);
                            finish();
                        }
                        if (child.isFirstClick()) {
                            linearLayoutChildren.setVisibility(View.VISIBLE);
                            if (child.hasChildren()) {
                                arrowImage.setImageResource(R.drawable.ic_action_arrow_opened);
                                text.setTypeface(text.getTypeface(), Typeface.BOLD);
                            }
                        }
                        else {
                            if (child.hasChildren())
                                arrowImage.setImageResource(R.drawable.ic_action_arrow_not_opened);
                            linearLayoutChildren.setVisibility(View.GONE);
                            text.setTypeface(text.getTypeface(), Typeface.NORMAL);
                        }
//                        Toast.makeText(PartActivity.this, child.getCategoryName()+" touched", Toast.LENGTH_LONG).show();
                    }
                });

                linearLayoutChildren.setOrientation(LinearLayout.VERTICAL);

                linLayout.addView(linearLayoutChildren, leftMarginParams);
                dfs(linearLayoutChildren, child, false);
            }
        }
    }
}
