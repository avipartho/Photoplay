package com.example.user.server;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class Tabbed_introActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    String user_s,host_S,pwd_s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_intro);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogt = new AlertDialog.Builder(Tabbed_introActivity.this);
                View tutorialview = getLayoutInflater().inflate(R.layout.about,null);
                dialogt.setTitle("About");

                dialogt.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                dialogt.setView(tutorialview);
                AlertDialog alertDialogt = dialogt.create();
                alertDialogt.setCancelable(false);
                alertDialogt.setCanceledOnTouchOutside(false);
                alertDialogt.show();

                Button pbutton = alertDialogt.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.CYAN);
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogt = new AlertDialog.Builder(Tabbed_introActivity.this);
                final View tutorialview = getLayoutInflater().inflate(R.layout.connect,null);
                dialogt.setTitle("Connect");


                dialogt.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText user = tutorialview.findViewById(R.id.user);
                        EditText host = tutorialview.findViewById(R.id.host);
                        EditText pwd = tutorialview.findViewById(R.id.password);
                        user_s = user.getText().toString();
                        host_S = host.getText().toString();
                        pwd_s = pwd.getText().toString();
                        Global g = Global.getInstance();
                        g.setData(user_s,host_S,pwd_s);
                        dialogInterface.dismiss();
                    }
                });

                dialogt.setView(tutorialview);
                AlertDialog alertDialogt = dialogt.create();
                alertDialogt.setCancelable(false);
                alertDialogt.setCanceledOnTouchOutside(false);
                alertDialogt.show();

                Button pbutton = alertDialogt.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.CYAN);


            }
        });

    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.play_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.learn_icon);
        tabLayout.getTabAt(2).setIcon(R.drawable.assist_icon);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0:
                    return new Tab1_play();
                case 1:
                    return new Tab2_learn();
                case 2:
                    return new Tab3_assist();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "PLAY";
                case 1:
                    return "LEARN";
                case 2:
                    return "ASSIST";
                default:
                    return null;
            }
        }
    }
}
