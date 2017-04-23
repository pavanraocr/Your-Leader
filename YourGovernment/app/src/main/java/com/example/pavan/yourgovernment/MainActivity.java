package com.example.pavan.yourgovernment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "mainActivity";
    private TextView location;
    private RecyclerView rv;
    private OfficialViewAdapter officialsViewAdapter;
    private List<Officials> officialsList;
    public JsonReaderAndWriter jsonParser;
    private String searchedLocation;
    private DataDownloader downloader;
    private String JSONOutput;
    private Locator locator;


    public void setSearchedLocation(String searchedLocation) {
        LocationData.setDataAddress(searchedLocation);
        location.setText(searchedLocation);
        this.searchedLocation = searchedLocation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        location = (TextView) findViewById(R.id.tv_location);
        rv = (RecyclerView) findViewById(R.id.rv_official_list);
        officialsList = new ArrayList<>();
        officialsViewAdapter = new OfficialViewAdapter(officialsList, this);
        jsonParser = new JsonReaderAndWriter();
        jsonParser.setOfficialsList(officialsList);
        rv.setAdapter(officialsViewAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));


        //check for internet connectivity
        if(!checkForNetworkConnectivity()){
            warnUserAboutNetwork();
            return;
        }

        if(LocationData.getDataAddress().isEmpty()){
            searchedLocation = "";
            locator = new Locator(this);
        }
        else {
            searchedLocation = LocationData.getDataAddress();
        }

        downloadData();
    }

/*    @Override
    protected void onPause() {
        LocationData.setDataAddress(searchedLocation);
        super.onPause();
    }

    @Override
    protected void onResume() {
        searchedLocation = LocationData.getDataAddress();
        super.onResume();
    }*/

    @Override
    protected void onDestroy() {
        if(null != locator)
            locator.shutdown();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult: CALL: " + permissions.length);
        Log.d(TAG, "onRequestPermissionsResult: PERM RESULT RECEIVED");

        if (requestCode == 5) {
            Log.d(TAG, "onRequestPermissionsResult: permissions.length: " + permissions.length);
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermissionsResult: HAS PERM");
                        locator.setUpLocationManager();
                        locator.determineLocation();
                        downloadData();
                    } else {
                        Toast.makeText(this, "Location permission was denied - cannot determine address", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onRequestPermissionsResult: NO PERM");
                    }
                }
            }
        }
        Log.d(TAG, "onRequestPermissionsResult: Exiting onRequestPermissionsResult");
    }

    /**
     * If there was no way by which the current location of the user could be retrieved
     * then the location text view suggests the situation and resets the current list of officials
     */
    public void noLocationAvailable() {
        Toast.makeText(this, getString(R.string.NO_LOC_PROV) +
                getString(R.string.SEARCH_MANUAL_MSG), Toast.LENGTH_LONG).show();

        location.setText(R.string.SEARCH_MANUAL_MSG);

        resetOfficialsList();
    }

    /**
     * Resets the officials data and notifies the adapter
     */
    private void resetOfficialsList() {
        if(officialsList.size() > 0){
            officialsList.clear();
            officialsViewAdapter.notifyDataSetChanged();
        }
    }

    /**
     * initiates the data downloading async task
     */
    private void downloadData() {
        if(searchedLocation.isEmpty() || searchedLocation.equals(getString(R.string.SEARCH_MANUAL_MSG))){
            resetOfficialsList();
            return;
        }

        downloader = new DataDownloader(this, getString(R.string.apiKey), searchedLocation);
        downloader.execute();
    }

    /**
     * This is called from data downloader once the data is downloaded
     */
    public void retrieveData(){
        officialsViewAdapter.notifyDataSetChanged();
        location.setText(LocationData.getDataAddress());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_about:
                Intent infoActivity = new Intent(this, Information.class);
                startActivity(infoActivity);
                break;
            case R.id.menu_location_search:
                createLocationReqPopUp();
                break;
            default:
                Log.d(TAG, "There was an unknown menu selected");
        }
        return true;
    }

    /**
     * Creates a popup for requesting the LocationData from the user
     */
    private void createLocationReqPopUp() {
        //check for internet connectivity
        if(!checkForNetworkConnectivity()){
            warnUserAboutNetwork();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.lbl_ttl_req_loc);
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                searchedLocation = et.getText().toString();

                downloadData();
            }
        });

        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * creates the dialog box that warns the user about the network not being active at the moment
     */
    private void warnUserAboutNetwork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_ttl_No_Network);

        builder.setMessage(R.string.aleart_body_no_network);

        //set the LocationData text to no network
        location.setText(R.string.txt_main_loc_no_network);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

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
    public void onClick(View v) {
        int pos = rv.getChildLayoutPosition(v);
        Officials officialClicked = officialsList.get(pos);


        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.detailObj), officialClicked);

        Intent officialsDetails = new Intent(this, OfficialsDetail.class);

        officialsDetails.putExtras(bundle);

        startActivity(officialsDetails);
    }

    public String getSearchedLocation() {
        return searchedLocation;
    }
}
