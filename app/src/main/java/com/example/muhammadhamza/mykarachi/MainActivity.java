package com.example.muhammadhamza.mykarachi;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    FloatingActionButton fabButton;
    ViewPager viewPager;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    private DatabaseReference myRefChildFeeds , myRefChildUpdates;
    private ArrayList<String> feedsArray = new ArrayList<>();
    private ArrayList<String> updatesArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase References for Feeds and Updates
        myRefChildFeeds=myRef.child("Feeds");
        myRefChildUpdates=myRef.child("UserExperincedUpdates");

        //Populating Initial Trial Feeds
        feedsArray.add("Taiwan’s President Accuses China of Renewed Intimidation - The New York Times");
        feedsArray.add("After ‘The Biggest Loser,’ Their Bodies Fought to Regain Weight - The New York Times");
        feedsArray.add("First, a Mixtape. Then a Romance. - The New York Times");
        feedsArray.add("Calling on Angels While Enduring the Trials of Job - The New York Times");
        feedsArray.add("Weak Federal Powers Could Limit Trump’s Climate-Policy Rollback - The New York Times");
        feedsArray.add("Can Carbon Capture Technology Prosper Under Trump? - The New York Times");
        feedsArray.add("Mar-a-Lago, the Future Winter White House and Home of the Calmer Trump - The New York Times");
        feedsArray.add("How to form healthy habits in your 20s - The New York Times");
        feedsArray.add("Turning Your Vacation Photos Into Works of Art - The New York Times");
        feedsArray.add("As Second Avenue Subway Opens, a Train Delay Ends in (Happy) Tears - The New York Times");
        feedsArray.add("Dylann Roof Himself Rejects Best Defense Against Execution - The New York Times");
        feedsArray.add("Modi’s Cash Ban Brings Pain, but Corruption-Weary India Grits Its Teeth - The New York Times");
        feedsArray.add("Suicide Bombing in Baghdad Kills at Least 36 - The New York Times");
        feedsArray.add("Fecal Pollution Taints Water at Melbourne’s Beaches After Storm - The New York Times");
        feedsArray.add("Malaika slams user who trolled her for 'divorcing rich man'");
        feedsArray.add("Nearly 2,300 CPWD buildings in Delhi unsafe: Union Minister");
        feedsArray.add("House Republicans Fret About Winning Their Health Care Suit - The New York Times");
        feedsArray.add("Istanbul, Donald Trump, Benjamin Netanyahu: Your Morning Briefing - The New York Times");
        feedsArray.add("The Afghan War and the Evolution of Obama - The New York Times");
        feedsArray.add("Airline Pilot, Believed to Be Drunk, Is Pulled From Cockpit in Canada - The New York Times");
        feedsArray.add("Riot by Drug Gangs in Brazil Prison Leaves at Least 56 Dead - The New York Times");
        feedsArray.add("SpaceX Says It’s Ready to Launch Rockets Again - The New York Times");
        feedsArray.add("A ‘World Unto Itself’ in New York Area Yeshivas: Floor Hockey - The New York Times");
        feedsArray.add("Inside Jonathan Lethem’s Oddball Trove - The New York Times");
        feedsArray.add("An Immigrant’s Instinct to Aid Others Collides With Financial Need - The New York Times");
        feedsArray.add("House Republicans, Under Fire, Back Down on Gutting Ethics Office - The New York Times");
        feedsArray.add("With Choice of Trade Negotiator, Trump Prepares to Confront Mexico and China - The New York Times");
        feedsArray.add("Israeli Soldier Who Shot Wounded Palestinian Assailant Is Convicted - The New York Times");
        feedsArray.add("It’s Time to Ignore Advice About Which Stocks to Buy in 2017 - The New York Times");
        feedsArray.add("Megyn Kelly’s Jump to NBC From Fox News Will Test Her, and the Networks - The New York Times");
        feedsArray.add("Anchor Becomes the News as Megyn Kelly Leaves Fox News for NBC - The New York Times");

        //Populating Initial Trial Updates
        updatesArray.add("Uber Granted Licence To Operate In London For 15 Months");
        updatesArray.add("60-yr-old lynched over rumours she was cutting people's hair");
        updatesArray.add("China To Cut Import Tariffs On Soybean");
        updatesArray.add("Illegal Migrant Parents Coming To US Will No Longer Be Prosecuted");
        updatesArray.add("We Don't Play Games With India, China For Political Gains, Says Nepal PM");
        updatesArray.add("California Fire Threatens 600 Buildings After Thousands Evacuated");
        updatesArray.add("Japan Blogger Dies After Being Stabbed Several Times In The Back");
        updatesArray.add("2 Israeli Missiles Strike Near Damascus Airport");
        updatesArray.add("Australia Buys High-Tech Drones To Monitor South China Sea, Pacific");
        updatesArray.add("Sarah Sanders Wasn't Refused Service At This Restaurant. Or That One");
        updatesArray.add("Planes Moving To Boarding Gates Collide In South Korea Airport");
        updatesArray.add("Ex-Athlete Said He Was Going To \"Kill Somebody\", Ran Over Man, 2 Sons");

        //Filling Feeds on Firebase
        for(int i=0; i<feedsArray.size(); i++) {
            myRefChildFeeds.child(String.valueOf(i+1)).setValue(feedsArray.get(i));
        }

        //Filling Updates on Firebase
        for(int i=0; i<updatesArray.size(); i++) {
            myRefChildUpdates.child(String.valueOf(i+1)).setValue(updatesArray.get(i));
        }

        //Code for completely disabling the translucency or any color placed on the status bar and navigation bar
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
        if(isServicesOkay()) {
            //ViewPager Inistantiation
            viewPager = findViewById(R.id.viewpager);
            CustomisedFragmentPageAdapter adapter = new CustomisedFragmentPageAdapter(getSupportFragmentManager(), MainActivity.this);
            viewPager.setAdapter(adapter);

            //TabLayout Instantiation
            TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);

            //FAB button Instantiation
            fabButton = findViewById(R.id.fab);
            fabButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, FabActivity.class));
                }
            });
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)ev.getRawX(), (int)ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean isServicesOkay(){
        Log.d(TAG, "isServicesOkay: Checking Google Services Version.");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS) {
            // Correct version is installed and MAPS request can be made.
            Log.d(TAG, "isServicesOkay: GooglePlay Service is Working.");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //am error occured for different version but it is resolvable.
            Log.d(TAG, "isServicesOkay: An ERROR occured but it can be resolved.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(this, "You can't make MAPS request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}