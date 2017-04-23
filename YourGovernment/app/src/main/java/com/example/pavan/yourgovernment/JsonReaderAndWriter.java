package com.example.pavan.yourgovernment;

import android.content.Context;
import android.location.Address;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by pavan on 2/23/17.
 */

public class JsonReaderAndWriter {

    private final static String TAG = "jsonReaderAndWriter";
    private List<Officials> listOfOfficials;

    /* Getter and Setters */
    public List<Officials> getOfficialsList() {
        return listOfOfficials;
    }

    public void setOfficialsList(List<Officials> listOfOfficials) {
        this.listOfOfficials = listOfOfficials;
    }

    /**
     * Loads the listOfOfficials from the json
     * @return false if the file descriptor is not null true otherwise
     */
    public boolean loadOfficialsData(Context c){
        InputStream fs;
        String completeJSON;
        listOfOfficials.clear();

        try {
//            fs = c.openFileInput(c.getString(R.string.jsonFileName));
            //TODO Remove from testing
            fs = c.getResources().openRawResource(R.raw.government_data);
        }
//        catch (FileNotFoundException e) {
//            //This is the first time the app has opened since the last time the data of the app was cleared
//            Log.d(TAG, "No JSON file for loading");
//            return true;
//        }
        catch (Exception e){
            e.getStackTrace();
            return false;
        }

        Log.d(TAG, "loading the file from the json file");

        if(fs == null){
            Log.d(TAG, "The inputStream sent into the function is not set\n" );
            return false;
        }

        try{
            BufferedReader rdr = new BufferedReader(new InputStreamReader(fs, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = rdr.readLine()) != null)
            {
                sb.append(line + "\n");
            }

            completeJSON = sb.toString();

            try{
                readAllData(c, completeJSON);
            }finally {
                rdr.close();
            }
        }
        catch(Exception e){
            e.getStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Reads all the OfficialsDetail in the json file
     * @param c - Context of the application
     * @param completeJSON - String that contains the complete JSON object
     * @throws IOException - If the rdr fails to read the fails
     */
    public void readAllData(Context c, String completeJSON){
        Log.d(TAG, "reading All OfficialsDetail");
        listOfOfficials.clear();

        try {
            JSONObject jObj = new JSONObject(completeJSON);

            //extract the LocationData information
            AddressClass newAddress;

            try {
                JSONObject obj = jObj.getJSONObject("normalizedInput");
                newAddress = retrieveAddress(obj);
                LocationData.serializeDataAddress(newAddress);
            }
            catch (JSONException e){
                //There was a key error
                Log.d(TAG, "There was a json key error when trying to find the LocationData data");
                e.printStackTrace();
            }

            //Extract offices information
            try{
                JSONArray arr = jObj.getJSONArray("offices");

                for(int i = 0; i < arr.length(); i++){
                    try{
                        JSONObject officeJSONObj = arr.getJSONObject(i);
                        JSONArray tempArr = officeJSONObj.getJSONArray("officialIndices");

                        for(int j = 0; j < tempArr.length(); j++){
                            int officialsIndex = tempArr.getInt(j);

                            Officials officalItem = new Officials();

                            officalItem.setOffice(officeJSONObj.getString("name"));
                            officalItem.setOfficialsID(officialsIndex);

                            listOfOfficials.add(officalItem);
                        }
                    }
                    catch (JSONException e){
                        Log.d(TAG, "There was some error in parsing the offices JSON structure");
                        e.printStackTrace();
                    }
                }
            }catch (JSONException e){
                //This when the offices has some error
                Log.d(TAG, "There was a json key error when trying to find the offices data");
                e.printStackTrace();
            }

            //Extract Officials information
            try{
                JSONArray officialsArr = jObj.getJSONArray("officials");

                for(Officials item: listOfOfficials){
                    JSONObject officialObj = officialsArr.getJSONObject(item.getOfficialsID());

                    //Setting the name of the official
                    if(officialObj.has("name"))
                        item.setName(officialObj.getString("name"));
                    else
                        item.setName("");

                    //setting the address of the official
                    Address addr = retrieveOfficialAddress(officialObj);
                    if(addr != null)
                        item.setAddress(addr);

                    //setting the party
                    if(officialObj.has("party"))
                        item.setParty(officialObj.getString("party"));
                    else
                        item.setParty("");

                    //setting the phone number
                    if(officialObj.has("phones")){
                        JSONArray tempArr = officialObj.getJSONArray("phones");
                        if(tempArr.length() > 0)
                            item.setPhoneNumber(tempArr.getString(0));
                        else
                            item.setPhoneNumber("");
                    }
                    else
                        item.setPhoneNumber("");

                    //setting the website URLs present for each candidate
                    if(officialObj.has("urls")){
                        JSONArray tempArry = officialObj.getJSONArray("urls");
                        ArrayList<String> urlList = new ArrayList<>();

                        for(int i = 0; i < tempArry.length(); i++)
                            urlList.add(tempArry.getString(i));

                        item.setWebsites(urlList);
                    }
                    else
                        item.setWebsites(new ArrayList<String>());

                    //setting the email addressList
                    if(officialObj.has("emails")){
                        StringBuilder sb = new StringBuilder();
                        JSONArray emailArr = officialObj.getJSONArray("emails");

                        for(int i = 0; i < emailArr.length(); i++){
                            if(sb.length() > 0)
                                sb.append(", ");

                            sb.append(emailArr.getString(i));
                        }
                        item.setEmail(sb.toString());
                    }else
                        item.setEmail("");

                    //Setting the link for the photo
                    if(officialObj.has("photoUrl"))
                        item.setPhotoURL(officialObj.getString("photoUrl"));
                    else
                        item.setPhotoURL("");

                    //setting the social channels in the which the official is available
                    if(officialObj.has("channels")){
                        JSONArray tempArr = officialObj.getJSONArray("channels");

                        for(int i = 0; i < tempArr.length(); i++){
                            JSONObject tempObj = tempArr.getJSONObject(i);

                            if(!tempObj.has("type") || !tempObj.has("id")){
                                continue;
                            }

                            item.appendSocialLinks(tempObj.getString("type"), tempObj.getString("id"));
                        }
                    }
                }
            }
            catch (JSONException e){
                Log.d(TAG, "There was a json key error when trying to find the offices data");
                e.printStackTrace();
            }

        } catch (JSONException e) {
            Log.d(TAG, "The completeJSON read from the HTTP request is not in JSON Format");
            e.printStackTrace();
        }

        Log.d(TAG, "reading all OfficialsDetail done");
    }

    /**
     * Compiles the address obj from the JSON format and returns it
     * @param officialObj
     * @return Address object - which should contain the address of a particular official
     * @throws JSONException
     */
    private Address retrieveOfficialAddress(JSONObject officialObj) throws JSONException {

        Address addr = null;
        StringBuilder sb = new StringBuilder();
        JSONObject addressJSONObj;
        JSONArray tempArr;

        if(officialObj.has("address")){
            tempArr = officialObj.getJSONArray("address");

            if(tempArr.length() > 0)
                addressJSONObj = tempArr.getJSONObject(0);
            else
                return addr;
        }
        else
            return addr;

        if(addressJSONObj.has("line1"))
            sb.append(addressJSONObj.getString("line1"));

        if(addressJSONObj.has("line2")){
            sb.append(", ");
            sb.append(addressJSONObj.getString("line2"));
        }

        if(addressJSONObj.has("line3")){
            sb.append(", ");
            sb.append(addressJSONObj.get("line3"));
        }

        if(addressJSONObj.has("city")){
            sb.append(", ");
            sb.append(addressJSONObj.get("city"));
        }

        if(addressJSONObj.has("state")){
            sb.append(", ");
            sb.append(addressJSONObj.get("state"));
        }

        addr = new Address(Locale.getDefault());

        //now to get the line 1 from the string builder
        addr.setAddressLine(1, sb.toString());

        if(addressJSONObj.has("zip")){
            addr.setPostalCode(addressJSONObj.getString("zip"));
        }

        return addr;
    }

    /**
     * Extracts the information from the JSON Object and creates an object of the address class
     * retruns the same for future use
     * @param obj - JSON Object of the structure {"line1":"","city":"","state":"","zip":""}
     * @return Address
     */
    private AddressClass retrieveAddress(JSONObject obj) {
        AddressClass resultAddress = new AddressClass();

        try{
            resultAddress.setLine1(obj.getString("line1"));
            resultAddress.setCity(obj.getString("city"));
            resultAddress.setState(obj.getString("state"));
            resultAddress.setZip(obj.getString("zip"));
        }
        catch (JSONException e){
            //There is some problem with the keys used in the JSON Object
            Log.d(TAG, "There is some error in parsing the normalizedInput JSON object");
        }

        return resultAddress;
    }
}
