package com.example.pavan.yourgovernment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class OfficialsDetail extends AppCompatActivity {
    private static final String TAG = "Offical_details";
    private Officials officialsDetails;

    private TextView tv_location;
    private TextView tv_office;
    private TextView tv_official_name;
    private TextView tv_party_name;
    private ImageView iv_official_photo;
    private TextView tv_official_address;
    private TextView tv_official_phone;
    private TextView tv_official_email;
    private TextView tv_official_website;
    private ImageView ib_official_youtube;
    private ImageView ib_official_google;
    private ImageView ib_official_twitter;
    private ImageView ib_official_facebook;
    private ScrollView sv_complete;

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
        setContentView(R.layout.activity_officials);
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
            sv_complete.setBackgroundColor(Color.RED);
        else if(officialsDetails.getParty().equals(getString(R.string.democratic)) ||
                officialsDetails.getParty().equals(getString(R.string.democrate)))
            sv_complete.setBackgroundColor(Color.BLUE);
        else
            sv_complete.setBackgroundColor(Color.BLACK);
    }

    /**
     * This initializes the variables with the view elements
     */
    private void initializeViewRef() {
        sv_complete = (ScrollView) findViewById(R.id.sv_official_information);
        tv_location = (TextView) findViewById(R.id.tv_loc_official);
        tv_office = (TextView) findViewById(R.id.tv_office_name_official);
        tv_official_name = (TextView) findViewById(R.id.tv_official_name_official);
        tv_party_name = (TextView) findViewById(R.id.tv_party);
        iv_official_photo = (ImageView) findViewById(R.id.iv_official_image);
        tv_official_address = (TextView) findViewById(R.id.tv_address_data);
        tv_official_phone = (TextView) findViewById(R.id.tv_phone_data);
        tv_official_email = (TextView) findViewById(R.id.tv_email_data);
        tv_official_website = (TextView) findViewById(R.id.tv_website_data);
        ib_official_youtube = (ImageView) findViewById(R.id.ib_youtube);
        ib_official_google = (ImageView) findViewById(R.id.ib_google_plus);
        ib_official_twitter = (ImageView) findViewById(R.id.ib_twitter);
        ib_official_facebook = (ImageView) findViewById(R.id.ib_facebook);
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

        if(!officialsDetails.getParty().isEmpty())
            tv_party_name.setText(officialsDetails.getParty());
        else
            tv_party_name.setVisibility(View.GONE);

        if(!officialsDetails.getAddress().isEmpty()){
            tv_official_address.setText(officialsDetails.getAddress());
            Linkify.addLinks(tv_official_address, Linkify.MAP_ADDRESSES);
        }

        if(!officialsDetails.getPhoneNumber().isEmpty()){
            tv_official_phone.setText(officialsDetails.getPhoneNumber());
            Linkify.addLinks(tv_official_phone, Linkify.PHONE_NUMBERS);
        }

        if(!officialsDetails.getEmail().isEmpty()){
            tv_official_email.setText(officialsDetails.getEmail());
            Linkify.addLinks(tv_official_email, Linkify.EMAIL_ADDRESSES);
        }

        if(!officialsDetails.getWebsites().isEmpty()){
            tv_official_website.setText(officialsDetails.getWebsites());
            Linkify.addLinks(tv_official_website, Linkify.WEB_URLS);
        }

        loadOfficialsPhoto();
        setVisibilityForSocialLinks();
    }

    /**
     * This loads the links with the urls
     */
    private void setVisibilityForSocialLinks() {
        for(final Officials.SocialLinks socialLinkObj : officialsDetails.getSocialLinkList()){
            if(socialLinkObj.getType().equals(getString(R.string.social_link_type_youtube))) {
                ib_official_youtube.setVisibility(View.VISIBLE);
                ib_official_youtube.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        youTubeClicked(v, socialLinkObj.getId());
                    }
                });
            }else if(socialLinkObj.getType().equals(getString(R.string.social_link_type_google))) {
                ib_official_google.setVisibility(View.VISIBLE);
                ib_official_google.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        googlePlusClicked(v, socialLinkObj.getId());
                    }
                });
            }else if(socialLinkObj.getType().equals(getString(R.string.social_link_type_twitter))) {
                ib_official_twitter.setVisibility(View.VISIBLE);
                ib_official_twitter.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        twitterClicked(v, socialLinkObj.getId());
                    }
                });
            }else if(socialLinkObj.getType().equals(getString(R.string.social_link_type_facebook))) {
                ib_official_facebook.setVisibility(View.VISIBLE);
                ib_official_facebook.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        facebookClicked(v, socialLinkObj.getId());
                    }
                });
            }
            else
                Log.d(TAG, "Invalid social link is present in the json data: \" " + socialLinkObj.getType() + " \"");
        }
    }

    /**
     * Creates an implicit intent to the youtube
     * @param v
     */
    public void youTubeClicked(View v, String name) {
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }

    /**
     * Creates an imlicit intent to googlePlus
     * @param v
     */
    public void googlePlusClicked(View v, String name) {
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://plus.google.com/" + name)));
        }
    }


    /**
     * Creates an implicit intent to twitter
     * @param v
     * @param name
     */
    public void twitterClicked(View v, String name) {
        Intent intent = null;
        try {
        // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
        // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }


    public void facebookClicked(View v, String id) {

        String FACEBOOK_URL = "https://www.facebook.com/" + id;
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + id;
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
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

            iv_official_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.detailObj), officialsDetails);

                    Intent officialsDetails = new Intent(getApplicationContext(), PhotoDetail.class);

                    officialsDetails.putExtras(bundle);

                    startActivity(officialsDetails);
                }
            });
        } else {
            Picasso.with(this).load(R.drawable.missingimage)
                    .placeholder(R.drawable.placeholder)
                    .into(iv_official_photo);
        }
    }
}
