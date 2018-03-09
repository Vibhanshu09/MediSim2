package com.example.vibhanshu.medisim;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeleteMedicineActivity extends AppCompatActivity {

    SearchView deleteQuery;
    String mediName;
    Button search;
    LinearLayout layout,mainLayoutForSnackBar;
    ProgressBar searchProgressBar;
    TextView deleteMedicineName;
    ImageButton deleteButton;
    AlertDialog.Builder confirmationDialog;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mediBrandRef = database.getReference().child("medi_test").child("brand");
    DatabaseReference mediGenericRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_medicine);

        mainLayoutForSnackBar = (LinearLayout) findViewById(R.id.delete_medicine_main_layout);

        deleteQuery = (SearchView) findViewById(R.id.delete_query);
        deleteQuery.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        search = (Button) findViewById(R.id.search_for_delete);
        searchProgressBar = (ProgressBar) findViewById(R.id.search_progress_bar);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {

                    layout.setVisibility(View.GONE);
                    searchProgressBar.setVisibility(View.VISIBLE);

                    mediName = deleteQuery.getQuery().toString().trim().toLowerCase();
                    mediBrandRef.child(mediName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            searchProgressBar.setVisibility(View.GONE);
                            if (dataSnapshot.exists()) {
                                MediBrand tempp = dataSnapshot.getValue(MediBrand.class);
                                //Toast.makeText(DeleteMedicineActivity.this,tempp.getGeneric(),Toast.LENGTH_SHORT).show();
                                mediGenericRef = FirebaseDatabase.getInstance().getReference().child("medi_test");
                                mediGenericRef = mediGenericRef.child("generic").child(tempp.getGeneric()).child("brand").child(mediName);

                                deleteMedicineName.setText(UsableMethods.setFirstLetterCapital(mediName));
                                layout.setVisibility(View.VISIBLE);
                            } else {
                                //TODO: Set not found message.
                                Toast.makeText(DeleteMedicineActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    Toast.makeText(DeleteMedicineActivity.this,R.string.no_internet,Toast.LENGTH_SHORT).show();
                }
            }
        });
        search.setEnabled(false);
        search.setBackground(getResources().getDrawable(R.drawable.button_disabled));

        deleteMedicineName = (TextView) findViewById(R.id.delete_medicine_text);
        layout = (LinearLayout) findViewById(R.id.delete_medicine_layout);

        deleteButton = (ImageButton) findViewById(R.id.delete_medicine_button);
        confirmationDialog = new AlertDialog.Builder(DeleteMedicineActivity.this);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(DeleteMedicineActivity.this,"clicked",Toast.LENGTH_SHORT).show();
                confirmationDialog.setTitle("Confirm Delete");
                confirmationDialog.setMessage("Do you really wants to delete?\nThis process can not be reversed!");
                confirmationDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        //Delete the medicine
                        mediGenericRef.removeValue();
                        mediBrandRef.child(mediName).removeValue();
                        deleteQuery.setQuery("",false);
                        deleteMedicineName.setText("");
                        layout.setVisibility(View.GONE);
                        Snackbar.make(mainLayoutForSnackBar,"Medicine Deleted Successfully!",Snackbar.LENGTH_LONG).show();
                    }
                });
                confirmationDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do Nothing
                    }
                });
                confirmationDialog.create();
                confirmationDialog.show();
            }
        });
    }
}
