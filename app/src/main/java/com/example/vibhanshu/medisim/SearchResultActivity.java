package com.example.vibhanshu.medisim;



import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity {

    String receiveSearchType;
    String receiveSearchQuery;
    String resultGenericNameValue;
    TextView resultGenericName;
    ProgressBar searchProgressBar;
    ImageView notFoundImage;
    TextView notFoundText;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mediBrandReference = database.getReference().child("medisim").child("brand");
    DatabaseReference mediGenericReference = database.getReference().child("medisim").child("generic");
    Query dataFindQuery;

    //TextView of medicine Details Dialog
    TextView name, generic, icd, tc, type, company, price, unit, quantity;
    Button mediDetailDialogClose;
    MediBrand resultMediBrand;
    MediGeneric resultMediGeneric;

    Dialog resultDialog;

    ArrayList<MediBrand> mediBrandArrayList = new ArrayList<MediBrand>();
    MediBrandAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        //For Getting Text send by previous activity.
        Intent intent = getIntent();

        //Get all ids to use
        findAllId();

        //resultDialog
        resultDialog = new Dialog(this);

        searchProgressBar.setVisibility(View.VISIBLE);


        //Text to search and type of the search.
        receiveSearchType = intent.getStringExtra("searchType");
        receiveSearchQuery = intent.getStringExtra("searchQuery");

        receiveSearchQuery = receiveSearchQuery.toLowerCase();

        //Setting adapter
        adapter = new MediBrandAdapter(this, mediBrandArrayList);

        listView.setAdapter(adapter);

        if (receiveSearchType.equals("Search_by_name"))
            searchByName();
        else
            searchByGeneric();

        resultDialog.setContentView(R.layout.medicine_detail);
        getIdForDialog();
        mediDetailDialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultDialog.dismiss();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediBrand tempMediBrandData = mediBrandArrayList.get(position);
                getMedicineDetails(tempMediBrandData.getName());
                resultDialog.show();
            }
        });
    }

    private void findAllId(){

        searchProgressBar = (ProgressBar) findViewById(R.id.search_progress_bar);

        //Image and text reference for "Not found" condition.
        notFoundImage = (ImageView) findViewById(R.id.not_found_image);
        notFoundText = (TextView) findViewById(R.id.not_found_text);

        resultGenericName = (TextView) findViewById(R.id.result_generic_name);

        //Find id of listView to show data.
        listView = (ListView) findViewById(R.id.search_result_list);

    }


    //Search By Name
    public void searchByName() {
        mediBrandReference.child(receiveSearchQuery).child("generic").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //Toast.makeText(SearchResultActivity.this,dataSnapshot.getValue(String.class),Toast.LENGTH_SHORT).show();
                    resultGenericNameValue = dataSnapshot.getValue(String.class);
                    resultGenericName.setText(UsableMethods.setFirstLetterCapital(resultGenericNameValue));
                    dataFindQuery = mediBrandReference.orderByChild("generic").equalTo(resultGenericNameValue);
                    dataFindQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                searchProgressBar.setVisibility(View.VISIBLE);
                                //Toast.makeText(SearchResultActivity.this,"Found",Toast.LENGTH_SHORT).show();
                                for (DataSnapshot result : dataSnapshot.getChildren()) {
                                    MediBrand tempp = result.getValue(MediBrand.class);
                                    //Toast.makeText(SearchResultActivity.this,tempp.getName(),Toast.LENGTH_SHORT).show();
                                    mediBrandArrayList.add(tempp);
                                    adapter.notifyDataSetChanged();
                                    searchProgressBar.setVisibility(View.GONE);
                                }
                            } else {
                                searchProgressBar.setVisibility(View.GONE);
                                notFoundImage.setVisibility(View.VISIBLE);
                                notFoundText.setVisibility(View.VISIBLE);
                                Toast.makeText(SearchResultActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    searchProgressBar.setVisibility(View.GONE);
                    notFoundImage.setVisibility(View.VISIBLE);
                    notFoundText.setVisibility(View.VISIBLE);
                    Toast.makeText(SearchResultActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                searchProgressBar.setVisibility(View.GONE);
                Toast.makeText(SearchResultActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Search By Generic
    private void searchByGeneric() {
        resultGenericName.setText(UsableMethods.setFirstLetterCapital(receiveSearchQuery));
        dataFindQuery = mediBrandReference.orderByChild("generic").equalTo(receiveSearchQuery);
        dataFindQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    searchProgressBar.setVisibility(View.VISIBLE);
                    //Toast.makeText(SearchResultActivity.this,"Found",Toast.LENGTH_SHORT).show();
                    for (DataSnapshot result : dataSnapshot.getChildren()) {
                        MediBrand tempp = result.getValue(MediBrand.class);
                        //Toast.makeText(SearchResultActivity.this,tempp.getName(),Toast.LENGTH_SHORT).show();
                        mediBrandArrayList.add(tempp);
                        adapter.notifyDataSetChanged();
                        searchProgressBar.setVisibility(View.GONE);
                    }
                } else {
                    searchProgressBar.setVisibility(View.GONE);
                    notFoundImage.setVisibility(View.VISIBLE);
                    notFoundText.setVisibility(View.VISIBLE);
                    Toast.makeText(SearchResultActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
            
    }

    private void getMedicineDetails(String mediName){
        mediBrandReference.child(mediName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                resultMediBrand = dataSnapshot.getValue(MediBrand.class);
                name.setText(UsableMethods.setFirstLetterCapital(resultMediBrand.getName()));
                generic.setText(UsableMethods.setFirstLetterCapital(resultMediBrand.getGeneric()));
                type.setText(UsableMethods.setFirstLetterCapital(resultMediBrand.getType()));
                company.setText(UsableMethods.setFirstLetterCapital(resultMediBrand.getCompany()));
                price.setText(NumberFormat.getCurrencyInstance().format(resultMediBrand.getPrice()));
                unit.setText(String.valueOf(resultMediBrand.getUnit()));
                quantity.setText(String.valueOf(resultMediBrand.getQuantity()));
                mediGenericReference.child(resultMediBrand.getGeneric()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        resultMediGeneric = dataSnapshot.getValue(MediGeneric.class);
                        icd.setText(UsableMethods.setFirstLetterCapital(resultMediGeneric.getIcd_code()));
                        tc.setText(UsableMethods.setFirstLetterCapital(resultMediGeneric.getT_c()));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getIdForDialog(){

        name = (TextView) resultDialog.findViewById(R.id.name_of_medicine);
        generic = (TextView) resultDialog.findViewById(R.id.generic_of_medicine);
        icd = (TextView) resultDialog.findViewById(R.id.icd_code_of_medicine);
        tc = (TextView) resultDialog.findViewById(R.id.t_c_of_medicine);
        type = (TextView) resultDialog.findViewById(R.id.type_of_medicine);
        company = (TextView) resultDialog.findViewById(R.id.company_of_medicine);
        price = (TextView) resultDialog.findViewById(R.id.price_of_medicine);
        unit = (TextView) resultDialog.findViewById(R.id.unit_of_medicine);
        quantity = (TextView) resultDialog.findViewById(R.id.quantity_of_medicine);

        mediDetailDialogClose = (Button) resultDialog.findViewById(R.id.medi_detail_done);

    }



}
