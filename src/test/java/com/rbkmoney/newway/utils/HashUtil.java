package com.rbkmoney.newway.utils;

import org.springframework.util.DigestUtils;

public class HashUtil {

    public static boolean checkHashMod(String str, int div, int mod) {
        return (getIntHash(str) % div) == mod;
    }

    /**
     * @param str for return a hexadecimal string
     * @return int value of first 7 digits of md5-hash of invoice_id
     */
    public static int getIntHash(String str) {
        String hexStr = DigestUtils.md5DigestAsHex(str.getBytes());
        return Integer.parseInt(hexStr.substring(0, 7), 16);
    }
}
