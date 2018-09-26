/*******************************************************************************
* Copyright 2009-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License"). You may
* not use this file except in compliance with the License. A copy of the
* License is located at
*
* http://aws.amazon.com/apache2.0/
*
* or in the "license" file accompanying this file. This file is
* distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the specific
* language governing permissions and limitations under the License.
*******************************************************************************/
`/*
 *	Use this code snippet in your app.
 *	If you need more information about configurations or implementing the sample code, visit the AWS docs:
 *	https://aws.amazon.com/developers/getting-started/net/
 *	
 *	Make sure to include the following packages in your code.
 *	
 *	using System;
 *	using System.IO;
 *
 *	using Amazon;
 *	using Amazon.SecretsManager;
 *	using Amazon.SecretsManager.Model;
 *
 */

/*
 * AWSSDK.SecretsManager version="3.3.0" targetFramework="net45"
 */
 
public static void GetSecret()
{
    string secretName = "<<{{MySecretName}}>>";
    string region = "<<{{MyRegionName}}>>";
    string secret = "";

    MemoryStream memoryStream = new MemoryStream();

    IAmazonSecretsManager client = new AmazonSecretsManagerClient(RegionEndpoint.GetBySystemName(region));

    GetSecretValueRequest request = new GetSecretValueRequest();
    request.SecretId = secretName;
    request.VersionStage = "AWSCURRENT"; // VersionStage defaults to AWSCURRENT if unspecified.

    GetSecretValueResponse response = null;

    // In this sample we only handle the specific exceptions for the 'GetSecretValue' API.
    // See https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
    // We rethrow the exception by default.

    try
    {
        response = client.GetSecretValueAsync(request).Result;
    }
    catch (DecryptionFailureException e)
    {
        // Secrets Manager can't decrypt the protected secret text using the provided KMS key.
        // Deal with the exception here, and/or rethrow at your discretion.
        throw;
    }
    catch (InternalServiceErrorException e)
    {
        // An error occurred on the server side.
        // Deal with the exception here, and/or rethrow at your discretion.
        throw;
    }
    catch (InvalidParameterException e)
    {
        // You provided an invalid value for a parameter.
        // Deal with the exception here, and/or rethrow at your discretion
        throw;
    }
    catch (InvalidRequestException e)
    {
        // You provided a parameter value that is not valid for the current state of the resource.
        // Deal with the exception here, and/or rethrow at your discretion.
        throw;
    }
    catch (ResourceNotFoundException e)
    {
        // We can't find the resource that you asked for.
        // Deal with the exception here, and/or rethrow at your discretion.
        throw;
    }
    catch (System.AggregateException ae)
    {
        // More than one of the above exceptions were triggered.
        // Deal with the exception here, and/or rethrow at your discretion.
        throw;
    }

    // Decrypts secret using the associated KMS CMK.
    // Depending on whether the secret is a string or binary, one of these fields will be populated.
    if (response.SecretString != null)
    {
        secret = response.SecretString;
    }
    else
    {
        memoryStream = response.SecretBinary;
        StreamReader reader = new StreamReader(memoryStream);
        string decodedBinarySecret = System.Text.Encoding.UTF8.GetString(Convert.FromBase64String(reader.ReadToEnd()));
    }

    // Your code goes here.
}
