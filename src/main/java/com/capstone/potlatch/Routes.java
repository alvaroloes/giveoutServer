package com.capstone.potlatch;

/**
 * Created by alvaro on 2/11/14.
 */
public interface Routes {
    public static final String TITLE_PARAMETER = "title";
    public static final String PAGE_PARAMETER = "page";
    public static final String LIMIT_PARAMETER = "limit";
    public static final String TOP_KIND_PARAMETER = "kind";
    public static final String REGRET_PARAMETER = "regret";

    public static final String GIFTS_PATH = "/gifts";
    public static final String GIFTS_ID_PATH = GIFTS_PATH + "/{id}";
    public static final String MY_GIFTS_PATH = GIFTS_PATH + "/mine";
    public static final String GIFTS_TOUCH_PATH = GIFTS_ID_PATH + "/touch";
    public static final String GIFTS_INAPPROPRIATE_PATH = GIFTS_ID_PATH + "/inappropriate";

    public static final String GIFTS_CHAIN_PATH = GIFTS_PATH + "/chains";

    public static final String USERS_PATH = "/users";
    public static final String CURRENT_USER_PATH = USERS_PATH + "/current";
    public static final String TOP_GIVERS_PATH = USERS_PATH + "/top";
}
