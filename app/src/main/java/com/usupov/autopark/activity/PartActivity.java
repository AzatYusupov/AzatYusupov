package com.usupov.autopark.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.usupov.autopark.R;
import com.usupov.autopark.model.CarCategory;

public class PartActivity extends Activity{


    private static int leftmargin = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setActionBar(toolbar);

        String carName = "Ford Focus III, 2012";
        TextView carNameText = (TextView) findViewById(R.id.part_car_name);
        carNameText.setText(carName);

        CarCategory one1 = new CarCategory("Внутренняя отделка");
        CarCategory one2 = new CarCategory("Двигатель");
        CarCategory one3 = new CarCategory("Кузов");
        CarCategory one4 = new CarCategory("Подвеска амортизации");
        CarCategory one5 = new CarCategory("Подвеска оси/система подвески колес");
            CarCategory one11 = new CarCategory("Балка моста/подвески оси");
            CarCategory one12 = new CarCategory("Подвеска поперечного рычага");
                CarCategory one13 = new CarCategory("Подвеска/крепление ходовой части");
                CarCategory one14 = new CarCategory("Рычаг (поперечный, диагональный, продольный)");
                    CarCategory one15 = new CarCategory("Рычаг независимой подвески колеса, подвеска колеса");
        CarCategory one6 = new CarCategory("Подготовка топливной системы");
        CarCategory one7 = new CarCategory("Ременный привод");
        CarCategory one8 = new CarCategory("Рулевое управление");
        CarCategory one9 = new CarCategory("Система зажигания, накаливания");
        CarCategory one10 = new CarCategory("Система охлаждения");

        one5.getChildren().add(one11);
        one5.getChildren().add(one12);
        one12.getChildren().add(one13);
        one12.getChildren().add(one14);
        one14.getChildren().add(one15);

        CarCategory one0 = new CarCategory("");
        one0.getChildren().add(one1);
        one0.getChildren().add(one2);
        one0.getChildren().add(one3);
        one0.getChildren().add(one4);
        one0.getChildren().add(one5);
        one0.getChildren().add(one6);
        one0.getChildren().add(one7);
        one0.getChildren().add(one8);
        one0.getChildren().add(one9);
        one0.getChildren().add(one10);

//        ArrayList<CarCategory> firstList = new ArrayList<>();
//        firstList.add(one1);
//        firstList.add(one2);
//        firstList.add(one3);
//        firstList.add(one4);
//        firstList.add(one5);
//        firstList.add(one6);
//        firstList.add(one7);
//        firstList.add(one8);
//        firstList.add(one9);
//        firstList.add(one10);

//        for (CarCategory item : firstList) {
//            dfs(item);
//        }
        LinearLayout lvMain = (LinearLayout) findViewById(R.id.lvMain);
        dfs(lvMain, one0, true);

//        ExpandableListAdapter adapter = ExpandableListAdapter(this, android.R.layout.simple_expandable_list_item_1);
//        lvParts.setAdapter(adapter);
//        lvParts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(PartActivity.this, position+"", Toast.LENGTH_LONG).show();
//            }
//        });
//        for (int i = 0; i < firstList.size(); i++) {
//            View item = lytinflater.inflate(R.layout.item_part_car, linLayout, false);
//            TextView partnName = (TextView) item.findViewById(R.id.part_name);
//            partnName.setText(firstList.get(i).getCategoryName());
//            partnName.setId(i);
//
//
//            ImageView partImage = (ImageView) item.findViewById(R.id.part_image);
//            partImage.setImageResource(R.drawable.ic_action_do_photo);
//
//            ImageView arrowImage = (ImageView) item.findViewById(R.id.arrow);
//            arrowImage.setImageResource(R.drawable.ic_action_arrow_not_opened);
//
//            linLayout.addView(item);
//
//        }
    }
    private void dfs(LinearLayout linLayout, CarCategory item, boolean first) {
        if (!item.hasChildren()) {

        }
        else {

            LayoutInflater inflater = getLayoutInflater();
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
