//snippet-sourcedescription:[CreateUser.java demonstrates how to add a new user to your user pool.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Cognito]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2020]
//snippet-sourceauthor:[scmacdon AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.cognito;

//snippet-start:[cognito.java2.new_admin_user.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
//snippet-end:[cognito.java2.new_admin_user.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateUser {

    public static void main(String[] args) {
        final String usage = "\n" +
                "Usage:\n" +
                "    <userPoolId> <userName> <email> <password>\n\n" +
                "Where:\n" +
                "    userPoolId - the Id value for the user pool where the user is created.\n\n" +
                "    userName - the user name for the new user.\n\n" +
                "    email - the email to use for verifying the user.\n\n" +
                "    password - the password for this user.\n\n" ;

        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }

        String userPoolId = args[0];
        String userName = args[1];
        String email = args[2];
        String password = args[3];

        CognitoIdentityProviderClient cognitoclient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .build();

        createNewUser(cognitoclient, userPoolId, userName, email, password);
        cognitoclient.close();
    }

    //snippet-start:[cognito.java2.add_login_provider.main]
    public static void createNewUser(CognitoIdentityProviderClient cognitoclient,
                                   String userPoolId,
                                   String name,
                                   String email,
                                   String password){

        try{

            AttributeType userAttrs = AttributeType.builder()
                    .name("email")
                    .value(email)
                    .build();

            AdminCreateUserRequest userRequest = AdminCreateUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(name)
                    .temporaryPassword(password)
                    .userAttributes(userAttrs)
                    .messageAction("SUPPRESS")
                    .build() ;

            AdminCreateUserResponse response = cognitoclient.adminCreateUser(userRequest);
            System.out.println("User " + response.user().username() + "is created. Status: " + response.user().userStatus());

        } catch (CognitoIdentityProviderException e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        //snippet-end:[cognito.java2.add_login_provider.main]
    }
}