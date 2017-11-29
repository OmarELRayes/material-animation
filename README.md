# material-animation
Android Material Design Animation 
Implementing Material Design shared element transition using Glide in RecyclerView. This article explain Activity to Activity transition. It will cover RecyclerView with CardView in GridLayout, downloading images using Glide library and CollapsingToolBar.

[youtube https://www.youtube.com/watch?v=atjYMutX1jo&w=560&h=315]

Add following dependencies to your app level build.gradle file

build.gradle

compile 'com.android.support:appcompat-v7:25.3.1'
compile 'com.android.support:support-v4:25.3.1'
compile 'com.android.support.constraint:constraint-layout:1.0.2'
compile 'com.android.support:support-vector-drawable:25.3.1'
compile 'com.android.support:palette-v7:25.3.1'
compile 'com.android.support:design:25.3.1'
compile 'com.android.support:recyclerview-v7:25.3.1'
compile 'com.android.volley:volley:1.0.0'
compile 'com.github.bumptech.glide:glide:3.7.0'
compile 'com.android.support:cardview-v7:25.3.1'

AndroidManifest.xml Make sure you create  tag with parent activity for DetailActivity so that you can see Back Arrow (<-) on DetailActivity.

<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.materialanimation">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:name=".app.AppController"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/MyMaterialTheme">
        <activity android:name=".MainActivity">
        </activity>
        <activity
            android:name=".activity.ImageGridActivity"
            android:label="Image Grid">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <activity
            android:name=".activity.DetailActivity"
            android:label="Detail Activity">
            <meta-data
                android:name="android.support.PARENT_ACTIVITY"
                android:value=".activity.ImageGridActivity" />
        </activity>
    </application>

</manifest>
Create activity_image_grid.xml in res/layout package.

activity_image_grid.xml

<?xml version="1.0" encoding="utf-8"?>
<android.support.design.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true">

    <android.support.design.widget.AppBarLayout
        android:id="@+id/appBar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:theme="@style/AppTheme.AppBarOverlay">

        <android.support.v7.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="?attr/colorPrimary"
            app:popupTheme="@style/AppTheme.PopupOverlay" />

    </android.support.design.widget.AppBarLayout>

    <include layout="@layout/content_main" />

</android.support.design.widget.CoordinatorLayout>
Create activity_main.xml in res/layout package.

content_main.xml

<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:layout_behavior="@string/appbar_scrolling_view_behavior">

    <android.support.v7.widget.RecyclerView
        android:id="@+id/recycler_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:scrollbars="vertical"
        android:layout_marginLeft="2dp"
        android:layout_marginRight="2dp"/>

</RelativeLayout>
Create gallery_thumbnail.xml in res/layout package which contains item for the RecyclerView.

gallery_thumbnail.xml

<?xml version="1.0" encoding="utf-8"?>
<android.support.v7.widget.CardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:card_view="http://schemas.android.com/apk/res-auto"
    android:id="@+id/card_view"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_gravity="center"
    android:layout_margin="3dp"
    android:elevation="3dp"
    card_view:cardCornerRadius="4dp">
    <com.materialanimation.util.SquareLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <ImageView
            android:id="@+id/thumbnail"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:adjustViewBounds="true"
            android:scaleType="centerCrop"/>
        <View
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="@android:color/black"
            android:alpha="0.2"/>
        <TextView
            android:id="@+id/etxtURL"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:singleLine="true"
            android:padding="10dp"
            android:layout_alignParentBottom="true"
            android:textStyle="bold"
            android:textColor="@android:color/white"/>
    </com.materialanimation.util.SquareLayout>
</android.support.v7.widget.CardView>
Here ImageGridActivity.java download the JSON from the sever which is having url for all the images and then all images are downloaded from the url using the glide library from the Adapter for RecyclerView. There is two more options for downloading images from the server either Picasso library or lazy loading using Volly library.

ImageGridActivity.java

public class ImageGridActivity extends AppCompatActivity implements GalleryAdapter.ItemClickListener {
    private static final String endpoint = "https://api.myjson.com/bins/14r4sr";
    private ArrayList images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;
    private static final String EXTRA_ANIMAL_ITEM = "image_url";
    private static final String EXTRA_ANIMAL_IMAGE_TRANSITION_NAME = "image_transition_name";
    private static final String EXTRA_ANIMAL_IMAGE_TRANSITION_NAME2 = "image_transition_name2";
    private static final String EXTRA__ITEM_NAME = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_grid);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.excludeTarget(R.id.appBar, true);
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);

            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getApplicationContext(), images, this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        fetchImages();
    }

    private void fetchImages() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Downloading json...");
        pDialog.show();

        JsonArrayRequest req = new JsonArrayRequest(endpoint,
                new Response.Listener() {
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

    @Override
    public void onItemClick(int pos, Image imageItem, ImageView shareImageView, TextView shareTextView) {
        Intent intent = new Intent(this, DetailActivity.class);
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
Fade Object has been created for changing the default activity transition so that default flash is removed and animation (transition) looks smooth between both the Activities. RecyclerView uses GrideLayout. onItemClick method will called when user click the item of recyclerview and gives the index of that item, model class object (Image) and views which we want to animate from one activity to another activity or we can say from RecyclerView to DetailAcivity.  It is possible to perform transition for multiple views but we must have to create pair with View  and  TransitionName. 

Not : Don't forget to set TtransitionName for the view inside RecyclerView Adapter which will discussed bellow in this post.

GalleryAdapter.java

public class GalleryAdapter extends RecyclerView.Adapter {

    private List images;
    private Context mContext;
    private ItemClickListener onItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public TextView etxtURL;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            etxtURL = (TextView) view.findViewById(R.id.etxtURL);
        }
    }


    public GalleryAdapter(Context context, List images, ItemClickListener onItemClickListener) {
        mContext = context;
        this.images = images;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_thumbnail, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Image image = images.get(position);
        Glide.with(mContext).load(image.getMedium())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbnail);
        ViewCompat.setTransitionName(holder.thumbnail, image.getName());
        ViewCompat.setTransitionName(holder.etxtURL, image.getName()+image.getTimestamp());
        holder.etxtURL.setText(image.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(holder.getAdapterPosition(), image, holder.thumbnail, holder.etxtURL);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public interface ItemClickListener {
        void onItemClick(int pos, Image imageItem, ImageView shareImageView, TextView mTextView);
    }
}
It is very normal implementation of RecyclerView Adapter but only difference is that we must have to set the TransitionName for the views in onBindViewHolder method for Views which we want to animate.

Note : Always use unique TransitionName for all the views.
DetailActivity.java

public class DetailActivity extends AppCompatActivity {
    private static final String EXTRA_ANIMAL_ITEM = "image_url";
    private static final String EXTRA_ANIMAL_IMAGE_TRANSITION_NAME = "image_transition_name";
    private static final String EXTRA__ITEM_NAME = "name";
    private static final String EXTRA_ANIMAL_IMAGE_TRANSITION_NAME2 = "image_transition_name2";

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
        TextView textView = (TextView) findViewById(R.id.animal_detail_text);
        textView.setText(animalItem);

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
                    public boolean onException(Exception e, String model, Target target, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
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
}
First of all supportPostponeEnterTransition() method has been called to tell activity to wait until the image loaded from the server. Once image loaded successfully using Glide or any library supportStartPostponedEnterTransition() method will start the animation. Keep in mind to set the TransitionName for the Views and the TransitionName must be same which set  in the RecyclerView Adapter. All the TransitionName must be unique for each views which want to animated.

getSupportActionBar().setDisplayHomeAsUpEnabled(true) for activity back button <- on the top left corner of the activity for calling parent activity, here in this case the parent activity is ImageGridActivity . 

onSupportNavigateUp() method must be override and must call onBackPressed() otherwise it will recreate the parent activity and again call the oncreate() method in  ImageGridActivity which leads to reload all the images for the grid again and transition also not work.

