package com.example.vishistvarugeese.e2emessenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.spongycastle.jce.X509Principal;
import org.spongycastle.util.encoders.Base64;
import org.spongycastle.x509.X509V3CertificateGenerator;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by Vishist Varugeese on 02-04-2018.
 */

public class RSA {

    KeyPairGenerator kpg;
    KeyPair kp;
    PublicKey publicKey;
    PrivateKey privateKey, privKey;
    byte[] encryptedBytes, decryptedBytes;
    Cipher cipher, cipher1;
    String encrypted, decrypted;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    String password = "password";
    char[] ksPass = password.toCharArray();


    public void keyGenerate(Context context, String user) throws NoSuchAlgorithmException {

        kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        kp = kpg.genKeyPair();
        publicKey = kp.getPublic();
        privateKey = kp.getPrivate();

        Log.d("key_original_priv", privateKey + "");
        Log.d("key_original_pub", publicKey + "");

        byte[] publicKeyBytes = publicKey.getEncoded();
        String pubKeyStr = new String(Base64.encode(publicKeyBytes));

        byte[] privateKeyBytes = privateKey.getEncoded();
        String privKeyStr = new String(Base64.encode(privateKeyBytes));

        Log.d("key_string_priv", privKeyStr + "");
        Log.d("key_string_pub", pubKeyStr + "");

        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(user + "_private_key", privKeyStr + "");
        editor.putString(user + "_public_key", pubKeyStr + "");
        editor.commit();

//        FileInputStream is = new FileInputStream("private.ks");
//
//        X509Certificate certificate = generateCertificate(kp);
//        KeyStore keyStore = KeyStore.getInstance("JKS");
//        keyStore.load(is,null);
//        Certificate[] certChain = new Certificate[1];
//        certChain[0] = certificate;
//        keyStore.setKeyEntry("key", (Key) kp.getPrivate(), ksPass, certChain);
    }


    public String Decrypt (String result, Context context, String user) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String privKeyStr = getPrefs(context).getString(user + "_private_key", "no private key");
        byte[] sigBytes = new byte[0];
        try {
            sigBytes = Base64.decode(privKeyStr.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(sigBytes);
        KeyFactory keyFact = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = null;

        try {
            privateKey = keyFact.generatePrivate(privateKeySpec);
            Log.d("key_original_priv", privateKey + "");
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

//        FileInputStream is = new FileInputStream("private.ks");
//        KeyStore keyStore = KeyStore.getInstance("JKS");
//        PrivateKey privkey = (PrivateKey) keystore.getKey("key", ksPass);

        cipher1=Cipher.getInstance("RSA");
        cipher1.init(Cipher.DECRYPT_MODE, privateKey);
        decryptedBytes = cipher1.doFinal(stringToBytes(result));
        decrypted = new String(decryptedBytes);
        return decrypted;

    }

    public String Encrypt (String plain, String pubKeyStr) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {

        byte[] sigBytes = Base64.decode(pubKeyStr);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(sigBytes);
        KeyFactory keyFact = KeyFactory.getInstance("RSA");
        PublicKey publicKey = null;

        try {
            publicKey = keyFact.generatePublic(x509KeySpec);
            Log.d("key_original_pub", publicKey + "");
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE , publicKey);
        encryptedBytes = cipher.doFinal(plain.getBytes());

        encrypted = bytesToString(encryptedBytes);
        return encrypted;

    }

    public  String bytesToString(byte[] b) {
        byte[] b2 = new byte[b.length + 1];
        b2[0] = 1;
        System.arraycopy(b, 0, b2, 1, b.length);
        return new BigInteger(b2).toString(36);
    }

    public  byte[] stringToBytes(String s) {
        byte[] b2 = new BigInteger(s, 36).toByteArray();
        return Arrays.copyOfRange(b2, 1, b2.length);
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("rsa", Context.MODE_PRIVATE);
    }

    public X509Certificate generateCertificate(KeyPair keyPair) throws NoSuchAlgorithmException, CertificateEncodingException, NoSuchProviderException, InvalidKeyException, SignatureException, ParseException {
        Date st = new SimpleDateFormat( "yyyyMMdd" ).parse( "20180401" );
        Date et = new SimpleDateFormat( "yyyyMMdd" ).parse( "20180429" );
        X509V3CertificateGenerator cert = new X509V3CertificateGenerator();
        cert.setSerialNumber(BigInteger.valueOf(1));   //or generate a random number
        cert.setSubjectDN(new X509Principal("CN=localhost"));  //see examples to add O,OU etc
        cert.setIssuerDN(new X509Principal("CN=localhost")); //same since it is self-signed
        cert.setPublicKey(keyPair.getPublic());
        cert.setNotBefore(st);
        cert.setNotAfter(et);
        cert.setSignatureAlgorithm("SHA1WithRSAEncryption");
        PrivateKey signingKey = keyPair.getPrivate();
        return cert.generate(signingKey, "BC");
    }
}