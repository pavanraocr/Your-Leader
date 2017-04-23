package com.example.pavan.yourgovernment;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pavan on 4/16/17.
 */

public class DataDownloader extends AsyncTask<Void, Void, Boolean> {
    private String TAG = "DataDownloader";
    private String apiKey;
    public boolean isAsyncTaskRunning;
    private String searchedLoc;
    private String baseURLForNameSearchAPI = "https://www.googleapis.com/civicinfo/v2/representatives";
    private MainActivity mainAct;
    private JsonReaderAndWriter jsonParser;

    private String jsonString;

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public DataDownloader(MainActivity ma, String APIKey, String str) {
        mainAct = ma;
        apiKey = APIKey;
        searchedLoc = str;
        jsonParser = ma.jsonParser;
    }

    public String getSearchedLoc() {
        return searchedLoc;
    }

    public void setSearchedLoc(String searchedLoc) {
        this.searchedLoc = searchedLoc;
    }

    /**
     * Creates the URL with the apikey and search text
     *
     * @return URL String
     */
    private String createQuerry(){
        Uri.Builder uRLBuilder;

        uRLBuilder = Uri.parse(baseURLForNameSearchAPI).buildUpon();
        uRLBuilder.appendQueryParameter("key", apiKey);
        uRLBuilder.appendQueryParameter("address", searchedLoc);
        return uRLBuilder.build().toString();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        jsonString = "";
    }

    @Override
    protected Boolean doInBackground(Void... param) {
        if(isAsyncTaskRunning){
            Log.d(TAG, "There is already an async task running in the background");
            return false;
        }

        isAsyncTaskRunning = true;
        boolean loadStatus = false;
        loadStatus = downloadData();
        isAsyncTaskRunning = false;
        Log.d(TAG, "async task ended and returning status " + String.valueOf(loadStatus));

        return loadStatus;
    }

    /**
     * Function that downloads the data using the URL and the api key and stores it as a string
     * in jsonFormatDataList
     *
     * @throws IOException if there is reading error
     */
    private boolean downloadData() {
        String url = createQuerry();

        Log.d(TAG, "The url from which the file is being downloaded from: " + url);

        StringBuilder fileContent = new StringBuilder();

        try{
            HttpURLConnection conn = openHTTPConnection(url);
            BufferedReader downloadReader = openConnectionForGet(conn);
            String line;

            while ((line = downloadReader.readLine()) != null){
                fileContent.append(line).append("\n");
            }
        }
        catch(Exception e){
            Log.d(TAG, "ERROR in downloading data check the stack trace");
            e.printStackTrace();
            return false;
        }

        jsonString = fileContent.toString();
        return true;
    }

    /**
     * Function that creates the
     * @param conn
     * @return
     * @throws IOException
     */
    private BufferedReader openConnectionForGet(HttpURLConnection conn) throws IOException {
        BufferedReader reader;
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            Log.d(TAG, "The connection is trying to set a request method which is invalid");
            e.printStackTrace();
            return null;
        }

        InputStream is = (InputStream) conn.getInputStream();
        reader = new BufferedReader((new InputStreamReader(is)));

        return reader;
    }

    /**
     * Creates the connection object and returns that instance with the connection open to the URL sent
     * @param url - URL to which the http connection is open
     * @return the object reference to the open conntection
     * @throws IOException - if the connection is not through then this exception is thrown
     */
    private HttpURLConnection openHTTPConnection(String url) throws IOException {
        URL downLink = null;

        try {
            downLink = new URL(url);
        } catch (MalformedURLException e) {
            Log.d(TAG, "Error in opening the link: \"" + url + "\"");
            e.printStackTrace();
            return null;
        }

        HttpURLConnection conn = (HttpURLConnection) downLink.openConnection();

        return conn;
    }

    @Override
    protected void onPostExecute(Boolean loadStatus) {
        super.onPostExecute(loadStatus);
        jsonParser.readAllData(mainAct.getApplicationContext(), jsonString);
        mainAct.retrieveData();
    }
}
