package com.example.vibhanshu.medisim;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vibhanshu Rai on 26-01-2018.
 */

public class UpdateMedicineActivity extends AppCompatActivity {

    SearchView updateQuery;
    String mediName;
    Button search, update;
    LinearLayout layout, mainLayoutForSnackBar, getMediNameToUpdate;
    ScrollView getMediDataToUpdate;
    ProgressBar searchProgressBar, updateProgressBar;
    TextView updateMedicineName;
    ImageButton updateImageButton;

    //Declare variable for storing values from Edit text fields.
    String name, genericName, icdCode, therapeuticClassification, company, type;
    double price;
    int unit, quantity;

    MediBrand tempBrandData;
    MediGeneric tempGenericData;

    //Update EditText field variable
    EditText mMedicineNameEditText, mGenericNameEditText, mTypeEditText, mICDCodeEditText;
    EditText mCompanyEditText, mPriceEditText, mUnitEditText, mQuantityEditText, mTherapeuticClassification;


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mediBrandRef = database.getReference().child("medisim").child("brand");
    DatabaseReference updateFinalValueRef = database.getReference().child("medisim");
    DatabaseReference mediGenericRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_medicine);

        //getting all ids to use
        findAllIdFromLayout();

        updateQuery.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mediName = query.trim().toLowerCase();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().isEmpty() || newText.trim().equals(null)){
                    search.setEnabled(false);
                    search.setBackground(getResources().getDrawable(R.drawable.button_disabled));
                }
                else {
                    search.setEnabled(true);
                    search.setBackground(getResources().getDrawable(R.drawable.button_enabled));
                }
                return false;
            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);


                if (checkNetworkStatus()) {

                    layout.setVisibility(View.GONE);
                    searchProgressBar.setVisibility(View.VISIBLE);

                    mediName = updateQuery.getQuery().toString().trim().toLowerCase();
                    mediBrandRef = mediBrandRef.child(mediName);
                    mediBrandRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            searchProgressBar.setVisibility(View.GONE);
                            if (dataSnapshot.exists()) {
                                tempBrandData = dataSnapshot.getValue(MediBrand.class);
                                //Toast.makeText(DeleteMedicineActivity.this,tempp.getGeneric(),Toast.LENGTH_SHORT).show();
                                mediGenericRef = updateFinalValueRef.child("generic").child(tempBrandData.getGeneric());

                                mediGenericRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        tempGenericData = dataSnapshot.getValue(MediGeneric.class);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                updateMedicineName.setText(UsableMethods.setFirstLetterCapital(mediName));
                                layout.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(UpdateMedicineActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else{
                    Toast.makeText(UpdateMedicineActivity.this,R.string.no_internet,Toast.LENGTH_SHORT).show();
                }
            }
        });
        search.setEnabled(false);
        search.setBackground(getResources().getDrawable(R.drawable.button_disabled));

        updateImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMediNameToUpdate.setVisibility(View.GONE);
                setValuesToEditText();
                getMediDataToUpdate.setVisibility(View.VISIBLE);

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNetworkStatus()){
                    if(getValuesFromEditText()){
                        enableUpdateProgress();

                        //TODO: UPDATE in database
                        updateDataByMapping();
                        Snackbar.make(mainLayoutForSnackBar,"Data Updated Successfully!",Snackbar.LENGTH_LONG).show();

                        disableUpdateProgress();
                    }

                } else {
                    Toast.makeText(UpdateMedicineActivity.this,R.string.no_internet,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void findAllIdFromLayout(){

        mainLayoutForSnackBar = (LinearLayout) findViewById(R.id.update_medicine_main_layout);
        getMediNameToUpdate = (LinearLayout) findViewById(R.id.get_medi_name_to_update);
        getMediDataToUpdate = (ScrollView) findViewById(R.id.get_medi_data_to_update);

        updateQuery = (SearchView) findViewById(R.id.update_query);

        search = (Button) findViewById(R.id.search_for_update);
        update = (Button) findViewById(R.id.update_medicine_button);

        searchProgressBar = (ProgressBar) findViewById(R.id.search_progress_bar);
        updateProgressBar = (ProgressBar) findViewById(R.id.update_medi_progress_bar);

        updateMedicineName = (TextView) findViewById(R.id.update_medicine_text);
        layout = (LinearLayout) findViewById(R.id.update_medicine_layout);

        //Find id of each editText
        mMedicineNameEditText = (EditText) findViewById(R.id.update_medi_name);
        mGenericNameEditText = (EditText) findViewById(R.id.update_medi_generic);
        mICDCodeEditText = (EditText) findViewById(R.id.update_medi_icd_code);
        mTherapeuticClassification = (EditText) findViewById(R.id.update_medi_t_c);
        mTypeEditText = (EditText) findViewById(R.id.update_medi_type);
        mCompanyEditText = (EditText) findViewById(R.id.update_medi_company);
        mPriceEditText = (EditText) findViewById(R.id.update_medi_price);
        mUnitEditText = (EditText) findViewById(R.id.update_medi_unit);
        mQuantityEditText = (EditText) findViewById(R.id.update_medi_quantity);

        updateImageButton = (ImageButton) findViewById(R.id.select_update_medicine_button);

    }

    private void setValuesToEditText(){
        mMedicineNameEditText.setText(tempBrandData.getName());
        mMedicineNameEditText.setEnabled(false);
        mGenericNameEditText.setText(tempBrandData.getGeneric());
        mGenericNameEditText.setEnabled(false);

        mICDCodeEditText.setText(tempGenericData.getIcd_code());
        mTherapeuticClassification.setText(tempGenericData.getT_c());

        mTypeEditText.setText(tempBrandData.getType());
        mCompanyEditText.setText(tempBrandData.getCompany());
        mPriceEditText.setText(String.valueOf(tempBrandData.getPrice()));
        mUnitEditText.setText(String.valueOf(tempBrandData.getUnit()));
        mQuantityEditText.setText(String.valueOf(tempBrandData.getQuantity()));
    }

    private boolean getValuesFromEditText() {

        //Checking ICD Code
        //TODO:-----------------------------------------------------------------------------------------------------------
        if (mICDCodeEditText.getText().toString().trim().isEmpty()) {
            mICDCodeEditText.setError("Field can't be empty");
            return false;
        } else {
            icdCode = mICDCodeEditText.getText().toString().trim().toLowerCase();
        }
        //Checking Therapeutic Classification
        if (mTherapeuticClassification.getText().toString().trim().isEmpty()) {
            mTherapeuticClassification.setError("Field can't be empty");
            return false;
        } else {
            therapeuticClassification = mTherapeuticClassification.getText().toString().trim().toLowerCase();
        }
        //Checking Type
        if (mTypeEditText.getText().toString().trim().isEmpty()) {
            mTypeEditText.setError("Field can't be empty");
            return false;
        } else {
            tempBrandData.setType(mTypeEditText.getText().toString().trim().toLowerCase());
        }
        //Checking Company Name
        if (mCompanyEditText.getText().toString().trim().isEmpty()) {
            mCompanyEditText.setError("Field can't be empty");
            return false;
        } else {
            tempBrandData.setCompany(mCompanyEditText.getText().toString().trim().toLowerCase());
        }
        //Checking Price field
        if (mPriceEditText.getText().toString().trim().isEmpty()) {
            mPriceEditText.setError("Field can't be empty");
            return false;
        } else {
            tempBrandData.setPrice(Double.parseDouble(mPriceEditText.getText().toString().trim()));
        }
        //Checking unit(power) field
        if (mUnitEditText.getText().toString().trim().isEmpty()) {
            mUnitEditText.setError("Field can't be empty");
            return false;
        } else {
            tempBrandData.setUnit(Integer.parseInt(mUnitEditText.getText().toString().trim()));
        }
        //Checking quantity field
        if (mQuantityEditText.getText().toString().trim().isEmpty()) {
            mQuantityEditText.setError("Field can't be empty");
            return false;
        } else {
            tempBrandData.setQuantity(Integer.parseInt(mQuantityEditText.getText().toString().trim()));
        }

        return true;
    }

    private void updateDataByMapping(){
        Map<String , Object> updateValues = new HashMap<>();
        updateValues.put("/brand/" + tempBrandData.getName(), tempBrandData);
        updateValues.put("/generic/" + tempBrandData.getGeneric() + "/icd_code", icdCode);
        updateValues.put("/generic/" + tempBrandData.getGeneric() + "/t_c", therapeuticClassification);

        updateFinalValueRef.updateChildren(updateValues);
    }

    private boolean checkNetworkStatus(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;


    }

    //Disable Button, Enable ProgressBar
    private void enableUpdateProgress() {
        update.setText("Updating...");
        update.setEnabled(false);
        update.setBackground(getResources().getDrawable(R.drawable.button_disabled));
        updateProgressBar.setVisibility(View.VISIBLE);
    }

    //Enable Button, Disable ProgressBar
    private void disableUpdateProgress() {
        update.setText("Update");
        update.setEnabled(true);
        update.setBackground(getResources().getDrawable(R.drawable.button_enabled));
        updateProgressBar.setVisibility(View.GONE);
    }

}
