package com.rengu.machinereadingcomprehension.Utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @program: machine-reading-comprehension
 * @author: hanch
 * @create: 2018-09-06 11:28
 **/

public class AESUtils {

    public static final String AES_ALGORITHM = "AES";

    //公钥解密
    public static File decrypt(File encryptFile, String RSA_encrypt_key, String public_key) throws Exception {
        String AES_KEY = RSAUtils.decrypt(RSA_encrypt_key, RSAUtils.getPublicKey(public_key));
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Base64.decodeBase64(RSAUtils.decrypt(RSA_encrypt_key, RSAUtils.getPublicKey(public_key))), AES_ALGORITHM));
        File decryptFile = new File(FileUtils.getUserDirectoryPath() + "/decrypt.json");
        decryptFile.createNewFile();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(new FileOutputStream(decryptFile), cipher);
        FileInputStream fileInputStream = new FileInputStream(encryptFile);
        IOUtils.copy(fileInputStream, cipherOutputStream);
        cipherOutputStream.close();
        fileInputStream.close();
        return decryptFile;
    }
}
