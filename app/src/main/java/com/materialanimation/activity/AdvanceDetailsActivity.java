package com.materialanimation.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.materialanimation.R;

/**
 * Created by mitual.sheth on 05-Dec-17.
 */

public class AdvanceDetailsActivity extends AppCompatActivity {

    private static final String EXTRA_ANIMAL_ITEM = "image_url";
    private static final String EXTRA__ITEM_NAME = "name";
    private static final String EXTRA_ANIMAL_IMAGE_TRANSITION_NAME = "image_transition_name";
    private static final String EXTRA_ANIMAL_IMAGE_TRANSITION_NAME2 = "image_transition_name2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_detail);
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

    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }
}
