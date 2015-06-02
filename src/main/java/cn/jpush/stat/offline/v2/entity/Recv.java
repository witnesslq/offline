package cn.jpush.stat.offline.v2.entity;

import java.util.HashMap;

public class Recv {

    // offline stats hbase table name
    public static final String TABLE_NAME = "msg_off";

    /*
     * Column family and qualifiers
     */
    public static final String FAMILY_A = "A";
    
    //android
    public static final String ANDROID_TARGER = "at";
    public static final String ANDROID_ONLINTPUSH = "ao";
    public static final String ANDROID_REV = "ar";
    public static final String ANDROID_CLICK = "ac";
    public static final String ANDROID_CUSTOM_CLICK = "acc";
    
    //ios
    public static final String IOS_ACTIVEUSER_CNT = "it";
    public static final String IOS_REV = "ir";
    public static final String IOS_CUSTOM_CLICK = "icc";
    
    //apns
    public static final String COLUMN_APNS_PUSH = "ep";
    public static final String COLUMN_NO_PUSH_APNS = "ef";
    public static final String COLUMN_APNS_REFUSE = "er";
    public static final String COLUMN_APNS_CLICK = "ec";
    
    //mpns
    public static final String MPNS_TARGET = "mt";
    public static final String MPNS_SUCC = "ms";
    public static final String MPNS_CLICK = "mc";
    
    private static final HashMap<String, String> QUALIFIERS = new HashMap<String, String>();
    
    public static String getStatus(String status){
        return QUALIFIERS.get(status);
    }

    /*
     * common fields
     */
    public static final String UID = "uid";
    public static final String ITIME = "itime";

    /**
     * fields specified by IM
     */
    public static final String JUID = "juid";
    public static final String TARGET = "target";
    public static final String TARGET_COUNT = "target_count";
    public static final String MSG_TYPE_CODE = "msg_type";

    public static final String CONTENT = "content";
    /*
     * fields specified by msgRecv
     */
    public static final String NUMBER_RECORDS_RECV = "total";
    public static final String RECORDS_RECV = "rows";
    public static final String MSGID_RECV = "mid";

    /*
     * fields specified by APNS or MPNS
     */
    public static final String MSGID_PNS = "msg_id";
    public static final String PLATFORM_PNS = "platform";
    public static final String APPKEY_PNS = "appkey";
    
    public static final String STATUS_APNS = "status";
    public static final String PUSH_APNS = "success";
    public static final String FAIL_APNS = "response";
    public static final String NO_PUSH_APNS = "failed";
    
    public static final String STATUS_MPNS = "success";
    public static final String ERROR_MPNS = "error";
    public static final int SUCCESS_MPNS = 0;
    public static final int FAIL_MPNS = 1;
    
    static{
        QUALIFIERS.put(PUSH_APNS, COLUMN_APNS_PUSH);
        QUALIFIERS.put(FAIL_APNS, COLUMN_APNS_REFUSE);
        QUALIFIERS.put(NO_PUSH_APNS, COLUMN_NO_PUSH_APNS);

    }
}
