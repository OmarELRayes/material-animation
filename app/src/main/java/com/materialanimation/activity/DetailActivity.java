package com.materialanimation.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.materialanimation.R;
import com.materialanimation.adapters.DetailActivityAdapter;
import com.materialanimation.app.AppController;
import com.materialanimation.model.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by mitual.sheth on 23-Nov-17.
 */

public class DetailActivity extends AppCompatActivity implements DetailActivityAdapter.ItemClickListener{
    private static final String endpoint = "https://api.myjson.com/bins/14r4sr";
    private static final String EXTRA_ANIMAL_ITEM = "image_url";
    private static final String EXTRA_ANIMAL_IMAGE_TRANSITION_NAME = "image_transition_name";
    private static final String EXTRA__ITEM_NAME = "name";
    private static final String EXTRA_ANIMAL_IMAGE_TRANSITION_NAME2 = "image_transition_name2";
    private RecyclerView recyclerView;
    private ArrayList<Image> images;
    private DetailActivityAdapter mAdapter;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //transparentToolbar();

        supportPostponeEnterTransition();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.excludeTarget(R.id.appBar, true);
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);

            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
        }

        Bundle extras = getIntent().getExtras();
        String animalItem = extras.getString(EXTRA_ANIMAL_ITEM);
        String name = extras.getString(EXTRA__ITEM_NAME);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(name);


        ImageView imageView = (ImageView) findViewById(R.id.animal_detail_image_view);
       /* TextView textView = (TextView) findViewById(R.id.animal_detail_text);
        textView.setText(animalItem);*/

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        images = new ArrayList<>();
        mAdapter = new DetailActivityAdapter(getApplicationContext(), images, this);

        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        String imageUrl = animalItem;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String imageTransitionName = extras.getString(EXTRA_ANIMAL_IMAGE_TRANSITION_NAME);
            imageView.setTransitionName(imageTransitionName);
            String imageTransitionName2 = extras.getString(EXTRA_ANIMAL_IMAGE_TRANSITION_NAME2);
            collapsingToolbar.setTransitionName(imageTransitionName2);
        }
        Glide.with(this).load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .dontAnimate()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        return false;
                    }
                })
                .into(imageView);
        /*Picasso.with(this)
                .load(imageUrl)
                .noFade()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        supportStartPostponedEnterTransition();
                    }

                    @Override
                    public void onError() {
                        supportStartPostponedEnterTransition();
                    }
                });*/
        fetchImages();
    }

    private void fetchImages() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Downloading json...");
        pDialog.show();

        JsonArrayRequest req = new JsonArrayRequest(endpoint,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        pDialog.hide();
                        pDialog.dismiss();
                        pDialog = null;
                        images.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                Image image = new Image();
                                image.setName(object.getString("name"));

                                JSONObject url = object.getJSONObject("url");
                                image.setSmall(url.getString("small"));
                                image.setMedium(url.getString("medium"));
                                image.setLarge(url.getString("large"));
                                image.setTimestamp(object.getString("timestamp"));

                                images.add(image);

                            } catch (JSONException e) {
                                Log.e("SRD", "Json parsing error: " + e.getMessage());
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SRD", "Error: " + error.getMessage());
                pDialog.dismiss();
                pDialog = null;
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }

    private void transparentToolbar() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
    private void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    @Override
    public void onItemClick(int pos, Image imageItem, ImageView shareImageView, TextView shareTextView) {
        Intent intent = new Intent(this, AdvanceDetailsActivity.class);
        intent.putExtra(EXTRA_ANIMAL_ITEM, imageItem.getLarge());
        intent.putExtra(EXTRA__ITEM_NAME, imageItem.getName());
        intent.putExtra(EXTRA_ANIMAL_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(shareImageView));
        intent.putExtra(EXTRA_ANIMAL_IMAGE_TRANSITION_NAME2, ViewCompat.getTransitionName(shareTextView));

        /*ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                shareImageView,
                ViewCompat.getTransitionName(shareImageView));*/
        Pair<View, String> mPair1 = new Pair<View, String>(shareImageView, ViewCompat.getTransitionName(shareImageView));
        Pair<View, String> mPair2 = new Pair<View, String>(shareTextView, ViewCompat.getTransitionName(shareTextView));

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, mPair1, mPair2);

        startActivity(intent, options.toBundle());
    }
}
