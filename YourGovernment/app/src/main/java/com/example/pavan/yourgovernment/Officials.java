package com.example.pavan.yourgovernment;

import android.location.Address;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pavan on 2/20/17.
 */

public class Officials implements Serializable{

    private final static transient String TAG = "Officials";


//    private static String dataAddress = "";

    private String office;
    private String name;
    private String party;
    private String address;
    private String phoneNumber;
    private String email;
    private int officialsID;
    private ArrayList<Website> websites;
    private String photoURL;

    private ArrayList<SocialLinks> socialLinkList;

    public Officials() {
        office = "";
        name = "";
        party = "";
        photoURL = "";
        address = "";
        phoneNumber = "";
        email = "";
        officialsID = -1;
        websites = new ArrayList<>();
        socialLinkList = new ArrayList<>();
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getWebsites() {
        StringBuilder sb = new StringBuilder();

        for(Website web: websites){
            if(!sb.toString().isEmpty())
                sb.append(", ");

            sb.append(web.getWebsiteURL());
        }

        return sb.toString();
    }

    public void setWebsites(ArrayList<String> urls) {
        websites = new ArrayList<>();

        for(String url: urls){
            Website web = new Website(url);
            websites.add(web);
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public int getOfficialsID() {
        return officialsID;
    }

    public void setOfficialsID(int officialsID) {
        this.officialsID = officialsID;
    }

    public String getAddress() {

        return address;
    }

    public void setAddress(Address address) {
        StringBuilder sb = new StringBuilder();

        if(address.getAddressLine(1).isEmpty())
            this.address = "";

        sb.append(address.getAddressLine(1));

        if(address.getPostalCode() != null && !address.getPostalCode().isEmpty()){
            sb.append(", ");
            sb.append(address.getPostalCode());
        }

        this.address = sb.toString();
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<SocialLinks> getSocialLinkList() {
        //TODO convert them into valid URLS
        return socialLinkList;
    }

    public void appendSocialLinks(String type, String id) {
        SocialLinks tempLink = new SocialLinks();
        tempLink.setId(id);
        tempLink.setType(type);

        this.socialLinkList.add(tempLink);
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    /**
     * Inner class to store the information of websites
     */
    public class Website implements Serializable{
        private String websiteURL;

        public Website(String websiteURL) {
            this.websiteURL = websiteURL;
        }

        public Website() {
            websiteURL = "";
        }

        public String getWebsiteURL() {
            return websiteURL;
        }

        public void setWebsiteURL(String websiteURL) {
            this.websiteURL = websiteURL;
        }
    }

    /**
     * Inner class to store the information about social media links
     *
     */
    public class SocialLinks implements Serializable{
        private String type;
        private String id;

        public SocialLinks(String type, String id) {
            this.type = type;
            this.id = id;
        }

        public SocialLinks() {
            type = "";
            id = "";
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
