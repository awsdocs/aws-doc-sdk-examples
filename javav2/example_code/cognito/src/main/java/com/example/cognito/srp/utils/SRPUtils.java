/* Copyright 2010-2023 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.example.cognito.srp.utils;

import software.amazon.awssdk.utils.Pair;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

public class SRPUtils {
    private static final String RNG_ALGORIGHM = "SHA1PRNG";
    private static final String HEX_N = "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1"
                                      + "29024E088A67CC74020BBEA63B139B22514A08798E3404DD"
                                      + "EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245"
                                      + "E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED"
                                      + "EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3D"
                                      + "C2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F"
                                      + "83655D23DCA3AD961C62F356208552BB9ED529077096966D"
                                      + "670C354E4ABC9804F1746C08CA18217C32905E462E36CE3B"
                                      + "E39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9"
                                      + "DE2BCBF6955817183995497CEA956AE515D2261898FA0510"
                                      + "15728E5A8AAAC42DAD33170D04507A33A85521ABDF1CBA64"
                                      + "ECFB850458DBEF0A8AEA71575D060C7DB3970F85A6E1E4C7"
                                      + "ABF5AE8CDB0933D71E8C94E04A25619DCEE3D2261AD2EE6B"
                                      + "F12FFA06D98A0864D87602733EC86A64521F2B18177B200C"
                                      + "BBE117577A615D6C770988C0BAD946E208E24FA074E5AB31"
                                      + "43DB5BFCE0FD108E4B82D120A93AD2CAFFFFFFFFFFFFFFFF";
    public static final BigInteger N = new BigInteger(HEX_N, 16);
    public static final BigInteger g = BigInteger.valueOf(2);
    public static final BigInteger k;
    private static final int SALT_KEY_LENGTH = 128;
    private static final int EPHEMERAL_KEY_LENGTH = 1024;
    private static final int DERIVED_KEY_SIZE = 16;
    private static final String DERIVED_KEY_INFO = "Caldera Derived Key";
    private static final SecureRandom SECURE_RANDOM;
    private static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.US);

    static {
        try {
            SECURE_RANDOM = SecureRandom.getInstance(RNG_ALGORIGHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        k = HashUtils.sha256(N, g);
        DATE_TIME_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static BigInteger generateSalt() {
        BigInteger salt = new BigInteger(SALT_KEY_LENGTH, SECURE_RANDOM);
        return salt;
    }

    public static String getCurrentTimestamp() {
        Date currentTimestamp = new Date();
        return DATE_TIME_FORMATTER.format(currentTimestamp);
    }

    private static BigInteger calculateX(String name, String password, BigInteger salt) {
        String credential = name + ":" + password;
        byte[] credentialHash = HashUtils.sha256(credential.getBytes(StandardCharsets.UTF_8));
        byte[] saltedCredentialHash = HashUtils.sha256(salt.toByteArray(), credentialHash);
        BigInteger x = new BigInteger(1, saltedCredentialHash);
        return x;
    }

    public static BigInteger generatePasswordVerifier(String name, String password, BigInteger salt) {
        BigInteger x = calculateX(name, password, salt);
        return g.modPow(x, N);
    }

    public static byte[] calculatePasswordClaimSignature(String name, String password, String timestamp,
                                                         BigInteger a, BigInteger A, BigInteger B, BigInteger salt,
                                                         byte[] secretBlock) {
        BigInteger x = calculateX(name, password, salt);
        // u = H(A, B)
        BigInteger u = HashUtils.sha256(A, B);
        // S = ((B-kg^x)^(a+ux))%N
        BigInteger S = (B.subtract(k.multiply(g.modPow(x, N))).modPow(a.add(u.multiply(x)), N)).mod(N);
        byte[] key = hkdf(S.toByteArray(), u.toByteArray());
        byte[] passwordClaimSignature = HashUtils.hmac(key,
                name.getBytes(StandardCharsets.UTF_8), secretBlock, timestamp.getBytes(StandardCharsets.UTF_8));
        return passwordClaimSignature;
    }

    private static byte[] hkdf(byte[] ikm, byte[] salt) {
        byte[] prk = HashUtils.hmac(salt, ikm);
        byte[] end = {(byte)1};
        byte[] output = HashUtils.hmac(prk, DERIVED_KEY_INFO.getBytes(StandardCharsets.UTF_8), end);
        return Arrays.copyOfRange(output, 0, DERIVED_KEY_SIZE);
    }

    public static Pair<BigInteger, BigInteger> generateSrpClientKeys() {
        BigInteger a, A;
        do {
            a = new BigInteger(EPHEMERAL_KEY_LENGTH, SECURE_RANDOM).mod(N);
            A = g.modPow(a, N);
        } while (A.mod(N).equals(BigInteger.ZERO));
        return Pair.of(a, A);
    }
}
