package com.usupov.autopark.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.android.productcard.R;
import com.usupov.autopark.config.UserURIConstants;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.UserModel;

import org.apache.http.HttpStatus;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UpdateActivity extends BasicActivity {

    @InjectView(R.id.update_name)
    EditText nameText;
    @InjectView(R.id.update_nameHint)
    TextView nameHint;
    @InjectView(R.id.update_nameView)
    View nameView;
    @InjectView(R.id.update_nameLayout)
    TextInputLayout nameLayout;

//    @InjectView(R.id.update_company)
//    EditText companyText;
//    @InjectView(R.id.update_companyLayout)
//    TextInputLayout companyLayout;
//
//    @InjectView(R.id.update_inn)
//    EditText innText;
//    @InjectView(R.id.update_address)
//    EditText addressText;
    @InjectView(R.id.btn_update)
    Button btnUpdate;

    private static String name;
//    private static String company;
//    private static String inn;
//    private static String address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_update, null, false);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.addView(contentView, 0);

        ButterKnife.inject(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Gson g = new Gson();
        UserModel user = g.fromJson(getIntent().getExtras().getString("user"), UserModel.class);

        nameText.setText(user.getName());
//        companyText.setText(user.getCompany());
//        innText.setText(user.getInn());
//        addressText.setText(user.getAddress());

        nameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    nameHint.setVisibility(View.VISIBLE);
                    nameView.setVisibility(View.VISIBLE);
                    nameLayout.setErrorEnabled(false);
                }
                else {
                    nameHint.setVisibility(View.GONE);
                    nameView.setVisibility(View.GONE);
                    nameLayout.setErrorEnabled(true);
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

//        BasicActivity.selectedActivityId = BasicActivity.UpdateActivityId;
    }

    public void update() {
        if (!validate()) {
            onIncorrectData(getString(R.string.error_fill_register));
            return;
        }

        btnUpdate.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(UpdateActivity.this,
                R.style.AppCompatAlertDialogStyle);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();

        final HashMap<String, String> pairs = new HashMap<>();

        pairs.put("name", name);
//        pairs.put("company", company);
//        pairs.put("inn", inn);
//        pairs.put("address", address);

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HttpHandler handler = new HttpHandler();
                int status = handler.postWithOneFile(UserURIConstants.USER_UPDATE, pairs, null, getApplicationContext(), false).getStatusCode();

                switch (status) {
                    case HttpStatus.SC_OK :
                        setResult(RESULT_OK);
                        Toast.makeText(UpdateActivity.this, getString(R.string.data_successful_saved), Toast.LENGTH_LONG).show();
                        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getBaseContext(),
                                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                        Intent intent = new Intent(UpdateActivity.this,  CarListActivity.class);
                        startActivity(intent, bundle);
                        finish();
                        break;
                    default:
                        onIncorrectData(getString(R.string.no_internet_connection));
                        break;
                }
                progressDialog.dismiss();
            }
        }, 1000);

    }

    public void onIncorrectData(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

        btnUpdate.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        name = nameText.getText().toString().trim();
//        company = companyText.getText().toString().trim();
//        inn = innText.getText().toString().trim();
//        address = addressText.getText().toString().trim();

        if (name.isEmpty()) {
            nameLayout.setError(getString(R.string.error_name_empty));
            nameHint.setVisibility(View.GONE);
            nameView.setVisibility(View.GONE);
            valid = false;
        }
        else if (name.length() < 3) {
            nameLayout.setError(getString(R.string.error_name));
            nameHint.setVisibility(View.GONE);
            nameView.setVisibility(View.GONE);
            valid = false;
        }
        else
            nameLayout.setError(null);

//        if (company.isEmpty()) {
//            companyLayout.setError(getString(R.string.error_company_empty));
//            valid = false;
//        }
//        else if (company.length() < 2) {
//            companyLayout.setError(getString(R.string.error_company));
//            valid = false;
//        }
//        else
//            companyLayout.setError(null);

        return valid;
    }

}
