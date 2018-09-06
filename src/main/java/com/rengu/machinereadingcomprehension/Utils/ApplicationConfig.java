package com.rengu.machinereadingcomprehension.Utils;

import java.io.File;

public class ApplicationConfig {

    public static int MAX_COMMIT_TIMES_T = 4;
    public static int MAX_COMMIT_TIMES_P = 2;
    public static int MAX_COMMIT_TIMES_F = 1;

    // 默认角色
    public static String DEFAULT_ADMIN_ROLE_NAME = "admin";
    public static String DEFAULT_USER_ROLE_NAME = "user";
    public static String DEFAULT_ACCEPT_ROLE_NAME = "accept";
    public static String DEFAULT_DENIED_ROLE_NAME = "denied";

    // 默认用户
    public static String DEFAULT_USER_USERNAME = "admin";
    public static String DEFAULT_USER_PASSWORD = "tszx11hcM@4";

    // 加密
    public static String RSA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCZ1umZ3Ug5X9N6S1H6kenJkEHfcdKojZvviGauJiYuzs++G9hGTPX2F0DM6746sLkkIGcxUTAH4Y2GqZPx1DSLVzX3ySogO48A1g4NoYTA0XhW63edI+s2mnMUV27cGcxyWz32VtU0JumKNv1KNW2NvSdmqUEN5WTuYgf4FtvoXwIDAQAB";
    public static String ENCRYPT_AES_KEY = "bzqqHW+AauH0KtVPiE27/C1ib4wpmJK7EtNRf/pIh9n2IqJPoTUhh9OPG6TdIkxDtVaIFvUtutwCdTXGCMOv+jT4niweHqUhGMNIpvSiy+hFedmwN8rkuAQETDnRhHNwrNbtReVyAFysNZBA+riXtaVB0WUW5oI/0Yb3DjKxjpQ=";
    public static File answerFile = null;
}
