package com.example.cognito;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class AdminInitiateAuth {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <AuthFlow> <ClientId> <UserPoolId>\n\n" +
                "Where:\n" +
                "    AuthFlow - The authentication flow for this call to run. \n\n" +
                "    ClientId - The app client ID.\n\n"
                "    UserPoolId - The user name of the user whose registration you want to confirm.\n\n"

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String authFlow = args[0];
        String clientId = args[1];
        String userPoolId = args[2];

        CognitoIdentityProviderClient identityProviderClient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        adminInitiateAuth(identityProviderClient, authFlow, clientId, userPoolId);

        identityProviderClient.close();
    }

    public static void adminInitiateAuth(CognitoIdentityProviderClient identityProviderClient, String authFlow, String clientId, String userPoolId){

        AdminInitiateAuthRequest req = AdminInitiateAuthRequest.builder()
                .authFlow(authFlow)
                .clientId(clientId)
                .userPoolId(userPoolId)
                .build();

        identityProviderClient.adminInitiateAuth(req);

        System.out.println("Admin initiate auth");
    }

}