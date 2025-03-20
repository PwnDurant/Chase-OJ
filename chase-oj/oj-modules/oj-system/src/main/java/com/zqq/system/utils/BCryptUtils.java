package com.zqq.system.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 加密算法工具类
 */
public class BCryptUtils {
    /**
     * 生成加密后密文
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword     真实密码
     * @param encodedPassword 加密后密文
     * @return 结果
     */

//    根据数据库中查处加密后的密码，提取出当时加密的盐值
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public static void main(String[] args) {
        System.out.println(encryptPassword("123456"));
        System.out.println(matchesPassword("123456", "$2a$10$gyOB.mHjvBYv.QzuWlY39en.Dis1uVHuwEZw3G7Po8E51iVQ/OAsS"));

    }
}
