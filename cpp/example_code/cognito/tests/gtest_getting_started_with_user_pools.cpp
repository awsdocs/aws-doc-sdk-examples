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
	"Session":	"AYABeErWPngI60Pd2QOssKI6FPcAHQABAAdTZXJ2aWNlABBDb2duaXRvVXNlclBvb2xzAAEAB2F3cy1rbXMAS2Fybjphd3M6a21zOnVzLWVhc3QtMTo3NDU2MjM0Njc1NTU6a2V5L2IxNTVhZmNhLWJmMjktNGVlZC1hZmQ4LWE5ZTA5MzY1M2RiZQC4AQIBAHiG0oCCDoro3IaeecGyxCZJOVZkUqttbPnF4J7Ar-5byAECXzth3O0rV5aQ_zkKra0FAAAAfjB8BgkqhkiG9w0BBwagbzBtAgEAMGgGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMkVmQ8QvJjwe-Az8nAgEQgDuG_FPUtW8Qkpoqpe5LvyqJQTzWdGTLOzHTj1-WyV4ShvYUMUvjToK5oyUWBYusziZZoKmhp7IpB2nMrgIAAAAADAAAEAAAAAAAAAAAAAAAAAD7nPhYC3f06YJTkQHGV1-O_____wAAAAEAAAAAAAAAAAAAAAEAAADih5uXM46Fjyh96zx_Gf6C9Sv7EkyZ78_84O7x_Fs87F9vHbMzq8IJG-YfjQ7cM3df7tm60hfuWOYnV8zLFC9mcWWuXwD7i8rBcQl7NkTDeTNY7L1g7TrWh0zvGX69fU9Nlp0ztxFS_5h_fVOPBt8h4JdGASxyuO9xmycRkRd-e-tFt4YD_psDfz2vuHVAnFFatPsv0AiHBuimFaiGbrKdNLsxrOFGBRoCrhihRpMAAcv3z_7zjOrqAhfx8vGpn2KekbKKN5bUtQDZ6Mz78SEQ4JXiEwaoHCivfpu-WztqzgeXdQe3s-jn_hQlWXD2LgqphX8"
})");

        // AWSCognitoIdentityProviderService.VerifySoftwareToken
        mockHttp.addResponseWithBody(R"({
	"Session":	"AYABeLhyOOndAXMD0uYHPEaH5xAAHQABAAdTZXJ2aWNlABBDb2duaXRvVXNlclBvb2xzAAEAB2F3cy1rbXMAS2Fybjphd3M6a21zOnVzLWVhc3QtMTo3NDU2MjM0Njc1NTU6a2V5L2IxNTVhZmNhLWJmMjktNGVlZC1hZmQ4LWE5ZTA5MzY1M2RiZQC4AQIBAHiG0oCCDoro3IaeecGyxCZJOVZkUqttbPnF4J7Ar-5byAEzaHteV77ZbhK8KX4wdX4oAAAAfjB8BgkqhkiG9w0BBwagbzBtAgEAMGgGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMLoJHcoS6clI0ueeoAgEQgDtwk0eP5Um-gSxowToW0AhUokYFd7kY14GcqZxReQo3FVpzvcl8AwWS1V2-9TzX8XONxsYJyZlLRxDWVAIAAAAADAAAEAAAAAAAAAAAAAAAAAA7uRfLuIWgSvtsgXft1Pi-_____wAAAAEAAAAAAAAAAAAAAAEAAAD3PFDu8sfmLggtkc_6IiscoPlbzuj1stET4BIfoSc3-93QgeV8COPKMM23mx8jpWiRJ_n6VVspYEWAi7CXJflaHSitZxOV1ejv7cPv-h17rZjVPtPkh_-6fcpewYIfBjYLcgK_lKi1wIakpg1aDsjCPZ5UKqU3tRDqrByvwzEZJ-ZyZVt8EmqHreqUeMIHQRxYMBq7HDRAEsAlzZzF8sBVT71tcoOaCl3Ns9kJKZ83CrZ5cC3XdqSoc5mn_y9KT_zms4I8O-p3-hcke2TGUNOQnaNhQTShZuTfrWrA_dv_e88RWsgpXNgystA8QJiLm2ROWa9SKv5Fdw2Nx0qvLmNluStrzs_Z88c",
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
	"Session":	"AYABeI2nAXkcFHBcvWmaUtBbEzsAHQABAAdTZXJ2aWNlABBDb2duaXRvVXNlclBvb2xzAAEAB2F3cy1rbXMAS2Fybjphd3M6a21zOnVzLWVhc3QtMTo3NDU2MjM0Njc1NTU6a2V5L2IxNTVhZmNhLWJmMjktNGVlZC1hZmQ4LWE5ZTA5MzY1M2RiZQC4AQIBAHiG0oCCDoro3IaeecGyxCZJOVZkUqttbPnF4J7Ar-5byAFVU71GW464sXNdG-Au3BooAAAAfjB8BgkqhkiG9w0BBwagbzBtAgEAMGgGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMYD73vMH88SESBZW2AgEQgDvIdSSORu_XjedaI15C7a6kedSjKuzE_8QznP6qfoTKhO2oU-llwYFdlFO_WQxRzXoiBxTjVS34Gaxv-wIAAAAADAAAEAAAAAAAAAAAAAAAAAAF-lAjPGLfqMJlaSXz2HvB_____wAAAAEAAAAAAAAAAAAAAAEAAADggZ9G07H55V1LRYE_7MR40BjzpB9f0anoqdA1IoDhAm5K9xQH5ex-5PahPJWWtvgaHkQD8pIB8Lb04C1TjV_rmt8iXTY1-DZ6_QdRjQ0ZPvv0cuItS1FNmkxD-lqFaBgZ6RHip-dragBD1RN2YWE3on-W06K0f8pET5SCSAAuSS8vFPZYt5Uj3cmCbPjz5WTp7RzTspEgV4FOzfDUZ7F33s7HhjEer9wx6f-DNM7cgAf9xEM2rZh2hLjEYC9Q9e_WsYrLPdMqIgzz7zdnS0SkGpK8STEhpIbAr0yrQozB3SxNAe4FWanUZhwBLhohOxt_",
	"ChallengeResponses":	{
		"SOFTWARE_TOKEN_MFA_CODE":	"448748",
		"USERNAME":	"foo"
	}
})");

        // AWSCognitoIdentityProviderService.DeleteUser
        mockHttp.addResponseWithBody(R"({
	"AccessToken":	"eyJraWQiOiJHcjI2cXNZVFRsUXNMWmFGTUE1YTJTeW01eUwydTd3dkVhZHdHWHR1VXUwPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJmZmMzNTMxNS0xZmUxLTQ1ZTktOGRlZi1iYmE4YjJjYjY1OTEiLCJkZXZpY2Vfa2V5IjoidXMtZWFzdC0xXzQ4N2EyODdlLWZjZjEtNDQyOS04MTQ0LTZkNzE0ZjBjOGVkYyIsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC51cy1lYXN0LTEuYW1hem9uYXdzLmNvbVwvdXMtZWFzdC0xX2d2VzFCSzJCdyIsImNsaWVudF9pZCI6IjN0ZGE0Y2Z1bTIxcDBsbjBpaDFjNmVwdDVjIiwib3JpZ2luX2p0aSI6IjZiYWU5OWQ0LWRmODYtNDk1Ni04ZGRhLWU4OWQ1NjQ0ZmE1NiIsImV2ZW50X2lkIjoiOWY5MTZmMDQtZGRiYy00YTM1LTk0ODktYTIwMDg2NmVhNjU5IiwidG9rZW5fdXNlIjoiYWNjZXNzIiwic2NvcGUiOiJhd3MuY29nbml0by5zaWduaW4udXNlci5hZG1pbiIsImF1dGhfdGltZSI6MTY3MzYzNjk1MCwiZXhwIjoxNjczNjQwNTUwLCJpYXQiOjE2NzM2MzY5NTAsImp0aSI6ImRmYmViOTc1LTdlNjEtNDA3ZS05NzRhLWE0OGVmMGNmMDViMCIsInVzZXJuYW1lIjoiamltMyJ9.eJVGTQWLlKK-Wj3e4NZXtTciYtU5elB-NqhpThnX5r0cV1lWecPKNaJuHD50rQyQKGflmEh1XZu3nD53UQhCAPyV-EIbgx7a9eHgxqOd1_KCkazWVVJ-UHPhsBpyqMp28Gr0rgxR68rgWSdpPxjdPdCnGV6nkATAPiYdy7_BibYgEQ8XvIbTkDK_baxtlWYujZ0Y8TWOOX4RQWrNR8Oul0GkUkfA_TPVeJtHedJ8P8s7Y8kVFyJrYQWjzhOGtDqxE5yxAk-xPJWeVGB7Y6xCAwgozkjUHHKfGK7Vm0Y-K4BbXGM_3beC0aNnnSnlPeY3llYKBeotP1l67hAbuovm6A"
})");

        // Client id and user pool id do not matter because this is mocked.
        const Aws::String clientID("1234567890abcdefg");
        const Aws::String userpoolID("us-east-1_111111111");
        bool result = AwsDoc::Cognito::gettingStartedWithUserPools(clientID, userpoolID,
                                                                   *s_clientConfig);
        ASSERT_TRUE(result);
    }
}