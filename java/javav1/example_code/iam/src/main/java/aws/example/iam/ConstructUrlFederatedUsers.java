/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/

// snippet-sourcedescription:[ConstructUrlFederatedUsers.java demonstrates how to programmatically construct a URL that gives federated users direct access to the AWS Management Console.]
// snippet-service:[iam]
// snippet-keyword:[Java]
// snippet-keyword:[AWS Identity and Access Management (IAM)]
// snippet-keyword:[Code Sample]
// snippet-keyword:[AssumeRole]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-22]
// snippet-sourceauthor:[stephswo (AWS)]
// snippet-start:[iam.java.ConstructUrlFederatedUsers.complete]

import java.net.URLEncoder;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
// Available at http://www.json.org/java/index.html
import org.json.JSONObject;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetFederationTokenRequest;
import com.amazonaws.services.securitytoken.model.GetFederationTokenResult;


/**
 * Constructs a URL for federated users
 */
public class ConstructUrl {

    public static void main(String[] args) {

        /* Calls to AWS STS API operations must be signed using the access key ID 
           and secret access key of an IAM user or using existing temporary 
           credentials. The credentials should not be embedded in code. For 
           this example, the code looks for the credentials in a 
           standard configuration file.
        */
        AWSCredentials credentials = 
          new PropertiesCredentials(
                 AwsConsoleApp.class.getResourceAsStream("AwsCredentials.properties"));
        
        AWSSecurityTokenServiceClient stsClient = 
          new AWSSecurityTokenServiceClient(credentials);
        
        GetFederationTokenRequest getFederationTokenRequest = 
          new GetFederationTokenRequest();
        getFederationTokenRequest.setDurationSeconds(1800);
        getFederationTokenRequest.setName("UserName");
        
        // A sample policy for accessing Amazon Simple Notification Service (Amazon SNS) in the console.
        
        String policy = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Action\":\"sns:*\"," +
          "\"Effect\":\"Allow\",\"Resource\":\"*\"}]}";
        
        getFederationTokenRequest.setPolicy(policy);
        
        GetFederationTokenResult federationTokenResult = 
          stsClient.getFederationToken(getFederationTokenRequest);
        
        Credentials federatedCredentials = federationTokenResult.getCredentials();
        
        // The issuer parameter specifies your internal sign-in
        // page, for example https://mysignin.internal.mycompany.com/.
        // The console parameter specifies the URL to the destination console of the
        // AWS Management Console. This example goes to Amazon SNS. 
        // The signin parameter is the URL to send the request to.
        
        String issuerURL = "https://mysignin.internal.mycompany.com/";
        String consoleURL = "https://console.aws.amazon.com/sns";
        String signInURL = "https://signin.aws.amazon.com/federation";
          
        // Create the sign-in token using temporary credentials,
        // including the access key ID,  secret access key, and security token.
        String sessionJson = String.format(
          "{\"%1$s\":\"%2$s\",\"%3$s\":\"%4$s\",\"%5$s\":\"%6$s\"}",
          "sessionId", federatedCredentials.getAccessKeyId(),
          "sessionKey", federatedCredentials.getSecretAccessKey(),
          "sessionToken", federatedCredentials.getSessionToken());
                      
        // Construct the sign-in request with the request sign-in token action, a
        // 12-hour console session duration, and the JSON document with temporary 
        // credentials as parameters.
        
        String getSigninTokenURL = signInURL + 
                                   "?Action=getSigninToken" +
                                   "&DurationSeconds=43200" + 
                                   "&SessionType=json&Session=" + 
                                   URLEncoder.encode(sessionJson,"UTF-8");
        
        URL url = new URL(getSigninTokenURL);
        
        // Send the request to the AWS federation endpoint to get the sign-in token
        URLConnection conn = url.openConnection ();
        
        BufferedReader bufferReader = new BufferedReader(new 
          InputStreamReader(conn.getInputStream()));  
        String returnContent = bufferReader.readLine();
        
        String signinToken = new JSONObject(returnContent).getString("SigninToken");
        
        String signinTokenParameter = "&SigninToken=" + URLEncoder.encode(signinToken,"UTF-8");
        
        // The issuer parameter is optional, but recommended. Use it to direct users
        // to your sign-in page when their session expires.
        
        String issuerParameter = "&Issuer=" + URLEncoder.encode(issuerURL, "UTF-8");
        
        // Finally, present the completed URL for the AWS console session to the user
        
        String destinationParameter = "&Destination=" + URLEncoder.encode(consoleURL,"UTF-8");
        String loginURL = signInURL + "?Action=login" +
                             signinTokenParameter + issuerParameter + destinationParameter;
    }
}
// snippet-end:[iam.java.ConstructUrlFederatedUsers.complete]
