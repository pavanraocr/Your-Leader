package com.example.pavan.yourgovernment;

/**
 * Created by pavan on 4/16/17.
 */

public class LocationData {
    /**
     * This holds the address of the LocationData which is being searched for
     *
     * Template: "<City>,<State>,<Zip>"
     */
    private static String dataAddress = "";


    public static void setDataAddress(String dataAddress) {
        LocationData.dataAddress = dataAddress;
    }

    public static void serializeDataAddress(AddressClass addr){
        dataAddress = "";

        if(!addr.getCity().isEmpty()){
            dataAddress += addr.getCity();
        }

        if(!addr.getState().isEmpty()){
            if(!dataAddress.isEmpty())
                dataAddress += ", ";

            dataAddress += addr.getState();
        }

        if(!addr.getZip().isEmpty()){
            if(!dataAddress.isEmpty())
                dataAddress += ", ";

            dataAddress += addr.getZip();
        }
    }

    public static String getDataAddress(){
        return dataAddress;
    }

    public static void resetDataAddress(){
        dataAddress = "";
    }
}
