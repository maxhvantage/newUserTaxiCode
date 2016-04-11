package com.usertaxi.gcmclasses;

/**
 * Created by Eyon on 9/11/2015.
 */
public class Config {


    // Server Url absolute url where php files are placed.
    public static final String YOUR_SERVER_URL = "http://www.hvantagetechnologies.com/central-taxi/user_app/";

    // Google project id
    public static final String GOOGLE_SENDER_ID = "507542572202";

    /**
     * Tag used on log messages.
     */
    public static final String TAG = "usercentraltaxi";

    // Broadcast reciever name to show gcm registration messages on screen
    public static final String DISPLAY_REGISTRATION_MESSAGE_ACTION = "com.usertaxi.gcm.DISPLAY_REGISTRATION_MESSAGE";

    // Broadcast reciever name to show user messages on screen
    public static final String DISPLAY_MESSAGE_ACTION = "com.usertaxi.gcm.DISPLAY_MESSAGE";

    // Parse server message with this name
    public static final String EXTRA_MESSAGE = "message";
}
