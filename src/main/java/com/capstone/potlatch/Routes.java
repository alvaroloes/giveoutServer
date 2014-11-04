package com.capstone.potlatch;

/**
 * Created by alvaro on 2/11/14.
 */
public interface Routes {
    public static final String GIFTS_PATH = "/gifts";
    public static final String TITLE_PARAMETER = "title";

    public static final String GIFTS_CHAIN_PATH = GIFTS_PATH + "/chains";

    public static final String USERS_PATH = "/users";
    public static final String CURRENT_USER_PATH = USERS_PATH + "/current";
    public static final String TOP_GIVERS_PATH = USERS_PATH + "/top";
}