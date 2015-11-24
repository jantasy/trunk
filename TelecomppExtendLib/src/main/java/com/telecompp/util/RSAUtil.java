package com.telecompp.util;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSAUtil {
    // // 512bits or more,get rsa keypair
    // public static KeyPair rsa_keyPair_generated(int bits)
    // { 
    //
    // KeyPairGenerator keyGen = null;
    // try
    // {
    // keyGen = KeyPairGenerator.getInstance("RSA");
    //
    // }
    // catch (NoSuchAlgorithmException e)
    // {
    // e.printStackTrace();
    // }
    // if (keyGen == null) {
    // return null;
    // }
    // keyGen.initialize(bits);
    // KeyPair keyPair = keyGen.generateKeyPair();
    // return keyPair;
    // }

    /**
     * get rsa public key and private key from map return hex data std mode
     * 
     * @return map:
     */
    public static void generate_rsa_std(KeyPair keyPair, Map<String, String> map) {

        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        // public key
        BigInteger public_key_exponent = publicKey.getPublicExponent();
        BigInteger public_key_modules = publicKey.getModulus();
        // output
        map.put(CipherContants.RSA_Name_Public_Modulus,
                public_key_modules.toString(16));
        map.put(CipherContants.RSA_Name_Public_Exponent,
                public_key_exponent.toString(16));
        // private key
        BigInteger private_key_modules = privateKey.getModulus();
        BigInteger private_key_exponent = privateKey.getPrivateExponent();
        // output
        map.put(CipherContants.RSA_Name_Private_Modulus,
                private_key_modules.toString(16));
        map.put(CipherContants.RSA_Name_Private_Exponent,
                private_key_exponent.toString(16));
    }

    /**
     * get rsa public key and private key from map hex key crt mode
     * 
     * @param keyPair
     * @param map
     */
    public static void generate_rsa_crt(KeyPair keyPair, Map<String, String> map) {
        RSAPrivateCrtKey privateCrtKey = (RSAPrivateCrtKey) keyPair
                .getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        // 公钥
        BigInteger public_key_modulus = publicKey.getModulus();
        BigInteger public_key_exponent = publicKey.getPublicExponent();
        // output
        map.put(CipherContants.RSA_Name_Public_Modulus,
                public_key_modulus.toString(16));
        map.put(CipherContants.RSA_Name_Public_Exponent,
                public_key_exponent.toString(16));
        // 私钥
        BigInteger private_key_modulus = privateCrtKey.getModulus();
        BigInteger private_key_exponent = privateCrtKey.getPrivateExponent();
        BigInteger private_key_p = privateCrtKey.getPrimeP();
        BigInteger private_key_q = privateCrtKey.getPrimeQ();
        BigInteger private_key_dp = privateCrtKey.getPrimeExponentP();
        BigInteger private_key_dq = privateCrtKey.getPrimeExponentQ();
        BigInteger private_key_qinv = privateCrtKey.getCrtCoefficient();
        map.put(CipherContants.RSA_Name_Private_Modulus,
                private_key_modulus.toString(16));
        map.put(CipherContants.RSA_Name_Private_P, private_key_p.toString(16));
        map.put(CipherContants.RSA_Name_Private_Q, private_key_q.toString(16));
        map.put(CipherContants.RSA_Name_Private_DP, private_key_dp.toString(16));
        map.put(CipherContants.RSA_Name_Private_DQ, private_key_dq.toString(16));
        map.put(CipherContants.RSA_Name_Private_QINV,
                private_key_qinv.toString(16));
        map.put(CipherContants.RSA_Name_Private_Exponent,
                private_key_exponent.toString(16));
    }

    /**
     * RSAKeySpec init rsa private key crt and rsa private key std and rsa
     * public key
     * 
     * @param type
     *            【RSA_KeySpec_PubKey =
     *            1;RSA_KeySpec_PriKey_Std=2;RSA_KeySpec_PriKey_Crt=3;】
     * @param pri_modulus
     * @param pri_exponent
     * @param p
     * @param q
     * @param dp
     * @param dq
     * @param qinv
     * @param pub_modulus
     * @param pub_exponent
     * @return KeySpec[RSAPublicKeySpec、RSAPrivateCrtKeySpec、RSAPrivateKeySpec]
     */
    private static KeySpec rsa_init_pk(int type, String pri_modulus,
            String pri_exponent, String p, String q, String dp, String dq,
            String qinv, String pub_modulus, String pub_exponent) {

        BigInteger pri_p = null;
        BigInteger pri_q = null;
        BigInteger pri_dp = null;
        BigInteger pri_dq = null;
        BigInteger pri_qinv = null;
        BigInteger pri_exponent_bigInteger = null;
        BigInteger pri_modulus_bigInteger = null;
        BigInteger pub_exponent_bigInteger = null;
        BigInteger pub_modulus_bigInteger = null;
        if (type == CipherContants.RSA_KeyType_PriKey_Crt) {
            // private key crt
            if (dp == null || dq == null || pri_exponent == null
                    || pri_modulus == null || p == null || q == null
                    || qinv == null || pub_exponent == null) {
                return null;
            }
            pri_p = new BigInteger(p, 16);
            pri_q = new BigInteger(q, 16);
            pri_dp = new BigInteger(dp, 16);
            pri_dq = new BigInteger(dq, 16);
            pri_qinv = new BigInteger(qinv, 16);
            pri_exponent_bigInteger = new BigInteger(pri_exponent, 16);
            pri_modulus_bigInteger = new BigInteger(pri_modulus, 16);
            pub_exponent_bigInteger = new BigInteger(pub_exponent, 16);
            RSAPrivateCrtKeySpec rsaPrivateCrtKeySpec = new RSAPrivateCrtKeySpec(
                    pri_modulus_bigInteger, pub_exponent_bigInteger,
                    pri_exponent_bigInteger, pri_p, pri_q, pri_dp, pri_dq,
                    pri_qinv);
            return rsaPrivateCrtKeySpec;
        } else if (type == CipherContants.RSA_KeyType_PubKey) {
            // public key
            if (pub_exponent == null || pub_modulus == null) {
                return null;
            }
            pub_exponent_bigInteger = new BigInteger(pub_exponent, 16);
            pub_modulus_bigInteger = new BigInteger(pub_modulus, 16);
            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(
                    pub_modulus_bigInteger, pub_exponent_bigInteger);
            return rsaPublicKeySpec;
        } else if (type == CipherContants.RSA_KeyType_PriKey_Std) {
            // private key std
            if (pri_modulus == null || pri_exponent == null) {
                return null;
            }
            pri_exponent_bigInteger = new BigInteger(pri_exponent, 16);
            pri_modulus_bigInteger = new BigInteger(pri_modulus, 16);
            RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(
                    pri_modulus_bigInteger, pri_exponent_bigInteger);
            return rsaPrivateKeySpec;
        } else {
            return null;
        }
    }

    /**
     * private key decrypt in crt mode
     * 
     * @param data
     * @param modulus
     *            hex
     * @param exponent
     *            hex
     * @param dp
     *            hex
     * @param dq
     *            hex
     * @param p
     *            hex
     * @param q
     *            hex
     * @param qinv
     *            hex
     * @param pub_exponents
     *            public key exponent hex
     * @param alg
     *            :RSA/ECB/PKCS1Padding
     * @return
     */
    public static byte[] rsa_crt_prikey_decrypt(byte[] data, String alg,
            String pri_modulus, String pri_exponent, String dp, String dq,
            String p, String q, String qinv, String pub_exponents) {
        if (pri_modulus == null || pri_exponent == null || dp == null
                || dq == null || p == null || q == null || qinv == null
                || pub_exponents == null || data == null) {
            return null;
        }
        byte[] result = null;
        RSAPrivateCrtKeySpec rsaPrivateCrtKeySpec = (RSAPrivateCrtKeySpec) rsa_init_pk(
                CipherContants.RSA_KeyType_PriKey_Crt, pri_modulus,
                pri_exponent, p, q, dp, dq, qinv, null, pub_exponents);
        KeyFactory factory = null;
        try {
            factory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (factory == null) {
            return null;
        }
        PrivateKey pri_key;
        try {
            pri_key = factory.generatePrivate(rsaPrivateCrtKeySpec);
            Cipher c1 = Cipher.getInstance(alg);
            c1.init(Cipher.DECRYPT_MODE, pri_key);
            result = c1.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    /**
     * rsa private key in std mode decrypt
     * 
     * @param data
     * @param alg
     *            "RSA/ECB/PKCS1Padding"
     * @param pri_modulus
     *            hex
     * @param pri_exponent
     *            hex
     * @return
     */
    public static byte[] rsa_std_prikey_decrypt(byte[] data, String alg,
            String pri_modulus, String pri_exponent) {
        byte[] result = null;

        PrivateKey privateKey = null;

        RSAPrivateKeySpec rsaPrivateKeySpec = (RSAPrivateKeySpec) rsa_init_pk(
                CipherContants.RSA_KeyType_PriKey_Std, pri_modulus,
                pri_exponent, null, null, null, null, null, null, null);

        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        if (keyFactory == null) {
            return null;
        }
        // "RSA/ECB/PKCS1Padding"
        try {
            privateKey = keyFactory.generatePrivate(rsaPrivateKeySpec);
            Cipher c1 = Cipher.getInstance(alg);
            if ((c1 == null) || (privateKey == null)) {
                return null;
            }
            c1.init(Cipher.DECRYPT_MODE, privateKey);
            result = c1.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * rsa public key encrypt
     * 
     * @param data
     * @param alg
     *            "RSA/ECB/PKCS1Padding"
     * @param pub_modulus
     * @param pub_exponent
     * @return
     */
    public static byte[] rsa_pubkey_encrypt(byte[] data, String alg,
            String pub_modulus, String pub_exponent) {

        byte[] result = null;

        PublicKey publicKey = null;
        RSAPublicKeySpec rsaPublicKeySpec = (RSAPublicKeySpec) rsa_init_pk(
                CipherContants.RSA_KeyType_PubKey, null, null, null, null,
                null, null, null, pub_modulus, pub_exponent);

        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
            return null;
        }
        if (keyFactory == null) {
            return null;
        }
        try {
            publicKey = keyFactory.generatePublic(rsaPublicKeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }

        try {
            Cipher c1 = Cipher.getInstance(alg);
            if ((c1 == null) || (publicKey == null)) {
                return null;
            }
            c1.init(Cipher.ENCRYPT_MODE, publicKey);
            result = c1.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * use [rsa private key crt ] or [ras private key std] signature
     * 
     * @param data
     * @param alg
     *            "SHA1WithRSA"
     *            SHA1WithRSA、MD2withRSA、MD5withRSA、SHA1withRSA、SHA256withRSA
     *            、SHA384withRSA、SHA512withRSA
     * @param prikey_type
     *            :[RSA_KeyType_PriKey_Std=2] [RSA_KeyType_PriKey_Crt=3]
     * @param pri_modulus
     * @param pri_exponent
     * @param dp
     * @param dq
     * @param p
     * @param q
     * @param qinv
     * @param pub_exponents
     * @return
     */
    public static byte[] rsa_prikey_signature(byte[] data, String alg,
            int prikey_type, String pri_modulus, String pri_exponent,
            String dp, String dq, String p, String q, String qinv,
            String pub_exponents) {

        PrivateKey privateKey = null;
        RSAPrivateCrtKeySpec rsaPrivateCrtKeySpec = null;// crt
        RSAPrivateKeySpec rsaPrivateKeySpec = null;// std
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
            return null;
        }
        if (keyFactory == null) {
            return null;
        }
        if (prikey_type == CipherContants.RSA_KeyType_PriKey_Crt) {
            rsaPrivateCrtKeySpec = (RSAPrivateCrtKeySpec) rsa_init_pk(
                    prikey_type, pri_modulus, pri_exponent, p, q, dp, dq, qinv,
                    null, pub_exponents);
            if (rsaPrivateCrtKeySpec == null) {
                return null;
            }
            try {
                privateKey = keyFactory.generatePrivate(rsaPrivateCrtKeySpec);
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
                return null;
            }
        } else if (prikey_type == CipherContants.RSA_KeyType_PriKey_Std) {
            rsaPrivateKeySpec = (RSAPrivateKeySpec) rsa_init_pk(prikey_type,
                    pri_modulus, pri_exponent, p, q, dp, dq, qinv, null,
                    pub_exponents);
            if (rsaPrivateKeySpec == null) {
                return null;
            }
            try {
                privateKey = keyFactory.generatePrivate(rsaPrivateKeySpec);
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
        if (privateKey == null) {
            return null;
        }
        Signature signature = null;
        try {
            signature = Signature.getInstance(alg);
            if (signature == null) {
                return null;
            }
            signature.initSign(privateKey);
            signature.update(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        } catch (SignatureException e) {
            e.printStackTrace();
            return null;
        }

        byte[] resp = null;
        try {
            resp = signature.sign();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return resp;
    }

    /**
     * rsa public key validate sign
     * 
     * @param data
     * @param bsignature
     * @param alg
     *            "SHA1WithRSA"
     * @param pub_modulus
     * @param pub_exponent
     * @return
     */
    public static boolean rsa_pubkey_verify(byte[] data, byte[] bsignature,
            String alg, String pub_modulus, String pub_exponent) {
        PublicKey publicKey = null;
        RSAPublicKeySpec rsaPublicKeySpec = (RSAPublicKeySpec) rsa_init_pk(
                CipherContants.RSA_KeyType_PubKey, null, null, null, null,
                null, null, null, pub_modulus, pub_exponent);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
            return false;
        }
        if(keyFactory == null){
            return false;
        }
        try {
            publicKey = keyFactory.generatePublic(rsaPublicKeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return false;
        }
        if (publicKey == null) {
            return false;
        }
        Signature signature = null;
        boolean b = false;
        try {
            signature = Signature.getInstance(alg);
            if (signature == null) {
                return false;
            }
            signature.initVerify(publicKey);
            signature.update(data);
            b = signature.verify(bsignature);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        return b;
    }
}
