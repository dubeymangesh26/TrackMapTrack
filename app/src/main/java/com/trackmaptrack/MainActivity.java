package com.trackmaptrack;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Button btnShowLocation,btnshowMap,shrelocation;


    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;
    Firebase url;


    DataBaseHelper db;
    private static final  int REQUEST_CODE_PERMISSION =2;
    String mPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;
    GPSTracker gps;
    TextView location;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DataBaseHelper(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnShowLocation = (Button) findViewById(R.id.button);
        btnshowMap = (Button) findViewById(R.id.button2);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
            btnShowLocation.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    gps = new GPSTracker(MainActivity.this);
                    location = (TextView) findViewById(R.id.Location);
                    if (gps.canGetLocation()) {
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();
                        Geocoder geocoder = new Geocoder(getApplicationContext());
                        try {

                            final List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                            String string = addressList.get(0).getFeatureName() + " ,\n ";
                            string += addressList.get(0).getSubLocality() + " ,\n  ";
                            string += addressList.get(0).getLocality() + " , ";
                            string += addressList.get(0).getAdminArea() + " ,\n  ";
                            string += addressList.get(0).getCountryName() + " , ";
                            string += addressList.get(0).getPostalCode();
                            location.setText(string);

                            shrelocation = (Button) findViewById(R.id.shrelocation);
                            shrelocation.setVisibility(View.VISIBLE);
                            btnShowLocation.setVisibility(View.GONE);
                            final String finalString = string;
                            shrelocation.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String uri = String.valueOf(finalString);
                                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                    sharingIntent.setType("text/plain");
                                    String ShareSub = "Here is my location";
                                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, ShareSub);
                                    sharingIntent.putExtra(Intent.EXTRA_TEXT, uri);
                                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                                }

                            });


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }


                }

            });





      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            View header = navigationView.getHeaderView(0);
            TextView profileName = header.findViewById(R.id.profileName);
            TextView email = header.findViewById(R.id.email);
            String s1 = profileName.getText().toString();
            String s2 = email.getText().toString();
            db.profiledata(s1, s2);

            //profileName.setText(userName);
        }

        public void Map (View view){

            Intent intent = new Intent(getApplicationContext(), MyMapActivity.class);
            startActivity(intent);
        }

        @Override
        public void onBackPressed () {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected (MenuItem item){
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.nav_map) {
                Intent intent = new Intent(MainActivity.this, MyMapActivity.class);
                startActivity(intent);

                // Handle the camera action
            } else if (id == R.id.chat) {
                Intent intent = new Intent(MainActivity.this, OnlineListActivity.class);
                startActivity(intent);

            } else if (id == R.id.changes_pass) {
                Intent intent = new Intent(MainActivity.this, ChangePasswardActivity.class);
                startActivity(intent);


            } else if (id == R.id.locationtrack) {
                Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
                startActivity(intent);

            } else if (id == R.id.nav_share) {

            } else if (id == R.id.nav_send) {
                Intent intent = new Intent(MainActivity.this, OnlineListActivity.class);
                startActivity(intent);


            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

    Dialog progressDialog;
    private void showProgressDialog() {
        progressDialog = new Dialog(MainActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.custom_dialog_progress);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        progressDialog.dismiss();

    }
    //sign out method
    public void signOut() {
        auth.signOut();
    }
}
