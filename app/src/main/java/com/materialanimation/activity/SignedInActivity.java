package com.materialanimation.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.materialanimation.R;
import com.materialanimation.fragment.DemosFragment;
import com.materialanimation.util.ActivityUtils;


public class SignedInActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private TextView lblUserId;
    private DrawerLayout mDrawerLayout;
    private NavigationView my_navigation_view;
    private ActionBarDrawerToggle mDrawerToggle;
    /*public static Intent createIntent(Context context, IdpResponse idpResponse) {
        Intent startIntent = new Intent();
        if (idpResponse != null) {
            startIntent.putExtra(EXTRA_IDP_RESPONSE, idpResponse);
        }
        return startIntent.setClass(context, SignedInActivity.class);
    }*/
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        my_navigation_view = (NavigationView) findViewById(R.id.my_navigation_view);
        lblUserId = (TextView) my_navigation_view.getHeaderView(0).findViewById(R.id.lblUserId);
        setSupportActionBar(toolbar);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        /*ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);*/
        //getSupportActionBar().setTitle(R.string.app_name);
        /*FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(MainActivity.createIntent(this));
            finish();
            return;
        }*/
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //populateProfile();
        setNavigationDrowerListeners();
        setDemosFragment();
    }

    private void setDemosFragment() {
        DemosFragment demosFragment =
                (DemosFragment) getSupportFragmentManager().findFragmentByTag("DemosFragment");
        if (demosFragment == null) {
            // Create the fragment
            demosFragment = DemosFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), demosFragment, "DemosFragment", R.id.content);
        }
    }

    private void setNavigationDrowerListeners() {
        my_navigation_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                Toast.makeText(SignedInActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                switch (menuItem.getItemId()) {
                    case R.id.nav_item_logout:
                        //logoutApp();
                        return true;
                    /*case R.id.navigation_dashboard:
                        mTextMessage.setText(R.string.title_dashboard);
                        return true;
                    case R.id.navigation_notifications:
                        mTextMessage.setText(R.string.title_notifications);
                        return true;*/
                }
                return true;
            }
        });
    }

    /*private void logoutApp() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        startActivity(new Intent(SignedInActivity.this, MainActivity.class));
                        finish();
                    }
                });
    }*/


    /*private void populateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mTextMessage.setText(
                TextUtils.isEmpty(user.getEmail()) ? "No email" : user.getEmail());
        lblUserId.setText(TextUtils.isEmpty(user.getEmail()) ? "No email" : user.getEmail());
        *//*mUserPhoneNumber.setText(
                TextUtils.isEmpty(user.getPhoneNumber()) ? "No phone number" : user.getPhoneNumber());
        mUserDisplayName.setText(
                TextUtils.isEmpty(user.getDisplayName()) ? "No display name" : user.getDisplayName());*//*
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            /*case R.id.action_settings:
                return true;*/
        }

        return super.onOptionsItemSelected(item);
    }

}
