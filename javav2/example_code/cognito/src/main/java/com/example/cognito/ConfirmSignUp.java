package com.example.cognito;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpRequest;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class ConfirmSignUp {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <ClientId> <ConfirmationCode> <Username>\n\n" +
                "Where:\n" +
                "    ClientId - The ID of the app client associated with the user pool.\n\n" +
                "    ConfirmationCode - The confirmation code sent by a user's request to confirm registration.\n\n" +
                "    Username - The user name of the user whose registration you want to confirm.\n\n"

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String clientId = args[0];
        String confirmationCode = args[1];
        String username = args[2];

        CognitoIdentityProviderClient identityProviderClient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        confirmSignUp(identityProviderClient, confirmationCode, username);
        identityProviderClient.close();
    }

    public static void confirmSignUp(CognitoIdentityProviderClient identityProviderClient, String confirmationCode, String username){

        ConfirmSignUpRequest req = ConfirmSignUpRequest.builder()
        	                .clientId(clientId)
        	                .confirmationCode(confirmationCode)
        	                .username(username)
        	                .build();

        identityProviderClient.confirmSignUp(req);

        System.out.println("User " + username + " sign up confirmed");
    }

}