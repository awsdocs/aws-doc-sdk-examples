/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
/*
 * Test types are indicated by the test label ending.
 *
 * _1_ Requires credentials, permissions, and AWS resources.
 * _2_ Requires credentials and permissions.
 * _3_ Does not require credentials.
 *
 */

#include <gtest/gtest.h>
#include <fstream>
#include "cognito_gtests.h"
#include "cognito_samples.h"


namespace AwsDocTest {
    // NOLINTNEXTLINE (readability-named-parameter)
    TEST_F(Cognito_GTests, gettingStartedWithUserPools_3_) {
        AddCommandLineResponses({"foo", // Enter a new user name:
                                 "foo_1foo", // Enter a new password:
                                 "foo@bar.com", // Enter a valid email for the user:
                                 "y", // Would you like to send a new code? (y/n)
                                 "888888", // Enter the confirmation code that was emailed:
                                 "", // Type enter to continue...
                                 "888888", // Enter the 6 digit code displayed in the authenticator app:
                                 "888888", // Re-enter the 6 digit code displayed in the authenticator app:
                                 "y"} // Would you like to delete the user you created? (y/n)
        );

        MockHTTP mockHttp;

        // AWSCognitoIdentityProviderService.SignUp
        mockHttp.addResponseWithBody(R"({
	"ClientId":	"1234567890abcdefg",
	"Username":	"foo",
	"Password":	"foo_1foo",
	"UserAttributes":	[{
			"Name":	"email",
			"Value":	"foo@bar.com"
		}]
})");

        // AWSCognitoIdentityProviderService.AdminGetUser
        mockHttp.addResponseWithBody(R"({
	"UserPoolId":	"us-east-1_111111111",
	"Username":	"foo"
})");

        // AWSCognitoIdentityProviderService.ResendConfirmationCode
        mockHttp.addResponseWithBody(R"({
	"ClientId":	"1234567890abcdefg",
	"Username":	"foo"
})");

        // AWSCognitoIdentityProviderService.ConfirmSignUp
        mockHttp.addResponseWithBody(R"({
	"ClientId":	"1234567890abcdefg",
	"Username":	"foo",
	"ConfirmationCode":	"105474"
})");

        // AWSCognitoIdentityProviderService.AdminGetUser
        mockHttp.addResponseWithBody(R"({
	"UserPoolId":	"us-east-1_111111111",
	"Username":	"foo"
})");

        // AWSCognitoIdentityProviderService.InitiateAuth
        mockHttp.addResponseWithBody(R"({
	"AuthFlow":	"USER_PASSWORD_AUTH",
	"AuthParameters":	{
		"PASSWORD":	"foo_1foo",
		"USERNAME":	"foo"
	},
	"ClientId":	"1234567890abcdefg"
})");

        // AWSCognitoIdentityProviderService.AssociateSoftwareToken
        mockHttp.addResponseWithBody(R"({
	"Session":	"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA11111111111111111111"
})");

        // AWSCognitoIdentityProviderService.VerifySoftwareToken
        mockHttp.addResponseWithBody(R"({
	"Session":	"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA11111111111111111111"
	"UserCode":	"050958"
})");
        // AWSCognitoIdentityProviderService.InitiateAuth
        mockHttp.addResponseWithBody(R"({
	"AuthFlow":	"USER_PASSWORD_AUTH",
	"AuthParameters":	{
		"PASSWORD":	"foo_1foo",
		"USERNAME":	"foo"
	},
	"ClientId":	"1234567890abcdefg"
})");
        // AWSCognitoIdentityProviderService.RespondToAuthChallenge
        mockHttp.addResponseWithBody(R"({
	"ClientId":	"1234567890abcdefg",
	"ChallengeName":	"SOFTWARE_TOKEN_MFA",
	"Session":	"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA11111111111111111111"
	"ChallengeResponses":	{
		"SOFTWARE_TOKEN_MFA_CODE":	"448748",
		"USERNAME":	"foo"
	}
})");

        // AWSCognitoIdentityProviderService.DeleteUser
        mockHttp.addResponseWithBody(R"({
	"AccessToken":	"BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB222222222222222222222"
})");

        // Client id and user pool id do not matter because this is mocked.
        const Aws::String clientID("1234567890abcdefg");
        const Aws::String userpoolID("us-east-1_111111111");
        bool result = AwsDoc::Cognito::gettingStartedWithUserPools(clientID, userpoolID,
                                                                   *s_clientConfig);
        ASSERT_TRUE(result);
    }
}