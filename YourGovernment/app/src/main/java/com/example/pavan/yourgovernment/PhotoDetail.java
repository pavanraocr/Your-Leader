package com.example.pavan.yourgovernment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.util.Linkify;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoDetail extends AppCompatActivity {
    private Officials officialsDetails;

    private TextView tv_location;
    private TextView tv_office;
    private TextView tv_official_name;
    private ConstraintLayout completeView;

    private ImageView iv_official_photo;

    /**
     * Checks whether the network is active at the current moment
     *
     * @return network availability status
     */
    private boolean checkForNetworkConnectivity() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        officialsDetails = new Officials();

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        officialsDetails = (Officials) bundle.getSerializable(getString(R.string.detailObj));
        initializeViewRef();

        populateView();

        setViewProperty();
    }

    /**
     * This sets the background color based on the party
     */
    private void setViewProperty() {
        if(officialsDetails.getParty().equals(getString(R.string.republican)))
                completeView.setBackgroundColor(Color.RED);
        else if(officialsDetails.getParty().equals(getString(R.string.democrate)))
            completeView.setBackgroundColor(Color.BLUE);
        else
            completeView.setBackgroundColor(Color.BLACK);
    }

    /**
     * This initializes the variables with the view elements
     */
    private void initializeViewRef() {
        completeView = (ConstraintLayout) findViewById(R.id.ll_pd_official_detail);
        tv_location = (TextView) findViewById(R.id.tv_pd_location);
        tv_office = (TextView) findViewById(R.id.tv_pd_office);
        tv_official_name = (TextView) findViewById(R.id.tv_pd_official_name);
        iv_official_photo = (ImageView) findViewById(R.id.iv_pd_official);
    }

    /**
     * Initializes the data about a particular official
     */
    private void populateView() {
        if(officialsDetails == null){
            return;
        }

        if(!LocationData.getDataAddress().isEmpty()){
            tv_location.setText(LocationData.getDataAddress());
        }

        if(!officialsDetails.getOffice().isEmpty()){
            tv_office.setText(officialsDetails.getOffice());
        }

        if(!officialsDetails.getName().isEmpty())
            tv_official_name.setText(officialsDetails.getName());

        loadOfficialsPhoto();
    }

    /**
     * This loads the data from the internet using an URL
     */
    private void loadOfficialsPhoto() {
        //check for internet connectivity
        if(!checkForNetworkConnectivity()){
            Picasso.with(this).load(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(iv_official_photo);
            return;
        }

        if (!officialsDetails.getPhotoURL().isEmpty()) {
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    // Here we try https if the http image attempt failed
                    final String changedUrl = officialsDetails.getPhotoURL().replace("http:", "https:");
                    picasso.load(changedUrl)
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(iv_official_photo);
                }
            }).build();
            picasso.load(officialsDetails.getPhotoURL())
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(iv_official_photo);
        } else {
            Picasso.with(this).load(R.drawable.missingimage)
                    .placeholder(R.drawable.placeholder)
                    .into(iv_official_photo);
        }
    }
}
