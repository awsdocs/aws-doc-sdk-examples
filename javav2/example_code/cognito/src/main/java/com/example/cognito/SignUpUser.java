//snippet-sourcedescription:[SignUp.java demonstrates how to register a user in the specified Amazon Cognito user pool.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Cognito]
//snippet-sourcetype:[full-example]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.cognito;

//snippet-start:[cognito.java2.signup.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
//snippet-end:[cognito.java2.signup.import]

/**
 * To run this Java code example, you need to create a client app in a user pool with a secret key. For details, see:
 * https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-settings-client-apps.html
 *
 * In addition, set up your development environment, including your credentials.
 *
 *  For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 */

public class SignUpUser {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <clientId> <secretkey> <userName> <password> <email>\n\n" +
            "Where:\n" +
            "    clientId - The app client id value that you can obtain from the AWS Management Console.\n\n" +
            "    secretkey - The app client secret value that you can obtain from the AWS Management Console.\n\n" +
            "    userName - The user name of the user you wish to register.\n\n" +
            "    password - The password for the user.\n\n" +
            "    email - The email address for the user.\n\n";

        if (args.length != 5) {
            System.out.println(usage);
            System.exit(1);
        }

        String clientId = args[0];
        String secretKey = args[1];
        String userName = args[2];
        String password = args[3];
        String email = args[4];

        CognitoIdentityProviderClient identityProviderClient = CognitoIdentityProviderClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        signUp(identityProviderClient, clientId, secretKey, userName, password, email);
        identityProviderClient.close();
    }

    //snippet-start:[cognito.java2.signup.main]
    public static void signUp(CognitoIdentityProviderClient identityProviderClient,
                                  String clientId,
                                  String secretKey,
                                  String userName,
                                  String password,
                                  String email) {

        AttributeType attributeType = AttributeType.builder()
            .name("email")
            .value(email)
            .build();

        List<AttributeType> attrs = new ArrayList<>();
        attrs.add(attributeType);
       try {
           String secretVal = calculateSecretHash(clientId, secretKey, userName);
           SignUpRequest signUpRequest = SignUpRequest.builder()
               .userAttributes(attrs)
               .username(userName)
               .clientId(clientId)
               .password(password)
               .secretHash(secretVal)
               .build();

           identityProviderClient.signUp(signUpRequest);
           System.out.println("User has been signed up");

       } catch(CognitoIdentityProviderException e) {
           System.err.println(e.awsErrorDetails().errorMessage());
           System.exit(1);
       } catch (NoSuchAlgorithmException e) {
           e.printStackTrace();
       } catch (InvalidKeyException e) {
           e.printStackTrace();
       }
    }

    public static String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) throws NoSuchAlgorithmException, InvalidKeyException {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

        SecretKeySpec signingKey = new SecretKeySpec(
            userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
            HMAC_SHA256_ALGORITHM);

        Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
        mac.init(signingKey);
        mac.update(userName.getBytes(StandardCharsets.UTF_8));
        byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
        return java.util.Base64.getEncoder().encodeToString(rawHmac);
    }
    //snippet-end:[cognito.java2.signup.main]
}
