package com.usertaxi.utils;

/**
 * AppConstants to store the constant variables and common tags to be used
 * within the application
 */
public class AppConstants {

    public static final int NETWORK_TIMEOUT_CONSTANT = 15000;
    public static final int NETWORK_CONNECTION_TIMEOUT_CONSTANT = 15000;
    public static final byte ROUTE_ACTIVITY_CONSTANT = (byte) 23;
    public static final int NETWORK_SOCKET_TIMEOUT_CONSTANT = 25000;


    // public static final String APPURL = "http://priyanshpatodi.com/testsite/texiDriverApp/api/customerApi/customer_api.php?";


    public static final String APPURL = "http://www.hvantagetechnologies.com/central-taxi/user_app/";

    public static final String TAG = "centralusertaxi";

    public static final String URL_LOGIN = "";

    public static final boolean IS_DEV_BUILD = true;

    public static final String REGISTRATION = APPURL + "general_api.php?method=registration";
    public static final String LOGIN = APPURL + "method=login";
    public static final String REQUESTTAXI = APPURL + "trip.php?method=startTrip";
    public static final String CANCELTAXI = APPURL + "trip.php?method=cancel";
    public static final String CHANGEPAASWORD = APPURL + "method=changePassword";
    public static final String SENDMESSAGE = APPURL + "trip.php?method=sendMessage";
    public static final String ALLREADYBOARDED = APPURL + "trip.php?method=boardedTrip";
    public static final String UPDATEPROFILE = APPURL + "general_api.php?method=updateProfile";
    public static final String COMPANYNAME = APPURL + "general_api.php?method=companyName";
    public static final String TRIPHISTORY = APPURL + "trip.php?method=tripHistory";
    public static final String FEEDBACKTRIP = APPURL + "trip.php?method=feedback";
    public static final String PANIC = APPURL + "trip.php?method=panic";
    public static final String EMAILVERYFICATION = APPURL + "trip.php?method=emailVerification";
    public static final String FIELTER = APPURL + "trip.php?method=tripHistory";
    public static final String FORGETPASSWORD = APPURL + "method=forgot_password";
    public static final String DRIVERINFO = APPURL + "trip.php?method=getDriver";
    public static final String DRIVERCURRENTINFO = APPURL + "trip.php?method=getCurrentDriverInfo";
    public static final String DRIVERBYCOMPANY = APPURL + "trip.php?method=companyDriver";
    public static final String Emailveryfy = APPURL + "trip.php?method=emailVerification_1";
}
