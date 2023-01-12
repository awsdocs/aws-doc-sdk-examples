/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include <fstream>
#include "cognito_gtests.h"
#include "cognito_samples.h"


namespace AwsDocTest {
    // NOLINTNEXTLINE (readability-named-parameter)
    TEST_F(Cognito_GTests, gettingStartedWithUserPools) {
        AddCommandLineResponses({"testUser", // Enter a new user name:
                                 "test1_Pw", // Enter a new password:
                                 "test_email@test.com", // Enter a valid email for the user:
                                 "y", // Would you like to send a new code? (y/n)
                                 "888888", // Enter the confirmation code that was emailed:
                                 "", // Type enter to continue...
                                 "888888", // Enter the 6 digit code displayed in the authenticator app:
                                 "888888", // Enter the 6 digit code displayed in the authenticator app:
                                 "231",
                                 "y",
                                 "3",
                                 "34",
                                 "6789",
                                 "y",
                                 "4",
                                 "81",
                                 "9",
                                 "n",
                                 "",
                                 "1",
                                 ""});

        MockHTTP mockHttp;

        mockHttp.addResponseWithBody(R"({"CodeDeliveryDetails":
{"AttributeName":"email","DeliveryMedium":"EMAIL","Destination":"s***@g***"},
"UserConfirmed":false,"UserSub":"665e97a8-182a-4968-ba9e-872c91ab78fc"})");

        mockHttp.addResponseWithBody(R"({
	"UserPoolId":	"us-east-1_gvW1BK2Bw",
	"Username":	"Steve7"
})");

        mockHttp.addResponseWithBody(R"( {
	"ClientId":	"3tda4cfum21p0ln0ih1c6ept5c",
	"Username":	"Steve7",
	"ConfirmationCode":	"671887"
})");

        mockHttp.addResponseWithBody(R"({
	"UserPoolId":	"us-east-1_gvW1BK2Bw",
	"Username":	"Steve7"
})");

        mockHttp.addResponseWithBody(R"({
	"AuthFlow":	"USER_PASSWORD_AUTH",
	"AuthParameters":	{
		"PASSWORD":	"Steve7_s",
		"USERNAME":	"Steve7"
	},
	"ClientId":	"3tda4cfum21p0ln0ih1c6ept5c"
})");

        // Client id and user pool id do not matter because this is mocked.
        const Aws::String clientID("3tda4cfum21p0ln0ih1c6ept5c");
        const Aws::String userpoolID("us-east-1_gvW1BK2Bw");
        bool result = AwsDoc::Cognito::gettingStartedWithUserPools(clientID, userpoolID, *s_clientConfig);
        ASSERT_TRUE(result);
    }
}