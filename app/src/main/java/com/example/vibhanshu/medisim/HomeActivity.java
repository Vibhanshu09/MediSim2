/**
 * This is Home activity. For Searching Medicines.
 * Created By Vibhanshu Rai on 24-Jan-2018, 5:30 PM (+5:30 GMT INDIA)
 * Last Modified By Vibhanshu Rai on 25-Jan-2018, 8:53 PM
 */
package com.example.vibhanshu.medisim;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button searchButton;
    RadioGroup searchRadioGroup;
    SearchView searchQuery;
    String mSearchQuery, mSearchType = "Search_by_name";
    NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view_home);
        mNavigationView.setNavigationItemSelectedListener(this);

        searchButton = findViewById(R.id.search_button);
        searchButton.setBackground(getResources().getDrawable(R.drawable.button_disabled));
        searchButton.setEnabled(false);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Checking Internet Access
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {

                    Intent intent = new Intent(HomeActivity.this, SearchResultActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("searchType", mSearchType);
                    bundle.putString("searchQuery", mSearchQuery);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    searchQuery.setQuery("", false);
                }
                else {
                    Toast.makeText(HomeActivity.this,R.string.no_internet,Toast.LENGTH_SHORT).show();
                }
            }
        });

        searchRadioGroup = findViewById(R.id.search_radio_group);
        searchRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.search_by_name:
                        searchQuery.setQueryHint("Enter Medicine Name");
                        Toast.makeText(getApplicationContext(), "By Name", Toast.LENGTH_SHORT).show();
                        mSearchType = "Search_by_name";
                        break;

                    case R.id.search_by_generic:
                        searchQuery.setQueryHint("Enter Generic Name");
                        Toast.makeText(getApplicationContext(), "By Generic", Toast.LENGTH_SHORT).show();
                        mSearchType = "Search_by_generic";
                        break;
                }
            }
        });
        searchQuery = findViewById(R.id.search_query);
        searchQuery.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mSearchQuery = searchQuery.getQuery().toString().trim();

                //Toast.makeText(getApplicationContext(),mSearchQuery,Toast.LENGTH_SHORT).show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.trim().isEmpty() || s.equals(null)) {
                    searchButton.setEnabled(false);
                    searchButton.setBackground(getResources().getDrawable(R.drawable.button_disabled));
                } else {
                    //TODO: feth data
                    mSearchQuery = searchQuery.getQuery().toString().trim();
                    searchButton.setEnabled(true);
                    searchButton.setBackground(getResources().getDrawable(R.drawable.button_enabled));
                }
                return true;
            }
        });
        //Checking User Authentication to hide nev drawer items.
        if (checkAuth()){
            showNavMenuItem();
        }
        else{
            hideNavMenuItem();
        }

    }

    @Override
    protected void onStart() {
        //Checking User Authentication to hide nev drawer items.
        if (checkAuth()){
            showNavMenuItem();
        }
        else{
            hideNavMenuItem();
        }
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (checkAuth()){
            menu.findItem(R.id.action_sign).setTitle("Log out");
        }
        else {
            menu.findItem(R.id.action_sign).setTitle("Log in");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign) {
            if (item.getTitle().equals("Log in")){
                startActivityForResult(new Intent(HomeActivity.this,LoginActivity.class),1);
                return true;
            }
            else {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(HomeActivity.this,"Logged out",Toast.LENGTH_SHORT).show();
                hideNavMenuItem();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.add_medicine:
                startActivity(new Intent(HomeActivity.this, AddMedicineActivity.class));
                break;
            case R.id.update_medicine:
                startActivity(new Intent(HomeActivity.this, UpdateMedicineActivity.class));
                break;
            case R.id.delete_medicine:
                startActivity(new Intent(HomeActivity.this, DeleteMedicineActivity.class));
                break;
            case R.id.nav_feedback:
                //TODO: start activity
                sendFeedback();
                break;
            case R.id.nav_about:
                startActivity(new Intent(HomeActivity.this, AboutActivity.class));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Checking User Authentication
    private Boolean checkAuth(){
        if (FirebaseAuth.getInstance().getCurrentUser() == null) //user is Not logged in
            return false;
        else //user is logged in
            return true;
    }
    //Hiding Medicine and Chemist Navigation menu from Navigation drawer
    //if the user is not authorized.
    private void hideNavMenuItem()
    {
        mNavigationView = (NavigationView) findViewById(R.id.nav_view_home);
        Menu nav_Menu = mNavigationView.getMenu();
        nav_Menu.findItem(R.id.medicine_menu).setVisible(false);
    }
    private void showNavMenuItem(){
        mNavigationView = (NavigationView) findViewById(R.id.nav_view_home);
        Menu nav_Menu = mNavigationView.getMenu();
        nav_Menu.findItem(R.id.medicine_menu).setVisible(true);
    }

    private void sendFeedback(){

        String[] mail = {"reeshanrai@gmail.com"};
        String subject = "Feedback/Question about Medisim";
        String body = "\n\n\n\n------Please Do not remove below content for your better help------\n\nBrand: " + Build.BRAND
                + "\n\nModel: " + Build.MODEL
                + "\n\nAPI: " + String.valueOf(Build.VERSION.SDK_INT)
                + "\n\nManufacturer: " + Build.MANUFACTURER
                + "\n\nDevice: " + Build.DEVICE;

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, mail);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT,body);

        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(emailIntent, "Send Feedback using..."));
        }
    }
}
