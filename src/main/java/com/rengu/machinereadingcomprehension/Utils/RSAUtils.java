package com.rengu.machinereadingcomprehension.Utils;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * @program: machine-reading-comprehension
 * @author: hanch
 * @create: 2018-09-06 11:27
 **/

public class RSAUtils {

    public static final String RSA_ALGORITHM = "RSA";

    //将base64编码后的公钥字符串转成PublicKey实例
    public static PublicKey getPublicKey(String publicKey) throws Exception {
        byte[] key = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

    //公钥解密
    public static String decrypt(String content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return new String(cipher.doFinal(Base64.decodeBase64(content)));
    }
}
