package com.usupov.autopark.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.usupov.autopark.R;
import com.usupov.autopark.activity.CarFoundActivity;
import com.usupov.autopark.activity.RecognizerSampleActivity;
import com.usupov.autopark.json.Car;
import com.usupov.autopark.json.CarCat;
import com.usupov.autopark.model.CarModel;
import com.usupov.autopark.model.CatalogBrand;
import com.usupov.autopark.model.CatalogModel;
import com.usupov.autopark.model.CatalogYear;
import com.usupov.autopark.service.SpeachRecogn;

import java.util.ArrayList;
import java.util.List;


public class CarNewFragment extends Fragment {

    protected static final int RESULT_SPEECH = 1;
    protected static final int REQUEST_ADD = 2;
    TextView tvVinError;
    private EditText vinEditText;
    private Button newCarFindBtn;

    private int selectedBrand = -1;
    private int selectedModel = -1;
    private int selectedYear = -1;
    private int selectedBrandListId = -1;
    private int selectedModelLisId = -1;
    private int selectedYearListId = -1;

    View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        newCarFindBtn = (Button)getActivity().findViewById(R.id.new_car_find_btn);


        tvVinError = (TextView) getActivity().findViewById(R.id.tvVinError);
        tvVinError.setTextColor(getResources().getColor(R.color.colorAccent));

        initVoiceBtn();
        initVinEdittext();
        initCatalogSelect();
        newCarFindBtn.setOnClickListener(catalogFindBtnClick);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_car_new, container, false);
        return rootView;
    }

    private View.OnClickListener catalogFindBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CarModel car = Car.getCarByCatalog(selectedBrand, selectedModel, selectedYear, getActivity().getApplicationContext());
            if (car==null)
                Toast.makeText(getActivity(), getString(R.string.by_catalog_not_found), Toast.LENGTH_LONG).show();
            else {
                Intent intent = new Intent(getActivity(), CarFoundActivity.class);
                Gson g = new Gson();
                intent.putExtra("car", g.toJson(car));
                startActivityForResult(intent, REQUEST_ADD);
            }
        }
    };

    public void initVinEdittext() {
        vinEditText = (EditText)getView().findViewById(R.id.edittext_vin_number);
        vinEditText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        vinEditText.setBackgroundResource(R.drawable.vin_right_border);

        vinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (vinEditText.getText().length()==17) {
                    String vin = vinEditText.getText()+"";
                    CarModel car = Car.getCarByVin(vin, getActivity().getApplicationContext());
                    if (car==null) {
                        tvVinError.setText(getString(R.string.error_vin));
                        vinEditText.setBackgroundResource(R.drawable.vin_error_border);
                    }
                    else {
                        tvVinError.setText("");
                        vinEditText.setBackgroundResource(R.drawable.vin_right_border);
                        Intent intent = new Intent(getActivity(), CarFoundActivity.class);
                        Gson g = new Gson();
                        intent.putExtra("car", g.toJson(car));
                        startActivityForResult(intent, REQUEST_ADD);
                    }
                }
                else if (vinEditText.getText().length() > 17) {
                    vinEditText.setText(vinEditText.getText().toString().substring(0, 17));
                }
                else {
                    if (vinEditText.getText()==null || vinEditText.getText().length()==0)
                        vinEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_voice_black, 0);
                    else
                        vinEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_close, 0);
                    tvVinError.setText("");
                    vinEditText.setBackgroundResource(R.drawable.vin_right_border);
                }
            }
        });
    }

    private void initVoiceBtn() {

        final EditText edt = (EditText) getView().findViewById(R.id.edittext_vin_number);
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
                            Intent intent = new Intent(getActivity(), RecognizerSampleActivity.class);
                            startActivityForResult(intent, RESULT_SPEECH);
                        }
                        else {
                            vinEditText.setText("");
                        }
                        return true;
                    }
//                    else {
//                        openKeyboard(vinEditText);
//                        return true;
//                    }
                }
                return false;
            }
        });

    }
    //3VWBB61C4WM050210
//45RT78WEDST12
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    ArrayList<String> text = data
                            .getStringArrayListExtra("all_results");
                    EditText edt = (EditText) getActivity().findViewById(R.id.edittext_vin_number);
                    String edt_text = SpeachRecogn.vinSpeach(text, getActivity()).toUpperCase();
                    if (edt_text.length() > 17)
                        edt_text = edt_text.substring(0, 17);
                    edt.setText(edt_text);
                }
                break;
            }

        }
    }


    private void initCatalogSelect() {
        LinearLayout linearLayoutBrand = (LinearLayout) rootView.findViewById(R.id.brand);

        linearLayoutBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final List<CatalogBrand> brandList = CarCat.getBradList(getActivity().getApplicationContext());
                if (brandList==null) {
                    Toast.makeText(getActivity(), getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                    return;
                }

                final AlertDialog.Builder builderBrand = new AlertDialog.Builder(getActivity());
                builderBrand.setTitle(getString(R.string.select_brand));

                String[]brandNames = new String[brandList.size()];
                for (int i = 0; i < brandNames.length; i++) {
                    brandNames[i] = brandList.get(i).getName();
                }
                builderBrand.setSingleChoiceItems(brandNames, selectedBrandListId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedBrand = brandList.get(which).getId();
                        String brandName =  brandList.get(which).getName();
                        TextView tvBrandName = (TextView)getView().findViewById(R.id.selectedBranName);
                        tvBrandName.setTextColor(Color.BLACK);
                        tvBrandName.setText(brandName);

                        TextView tvModelName = (TextView)rootView.findViewById(R.id.selectedModelName);
                        tvModelName.setText(getString(R.string.select_model));
                        tvModelName.setTextColor(Color.GRAY);

                        TextView tvYearName = (TextView)rootView.findViewById(R.id.selectedYear);
                        tvYearName.setText(getString(R.string.select_year));
                        tvYearName.setTextColor(Color.GRAY);

                        selectedModel = -1;
                        selectedYear =  -1;
                        selectedBrandListId = which;
                        newCarFindBtn.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });
                builderBrand.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderBrand.show();
            }
        });
        LinearLayout linearLayoutModel = (LinearLayout) rootView.findViewById(R.id.model);
        linearLayoutModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedBrand==-1)
                    return;
                final List<CatalogModel> modelList = CarCat.getModels(selectedBrand, getActivity().getApplicationContext());
                if (modelList==null) {
                    Toast.makeText(getActivity(), getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                    return;
                }
                final AlertDialog.Builder builderModel = new AlertDialog.Builder(getActivity());
                builderModel.setTitle(getString(R.string.select_model));

                String[]modelNames = new String[modelList.size()];
                for (int i = 0; i < modelNames.length; i++) {
                    modelNames[i] = modelList.get(i).getName();
                }
                builderModel.setSingleChoiceItems(modelNames, selectedModelLisId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedModel = modelList.get(which).getId();
                        String brandName =  modelList.get(which).getName();
                        TextView tvBrandName = (TextView)rootView.findViewById(R.id.selectedModelName);
                        tvBrandName.setTextColor(Color.BLACK);
                        tvBrandName.setText(brandName);

                        TextView tvYearName = (TextView)rootView.findViewById(R.id.selectedYear);
                        tvYearName.setText(getString(R.string.select_year));
                        tvYearName.setTextColor(Color.GRAY);
                        selectedYear =  -1;
                        selectedModelLisId = which;
                        newCarFindBtn.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });
                builderModel.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderModel.show();
            }
        });
        LinearLayout linearLayoutYear = (LinearLayout) rootView.findViewById(R.id.year);
        linearLayoutYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedBrand==-1 || selectedModel==-1)
                    return;
                final List<CatalogYear> yearList = CarCat.getYears(selectedBrand, selectedModel, getActivity().getApplicationContext());
                if (yearList==null) {
                    Toast.makeText(getActivity(), getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                    return;
                }

                final AlertDialog.Builder builderYear = new AlertDialog.Builder(getActivity());
                builderYear.setTitle(getString(R.string.select_year));

                String[]yearNames = new String[yearList.size()];
                for (int i = 0; i < yearNames .length; i++) {
                    yearNames [i] = yearList.get(i).getName();
                }
                builderYear.setSingleChoiceItems(yearNames, selectedYearListId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedYear = yearList.get(which).getId();
                        String yearName =  yearList.get(which).getName();
                        TextView tvYearName = (TextView)rootView.findViewById(R.id.selectedYear);
                        tvYearName.setTextColor(Color.BLACK);
                        tvYearName.setText(yearName);
                        selectedYearListId= which;
                        newCarFindBtn.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });
                builderYear.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderYear.show();
            }
        });
    }
}
