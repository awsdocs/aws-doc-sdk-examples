//snippet-sourcedescription:[EnableMFAForCognitoUser.java demonstrates how to enable MFA in Cognito Userpool which MFA is optional to users]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Cognito]
//snippet-service:[cognito]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-06-20]
//snippet-sourceauthor:[wenzaca]
/* Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
//snippet-start:[cognito.java.mfa.complete]
package aws.example.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;

import java.util.Arrays;

public class EnableMFAForCognitoUser {

    // TODO - Add your data here!
    static String USERNAME = ""; // Input an unique username for the UserPool
    static String PHONE_NUMBER = ""; // Input the user phone number for the user Attribute
    static String USERPOOL_ID = ""; // Input the UserPool Id, e.g. us-east-1_xxxxxxxx
    static String USER_TEMP_PASSWORD = ""; // Input the temporary password for the user
    static String USER_EMAIL = ""; // Input the email for the user attribute

    public static void main(String[] args) {

        AWSCognitoIdentityProvider cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.defaultClient();

        // Create User in UserPool using AdminCreateUser
        // @see <a href="https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/API_AdminCreateUser.html">label</a>
        cognitoIdentityProvider.adminCreateUser(
                new AdminCreateUserRequest()
                        .withUserPoolId(USERPOOL_ID)
                        .withUsername(USERNAME)
                        .withTemporaryPassword(USER_TEMP_PASSWORD)
                        .withUserAttributes(
                                new AttributeType()
                                        .withName("phone_number")
                                        .withValue(PHONE_NUMBER),
                                new AttributeType()
                                        .withName("phone_number_verified")
                                        .withValue("true"),
                                new AttributeType()
                                        .withName("email")
                                        .withValue(USER_EMAIL)));

        SMSMfaSettingsType sMSMfaSettings = new SMSMfaSettingsType().withPreferredMfa(Boolean.TRUE).withEnabled(Boolean.TRUE);

        // Set MFA preferred type for the User using AdminSetUserMFAPreference
        // @see <a href="https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/API_AdminSetUserMFAPreference.html">label</a>
        cognitoIdentityProvider.adminSetUserMFAPreference(
                new AdminSetUserMFAPreferenceRequest()
                        .withSMSMfaSettings(sMSMfaSettings)
                        .withUserPoolId(USERPOOL_ID)
                        .withUsername(USERNAME));

        // Add MFA Options type for the User using AdminSetUserSettings
        // @see <a href="https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/API_AdminSetUserSettings.html">label</a>
        cognitoIdentityProvider.adminSetUserSettings(
                new AdminSetUserSettingsRequest()
                        .withUserPoolId(USERPOOL_ID)
                        .withUsername(USERNAME)
                        .withMFAOptions(Arrays.asList(
                                new MFAOptionType()
                                        .withDeliveryMedium("SMS")
                                        .withAttributeName("phone_number"))));


        // Validate the data created/updated in this class.
        AdminGetUserResult user = cognitoIdentityProvider.adminGetUser(
                new AdminGetUserRequest()
                        .withUserPoolId(USERPOOL_ID)
                        .withUsername(USERNAME));
        assert (user.getUsername().equals(USERNAME));
        assert (!user.getMFAOptions().isEmpty());
        assert (user.getMFAOptions().get(0).getDeliveryMedium().equals("SMS"));
        assert (user.getPreferredMfaSetting().equals("SMS_MFA"));
        assert (user.getEnabled());
    }
}
//snippet-end:[cognito.java.mfa.complete]
