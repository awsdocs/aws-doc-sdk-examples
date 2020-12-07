//snippet-sourcedescription:[CreateAdminUser.java demonstrates how to add a new admin to your user pool.]
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

public class CreateAdminUser {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateAdminUser <userPoolId> <userName> <email>\n\n" +
                "Where:\n" +
                "    userPoolId - the Id value for the user pool where the user will be created.\n\n" +
                "    userName - the user name for the admin user.\n\n" +
                "    email - the email to use for verifying the admin account.\n\n" ;

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String userPoolId = args[0];
        String userName = args[1];
        String email = args[2];

        CognitoIdentityProviderClient cognitoclient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .build();

        createAdmin(cognitoclient, userPoolId, userName, email);
        cognitoclient.close();
    }

    //snippet-start:[cognito.java2.add_login_provider.main]
    public static void createAdmin(CognitoIdentityProviderClient cognitoclient,
                                   String userPoolId,
                                   String name,
                                   String email){

        try{
            AdminCreateUserResponse response = cognitoclient.adminCreateUser(
                    AdminCreateUserRequest.builder()
                            .userPoolId(userPoolId)
                            .username(name)
                            .userAttributes(AttributeType.builder()
                                    .name("email")
                                    .value(email)
                                    .build())
                            .messageAction("SUPPRESS")
                            .build()
            );

            System.out.println("User " + response.user().username() + "is created. Status: " + response.user().userStatus());

        } catch (CognitoIdentityProviderException e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        //snippet-end:[cognito.java2.add_login_provider.main]
    }
}