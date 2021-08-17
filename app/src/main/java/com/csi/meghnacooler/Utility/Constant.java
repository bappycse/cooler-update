package com.csi.meghnacooler.Utility;

/**
 * Created by Jahid on 10/2/19.
 */
public class Constant {

    public static boolean requisitionFlag= false;

    public static class sharedPrefItems{
        public static  final String globalPreferenceNameForUser = "USER";
        public static final String USER_ID = "USER_ID";
        public static final String USER_NAME_ID = "USER_NAME_ID";
        public static final String USER_NAME = "USER_NAME";
        public static final String GROUP = "GROUP";
        public static final String DESIGNATION = "DESIGNATION";
        public static final String EMAIL = "EMAIL";
        public static final String PHONE = "PHONE";
        public static final String NOTE = "NOTE";
        public static final String REMEMBER_ME = "REMEMBER_ME";
        public static final String RECENT_TOKEN = "RECENT_TOKEN";
        public static final String OLD_TOKEN = "OLD_TOKEN";
    }
    public static class API{
        //public static final String URL = "http://cooler.mgi.org/cooler/public/api/v1"; //

        //public  static final String URL = "http://cooler.mgi.org/api/v1"; /// live
        public  static final String URL = "http://192.168.1.101/cooler/public/api/v1"; /// local
        //public  static final String URL = "http://192.168.1.108/cooler/public/api/v1"; /// local

        public static final String LOGIN = URL+"/login";
        public static final String NOTIFICATION = URL+"/user";
        public static final String COMPLAIN_LIST = URL+"/complain/list";
        public static final String COMPLAIN_ACTION = URL+"/complain";
        public static final String COMPLAIN_TYPE = URL+"/complain/type";
        public static final String PRODUCT_LIST = URL+"/products/spare";
        public static final String REQUISITION_ENTRY = URL+"/products/requisition";
        public static final String REQUISITION_UPDATE = URL+"/products/requisition-update";
        public static final String COST_ENTRY = URL+"/cost/requisition";
        public static final String TICKET_LIST = URL+"/complain/list/all";
        public static final String STOCK_LIST = URL+"/product/stock-info";
        public static final String CHANGE_PASSWORD = URL+"/user/change-password";
        public static final String UPDATE_PROFILE = URL+"/user/change-information";
        public static final String UPDATE_COMPLAIN = URL+"/complain/feedback/update";
    }
}
