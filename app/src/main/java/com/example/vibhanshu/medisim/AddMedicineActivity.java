package com.example.vibhanshu.medisim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Vibhanshu Rai on 26-01-2018.
 */

public class AddMedicineActivity extends AppCompatActivity {

    //Declaring Variables for Layout id, EditText id, Button id and ProgressBar id.
    RelativeLayout layout;
    EditText mMedicineNameEditText, mGenericNameEditText, mTypeEditText, mICDCodeEditText;
    EditText mCompanyEditText, mPriceEditText, mUnitEditText, mQuantityEditText, mTherapeuticClassification;
    EditText mStateEditText;
    Button mAddMedicineButton;
    ProgressBar mAddMediProgressBar;
    //Declare variable for storing values from Edit text fields.
    String name, genericName, icdCode, therapeuticClassification, company, type, state;
    double price;
    String unit, quantity;
    private FirebaseDatabase database;
    private DatabaseReference myBrandRef, myGenericRef, myGenericRefForBrandName;
    private MediBrand mMediDetail;
    private MediGeneric mMediGeneric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        //Finding ID of each edit text fields, button and progress bar.
        findAllIdFromLayout();

        //Get Current Instance of the database.
        database = FirebaseDatabase.getInstance();

        //Get id of the layot for SnackBar
        layout = (RelativeLayout) findViewById(R.id.add_medi_layout);

        //Checking if Generic name is Previously Exists, then retrieve ICD code and T_C;
        mGenericNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    myGenericRef = database.getReference("medisim").child("generic")
                            .child(mGenericNameEditText.getText().toString().trim().toLowerCase());

                    //Checking if particular Generic name existed, if exist then add only mediBrand, else new entry
                    myGenericRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                mMediGeneric = dataSnapshot.getValue(MediGeneric.class);
                                mICDCodeEditText.setText(mMediGeneric.getIcd_code());
                                mICDCodeEditText.setEnabled(false);
                                mTherapeuticClassification.setText(mMediGeneric.getT_c());
                                mTherapeuticClassification.setEnabled(false);
                                mTypeEditText.requestFocus();
                            } else {
                                mICDCodeEditText.setText("");
                                mICDCodeEditText.setEnabled(true);
                                mTherapeuticClassification.setText("");
                                mTherapeuticClassification.setEnabled(true);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

        mAddMedicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check network Connection
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {

                    //Disable Button, Enable ProgressBar
                    enableAdditionProgress();

                    //Storing EditText values in variables if All filds are filled
                    if (getValuesFromEditText()) { //All fields are field

                        //Initializing MediBrand with inserted data
                        mMediDetail = new MediBrand(name, company, genericName, type, price, quantity, unit, state);

                        //Initializing MediGeneric with inserted data
                        mMediGeneric = new MediGeneric(genericName, icdCode, therapeuticClassification, name);

                        //Taking Reference of medibrand from database
                        myBrandRef = database.getReference("medisim").child("brand")
                                .child(name);

                        //Checking if data is previously existed in database, if not then add new one
                        myBrandRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Toast.makeText(AddMedicineActivity.this, "Data Previously Existed", Toast.LENGTH_SHORT)
                                            .show();
                                    disableAdditionProgress();

                                } else {
                                    myBrandRef.setValue(mMediDetail.mapMediBrand());

                                    //Taking Reference of generic from database
                                    myGenericRef = database.getReference("medisim").child("generic")
                                            .child(genericName);

                                    //Checking if particular Generic name existed, if exist then add only mediBrand, else new entry
                                    myGenericRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                myGenericRefForBrandName = myGenericRef.child("brand").child(name);
                                                myGenericRefForBrandName.setValue(true);
                                            } else {
                                                myGenericRef.setValue(mMediGeneric.mapNewGenericDetail());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    disableAdditionProgress();
                                    Snackbar.make(layout, "Added Successfully", Snackbar.LENGTH_LONG)
                                            .show();
                                    clearAllEditTextField();

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    } else { //Some or all fields are not filled.

                        disableAdditionProgress();
                    }
                } else {
                    Toast.makeText(AddMedicineActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Finding ID of each edit text fields, button and progress bar.
    private void findAllIdFromLayout() {
        mMedicineNameEditText = findViewById(R.id.add_medi_name);
        mGenericNameEditText = findViewById(R.id.add_medi_generic);
        mICDCodeEditText = findViewById(R.id.add_medi_icd_code);
        mTherapeuticClassification = findViewById(R.id.add_medi_t_c);
        mTypeEditText = findViewById(R.id.add_medi_type);
        mCompanyEditText = findViewById(R.id.add_medi_company);
        mPriceEditText = findViewById(R.id.add_medi_price);
        mUnitEditText = findViewById(R.id.add_medi_unit);
        mQuantityEditText = findViewById(R.id.add_medi_quantity);
        mStateEditText = findViewById(R.id.add_medi_state);

        mAddMedicineButton = findViewById(R.id.add_new_medicine);

        mAddMediProgressBar = findViewById(R.id.add_medi_progress_bar);

    }

    private boolean getValuesFromEditText() {
        //Checking Medicine Name field
        if (mMedicineNameEditText.getText().toString().trim().isEmpty()) {
            mMedicineNameEditText.setError("Field can't be empty");
            return false;
        } else {
            name = mMedicineNameEditText.getText().toString().trim().toLowerCase();
        }
        //Checking Generic Name
        if (mGenericNameEditText.getText().toString().trim().isEmpty()) {
            mGenericNameEditText.setError("Field can't be empty");
            return false;
        } else {
            genericName = mGenericNameEditText.getText().toString().trim().toLowerCase();
        }
        //Checking ICD Code
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
            type = mTypeEditText.getText().toString().trim().toLowerCase();
        }
        //Checking Company Name
        if (mCompanyEditText.getText().toString().trim().isEmpty()) {
            mCompanyEditText.setError("Field can't be empty");
            return false;
        } else {
            company = mCompanyEditText.getText().toString().trim().toLowerCase();
        }
        //Checking Price field
        if (mPriceEditText.getText().toString().trim().isEmpty()) {
            mPriceEditText.setError("Field can't be empty");
            return false;
        } else {
            price = Double.parseDouble(mPriceEditText.getText().toString().trim());
        }
        //Checking unit(power) field
        if (mUnitEditText.getText().toString().trim().isEmpty()) {
            mUnitEditText.setError("Field can't be empty");
            return false;
        } else {
            unit = mUnitEditText.getText().toString().trim().toLowerCase();
        }
        //Checking quantity field
        if (mQuantityEditText.getText().toString().trim().isEmpty()) {
            mQuantityEditText.setError("Field can't be empty");
            return false;
        } else {
            quantity = mQuantityEditText.getText().toString().trim().toLowerCase();
        }
        //Checking City field
        if(mStateEditText.getText().toString().trim().isEmpty()){
            mStateEditText.setError("Field can't be empty");
            return false;
        } else {
            state = mStateEditText.getText().toString().trim().toLowerCase();
        }

        return true;
    }

    //Disable Button, Enable ProgressBar
    private void enableAdditionProgress() {
        mAddMedicineButton.setText("Adding...");
        mAddMedicineButton.setEnabled(false);
        mAddMedicineButton.setBackground(getResources().getDrawable(R.drawable.button_disabled));
        mAddMediProgressBar.setVisibility(View.VISIBLE);
    }

    //Enable Button, Disable ProgressBar
    private void disableAdditionProgress() {
        mAddMedicineButton.setText("Add");
        mAddMedicineButton.setEnabled(true);
        mAddMedicineButton.setBackground(getResources().getDrawable(R.drawable.button_enabled));
        mAddMediProgressBar.setVisibility(View.GONE);
    }

    //Clear All Edit Text Field for new entry.
    private void clearAllEditTextField() {
        mMedicineNameEditText.setText("");
        mGenericNameEditText.setText("");
        mICDCodeEditText.setText("");
        mTherapeuticClassification.setText("");
        mTypeEditText.setText("");
        mCompanyEditText.setText("");
        mPriceEditText.setText("");
        mUnitEditText.setText("");
        mQuantityEditText.setText("");
        mStateEditText.setText("");
        mMedicineNameEditText.requestFocus();
    }
}
