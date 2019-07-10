//snippet-sourcedescription:[S3Encrypt.java demonstrates how to use various encryption settings in Amazon S3.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon S3]
//snippet-keyword:[AmazonS3Encryption]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
package aws.example.s3;

// snippet-start:[s3.java1.s3_encrypt.complete]
// snippet-start:[s3.java1.s3_encrypt.import]

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3Encryption;
import com.amazonaws.services.s3.AmazonS3EncryptionClientBuilder;
import com.amazonaws.services.s3.model.CryptoConfiguration;
import com.amazonaws.services.s3.model.CryptoMode;
import com.amazonaws.services.s3.model.EncryptionMaterials;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.KMSEncryptionMaterialsProvider;
import com.amazonaws.services.s3.model.StaticEncryptionMaterialsProvider;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
// snippet-end:[s3.java1.s3_encrypt.import]

/**
 * Test out various cryptography settings for S3.
 * <p>
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 * This code also requires you to install the Unlimited Strength Java(TM) Cryptography Extension Policy Files (JCE)
 * You can install this from the oracle site: http://www.oracle.com
 */
public class S3Encrypt {
    public static final String BUCKET_NAME = "s3EncryptTestBucket"; //add your bucket name
    public static final String ENCRYPTED_KEY = "enc-key";
    public static final String NON_ENCRYPTED_KEY = "some-key";

    public static void main(String[] args) {
        System.out.println("calling encryption with customer managed keys");
        S3Encrypt encrypt = new S3Encrypt();

        try {
            //can change to call the other encryption methods
            encrypt.authenticatedEncryption_CustomerManagedKey();
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Uses AES/GCM with AESWrap key wrapping to encrypt the key. Uses v2 metadata schema. Note that authenticated
     * encryption requires the bouncy castle provider to be on the classpath. Also, for authenticated encryption the size
     * of the data can be no longer than 64 GB.
     */
    // snippet-start:[s3.java1.s3_encrypt.authenticated_encryption]
    public void authenticatedEncryption_CustomerManagedKey() throws NoSuchAlgorithmException {
        // snippet-start:[s3.java1.s3_encrypt.authenticated_encryption_build]
        SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
        AmazonS3Encryption s3Encryption = AmazonS3EncryptionClientBuilder
                .standard()
                .withRegion(Regions.US_WEST_2)
                .withCryptoConfiguration(new CryptoConfiguration(CryptoMode.AuthenticatedEncryption))
                .withEncryptionMaterials(new StaticEncryptionMaterialsProvider(new EncryptionMaterials(secretKey)))
                .build();

        AmazonS3 s3NonEncrypt = AmazonS3ClientBuilder.defaultClient();
        // snippet-end:[s3.java1.s3_encrypt.authenticated_encryption_build]

        s3Encryption.putObject(BUCKET_NAME, ENCRYPTED_KEY, "some contents");
        s3NonEncrypt.putObject(BUCKET_NAME, NON_ENCRYPTED_KEY, "some other contents");
        System.out.println(s3Encryption.getObjectAsString(BUCKET_NAME, ENCRYPTED_KEY));
        System.out.println(s3Encryption.getObjectAsString(BUCKET_NAME, NON_ENCRYPTED_KEY));
    }
    // snippet-end:[s3.java1.s3_encrypt.authenticated_encryption]

    /**
     * For ranged GET we do not use authenticated encryption since we aren't reading the entire message and can't produce the
     * MAC. Instead we use AES/CTR, an unauthenticated encryption algorithm. If {@link CryptoMode#StrictAuthenticatedEncryption}
     * is enabled, ranged GETs will not be allowed since they do not use authenticated encryption..
     */

    public void authenticatedEncryption_RangeGet_CustomerManagedKey() throws NoSuchAlgorithmException {
        SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
        AmazonS3Encryption s3Encryption = AmazonS3EncryptionClientBuilder
                .standard()
                .withRegion(Regions.US_WEST_2)
                .withCryptoConfiguration(new CryptoConfiguration(CryptoMode.AuthenticatedEncryption))
                .withEncryptionMaterials(new StaticEncryptionMaterialsProvider(new EncryptionMaterials(secretKey)))
                .build();

        AmazonS3 s3NonEncrypt = AmazonS3ClientBuilder.defaultClient();

        s3Encryption.putObject(BUCKET_NAME, ENCRYPTED_KEY, "some contents");
        s3NonEncrypt.putObject(BUCKET_NAME, NON_ENCRYPTED_KEY, "some other contents");
        System.out.println(s3Encryption.getObjectAsString(BUCKET_NAME, ENCRYPTED_KEY));
        System.out.println(s3Encryption.getObjectAsString(BUCKET_NAME, NON_ENCRYPTED_KEY));
    }


    /**
     * Same as {@link #authenticatedEncryption_CustomerManagedKey()} except uses an asymmetric key pair and
     * RSA/ECB/OAEPWithSHA-256AndMGF1Padding as the key wrapping algorithm.
     */
    public void authenticatedEncryption_CustomerManagedAsymmetricKey() throws NoSuchAlgorithmException {
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        AmazonS3Encryption s3Encryption = AmazonS3EncryptionClientBuilder
                .standard()
                .withRegion(Regions.US_WEST_2)
                .withCryptoConfiguration(new CryptoConfiguration(CryptoMode.AuthenticatedEncryption))
                .withEncryptionMaterials(new StaticEncryptionMaterialsProvider(new EncryptionMaterials(keyPair)))
                .build();

        AmazonS3 s3NonEncrypt = AmazonS3ClientBuilder.defaultClient();

        s3Encryption.putObject(BUCKET_NAME, ENCRYPTED_KEY, "some contents");
        s3NonEncrypt.putObject(BUCKET_NAME, NON_ENCRYPTED_KEY, "some other contents");
        System.out.println(s3Encryption.getObjectAsString(BUCKET_NAME, ENCRYPTED_KEY));
        System.out.println(s3Encryption.getObjectAsString(BUCKET_NAME, NON_ENCRYPTED_KEY));
    }

    /**
     * Uses AES/GCM with AESWrap key wrapping to encrypt the key. Uses v2 metadata schema. The only difference between this and
     * {@link #authenticatedEncryption_CustomerManagedKey()} is that attempting to retrieve an object non
     * encrypted with AES/GCM will thrown an exception instead of falling back to encryption only or plaintext GET.
     */
    // snippet-start:[s3.java1.s3_encrypt.strict_authenticated_encryption]
    public void strictAuthenticatedEncryption_CustomerManagedKey() throws NoSuchAlgorithmException {
        // snippet-start:[s3.java1.s3_encrypt.strict_authenticated_encryption_build]
        SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
        AmazonS3Encryption s3Encryption = AmazonS3EncryptionClientBuilder
                .standard()
                .withRegion(Regions.US_WEST_2)
                .withCryptoConfiguration(new CryptoConfiguration(CryptoMode.StrictAuthenticatedEncryption))
                .withEncryptionMaterials(new StaticEncryptionMaterialsProvider(new EncryptionMaterials(secretKey)))
                .build();

        AmazonS3 s3NonEncrypt = AmazonS3ClientBuilder.defaultClient();
        // snippet-end:[s3.java1.s3_encrypt.strict_authenticated_encryption_build]

        s3Encryption.putObject(BUCKET_NAME, ENCRYPTED_KEY, "some contents");
        s3NonEncrypt.putObject(BUCKET_NAME, NON_ENCRYPTED_KEY, "some other contents");
        System.out.println(s3Encryption.getObjectAsString(BUCKET_NAME, ENCRYPTED_KEY));
        try {
            s3Encryption.getObjectAsString(BUCKET_NAME, NON_ENCRYPTED_KEY);
        } catch (SecurityException e) {
            // Strict authenticated encryption will throw an exception if an object is not encrypted with AES/GCM
            System.err.println(NON_ENCRYPTED_KEY + " was not encrypted with AES/GCM");
        }
    }
    // snippet-end:[s3.java1.s3_encrypt.strict_authenticated_encryption]

    /**
     * Strict authenticated encryption mode does not support ranged GETs. This is because we must use AES/CTR for ranged
     * GETs which is not an authenticated encryption algorithm. To do a partial get using authenticated encryption you have to
     * get the whole object and filter to the data you want.
     */
    public void strictAuthenticatedEncryption_RangeGet_CustomerManagedKey() throws NoSuchAlgorithmException {
        SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
        AmazonS3Encryption s3Encryption = AmazonS3EncryptionClientBuilder
                .standard()
                .withRegion(Regions.US_WEST_2)
                .withCryptoConfiguration(new CryptoConfiguration(CryptoMode.StrictAuthenticatedEncryption))
                .withEncryptionMaterials(new StaticEncryptionMaterialsProvider(new EncryptionMaterials(secretKey)))
                .build();

        s3Encryption.putObject(BUCKET_NAME, ENCRYPTED_KEY, "some contents");
        try {
            s3Encryption.getObject(new GetObjectRequest(BUCKET_NAME, ENCRYPTED_KEY).withRange(0, 2));
        } catch (SecurityException e) {
            System.err.println("Range GET is not supported with authenticated encryption");
        }
    }

    /**
     * Uses AES/CBC algorithm, no key wrapping.
     */
    public void encryptionOnly_CustomerManagedKey() throws NoSuchAlgorithmException {
        SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
        AmazonS3Encryption s3Encryption = AmazonS3EncryptionClientBuilder
                .standard()
                .withRegion(Regions.US_WEST_2)
                .withCryptoConfiguration(new CryptoConfiguration(CryptoMode.EncryptionOnly))
                .withEncryptionMaterials(new StaticEncryptionMaterialsProvider(new EncryptionMaterials(secretKey)))
                .build();

        AmazonS3 s3NonEncrypt = AmazonS3ClientBuilder.defaultClient();

        s3Encryption.putObject(BUCKET_NAME, ENCRYPTED_KEY, "some contents");
        s3NonEncrypt.putObject(BUCKET_NAME, NON_ENCRYPTED_KEY, "some other contents");
        System.out.println(s3Encryption.getObjectAsString(BUCKET_NAME, ENCRYPTED_KEY));
        System.out.println(s3Encryption.getObjectAsString(BUCKET_NAME, NON_ENCRYPTED_KEY));
    }

    /**
     * Non-authenticated encryption schemes can do range GETs without an issue.
     */
    // snippet-start:[s3.java1.s3_encrypt.encryption_only]
    public void encryptionOnly_RangeGet_CustomerManagedKey() throws NoSuchAlgorithmException {
        SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
        AmazonS3Encryption s3Encryption = AmazonS3EncryptionClientBuilder
                .standard()
                .withRegion(Regions.US_WEST_2)
                .withCryptoConfiguration(new CryptoConfiguration(CryptoMode.EncryptionOnly))
                .withEncryptionMaterials(new StaticEncryptionMaterialsProvider(new EncryptionMaterials(secretKey)))
                .build();

        s3Encryption.putObject(BUCKET_NAME, ENCRYPTED_KEY, "some contents");
        System.out.println(s3Encryption.getObject(new GetObjectRequest(BUCKET_NAME, ENCRYPTED_KEY)
                .withRange(0, 2)));
    }
    // snippet-end:[s3.java1.s3_encrypt.encryption_only]

    /**
     * Uses an asymmetric key pair instead of a symmetric key. Note this does not change the algorithm used to encrypt
     * the content, that will still be a symmetric key algorithm (AES/CBC in this case) using the derived CEK. It does impact
     * the algorithm used to encrypt the CEK, in this case we use RSA/ECB/OAEPWithSHA-256AndMGF1Padding.
     */
    // snippet-start:[s3.java1.s3_encrypt.encryption_only_asymetric_key]
    public void encryptionOnly_CustomerManagedAsymetricKey() throws NoSuchAlgorithmException {
        // snippet-start:[s3.java1.s3_encrypt.encryption_only_asymetric_key_build]
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        AmazonS3Encryption s3Encryption = AmazonS3EncryptionClientBuilder
                .standard()
                .withRegion(Regions.US_WEST_2)
                .withCryptoConfiguration(new CryptoConfiguration(CryptoMode.EncryptionOnly))
                .withEncryptionMaterials(new StaticEncryptionMaterialsProvider(new EncryptionMaterials(keyPair)))
                .build();

        AmazonS3 s3NonEncrypt = AmazonS3ClientBuilder.defaultClient();
        // snippet-end:[s3.java1.s3_encrypt.encryption_only_asymetric_key_build]

        // snippet-start:[s3.java1.s3_encrypt.encryption_only_asymetric_key_put_object]
        s3Encryption.putObject(BUCKET_NAME, ENCRYPTED_KEY, "some contents");
        s3NonEncrypt.putObject(BUCKET_NAME, NON_ENCRYPTED_KEY, "some other contents");
        // snippet-end:[s3.java1.s3_encrypt.encryption_only_asymetric_key_put_object]
        // snippet-start:[s3.java1.s3_encrypt.encryption_only_asymetric_key_retrieve]
        System.out.println(s3Encryption.getObjectAsString(BUCKET_NAME, ENCRYPTED_KEY));
        System.out.println(s3Encryption.getObjectAsString(BUCKET_NAME, NON_ENCRYPTED_KEY));
        // snippet-end:[s3.java1.s3_encrypt.encryption_only_asymetric_key_retrieve]
    }
    // snippet-end:[s3.java1.s3_encrypt.encryption_only_asymetric_key]

    /**
     * This uses the V2 metadata schema with a key wrap algorithm of 'kms' and a CEK algorithm of AES/CBC/PKCS5Padding.
     */
    // snippet-start:[s3.java1.s3_encrypt.kms_encryption_only]
    public void encryptionOnly_KmsManagedKey() throws NoSuchAlgorithmException {
        // snippet-start:[s3.java1.s3_encrypt.kms_encryption_only_build]
        AmazonS3Encryption s3Encryption = AmazonS3EncryptionClientBuilder
                .standard()
                .withRegion(Regions.US_WEST_2)
                .withCryptoConfiguration(new CryptoConfiguration(CryptoMode.EncryptionOnly).withAwsKmsRegion(Region.getRegion(Regions.US_WEST_2)))
                // Can either be Key ID or alias (prefixed with 'alias/')
                .withEncryptionMaterials(new KMSEncryptionMaterialsProvider("alias/s3-kms-key"))
                .build();

        AmazonS3 s3NonEncrypt = AmazonS3ClientBuilder.defaultClient();
        // snippet-end:[s3.java1.s3_encrypt.kms_encryption_only_build]

        // snippet-start:[s3.java1.s3_encrypt.kms_encryption_only_put_object]
        s3Encryption.putObject(BUCKET_NAME, ENCRYPTED_KEY, "some contents");
        s3NonEncrypt.putObject(BUCKET_NAME, NON_ENCRYPTED_KEY, "some other contents");
        // snippet-end:[s3.java1.s3_encrypt.kms_encryption_only_put_object]
        // snippet-start:[s3.java1.s3_encrypt.kms_encryption_only_retrieve]
        System.out.println(s3Encryption.getObjectAsString(BUCKET_NAME, ENCRYPTED_KEY));
        System.out.println(s3Encryption.getObjectAsString(BUCKET_NAME, NON_ENCRYPTED_KEY));
        // snippet-end:[s3.java1.s3_encrypt.kms_encryption_only_retrieve]
    }
    // snippet-end:[s3.java1.s3_encrypt.kms_encryption_only]

    /**
     * This uses the V2 metadata schema with a key wrap algorithm of 'kms' and a CEK algorithm of AES/GCM/NoPadding.
     */
    // snippet-start:[s3.java1.s3_encrypt.kms_authenticated_encryption]
    public void authenticatedEncryption_KmsManagedKey() throws NoSuchAlgorithmException {
        // snippet-start:[s3.java1.s3_encrypt.kms_authenticated_encryption_builder]
        AmazonS3Encryption s3Encryption = AmazonS3EncryptionClientBuilder
                .standard()
                .withRegion(Regions.US_WEST_2)
                .withCryptoConfiguration(new CryptoConfiguration(CryptoMode.AuthenticatedEncryption).withAwsKmsRegion(Region.getRegion(Regions.US_WEST_2)))
                // Can either be Key ID or alias (prefixed with 'alias/')
                .withEncryptionMaterials(new KMSEncryptionMaterialsProvider("alias/s3-kms-key"))
                .build();

        AmazonS3 s3NonEncrypt = AmazonS3ClientBuilder.defaultClient();
        // snippet-end:[s3.java1.s3_encrypt.kms_authenticated_encryption_builder]

        // snippet-start:[s3.java1.s3_encrypt.kms_authenticated_encryption_put_object]
        s3Encryption.putObject(BUCKET_NAME, ENCRYPTED_KEY, "some contents");
        s3NonEncrypt.putObject(BUCKET_NAME, NON_ENCRYPTED_KEY, "some other contents");
        System.out.println(s3Encryption.getObjectAsString(BUCKET_NAME, ENCRYPTED_KEY));
        System.out.println(s3Encryption.getObjectAsString(BUCKET_NAME, NON_ENCRYPTED_KEY));
        // snippet-end:[s3.java1.s3_encrypt.kms_authenticated_encryption_put_object]
    }
    // snippet-end:[s3.java1.s3_encrypt.kms_authenticated_encryption]

    /**
     * Same as authenticatedEncryption_KmsManagedKey except throws an exception when trying to get objects not encrypted with
     * AES/GCM.
     */
    // snippet-start:[s3.java1.s3_encrypt.kms_authenticated_encryption_strict]
    public void strictAuthenticatedEncryption_KmsManagedKey() throws NoSuchAlgorithmException {
        // snippet-start:[s3.java1.s3_encrypt.kms_authenticated_encryption_strict_builder]
        AmazonS3Encryption s3Encryption = AmazonS3EncryptionClientBuilder
                .standard()
                .withRegion(Regions.US_WEST_2)
                .withCryptoConfiguration(new CryptoConfiguration(CryptoMode.StrictAuthenticatedEncryption).withAwsKmsRegion(Region.getRegion(Regions.US_WEST_2)))
                // Can either be Key ID or alias (prefixed with 'alias/')
                .withEncryptionMaterials(new KMSEncryptionMaterialsProvider("alias/s3-kms-key"))
                .build();

        AmazonS3 s3NonEncrypt = AmazonS3ClientBuilder.defaultClient();
        // snippet-end:[s3.java1.s3_encrypt.kms_authenticated_encryption_strict_builder]

        // snippet-start:[s3.java1.s3_encrypt.kms_authenticated_encryption_strict_put_object]
        s3Encryption.putObject(BUCKET_NAME, ENCRYPTED_KEY, "some contents");
        s3NonEncrypt.putObject(BUCKET_NAME, NON_ENCRYPTED_KEY, "some other contents");
        // snippet-end:[s3.java1.s3_encrypt.kms_authenticated_encryption_strict_put_object]
        // snippet-start:[s3.java1.s3_encrypt.kms_authenticated_encryption_strict_exception]
        try {
            s3Encryption.getObjectAsString(BUCKET_NAME, NON_ENCRYPTED_KEY);
        } catch (SecurityException e) {
            // Strict authenticated encryption will throw an exception if an object is not encrypted with AES/GCM
            System.err.println(NON_ENCRYPTED_KEY + " was not encrypted with AES/GCM");
        }

        // snippet-end:[s3.java1.s3_encrypt.kms_authenticated_encryption_strict_exception]
    }
    // snippet-end:[s3.java1.s3_encrypt.kms_authenticated_encryption_strict]

}
// snippet-end:[s3.java1.s3_encrypt.complete]
