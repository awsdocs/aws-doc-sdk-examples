using Amazon;
using Amazon.SecurityToken;
using Amazon.SecurityToken.Model;
using System;
using System.Threading.Tasks;

namespace AssumeRoleExample
{
    class AssumeRole
    {
        /// <summary>
        /// This example shows how to use the AWS Security Token
        /// Service (AWS STS) to Assume an IAM role.
        /// </summary>
        private static readonly RegionEndpoint REGION = RegionEndpoint.USWest2;
        
        static async Task Main()
        {
            // Create the SecurityToken client and then get the Identity of the
            // default user. Remember that the default user must have permissions
            // to call AssumeRoleAsync for the role defined in roleArnToAssume.
            // var roleArnToAssume = "arn:aws:iam::123456789012:role/testAssumeRole";
            var roleArnToAssume = "arn:aws:iam::704825161248:role/testAssumeRole";

            var client = new Amazon.SecurityToken.AmazonSecurityTokenServiceClient(REGION);

            // Get and display the information about the identity of the default user.
            var callerIdRequest = new GetCallerIdentityRequest();
            var caller = await client.GetCallerIdentityAsync(callerIdRequest);
            Console.WriteLine($"Original Caller: {caller.Arn}");

            // Create the request to use with the AssumeRoleAsync call.
            var assumeRoleReq = new AssumeRoleRequest() {
                DurationSeconds = 1600,
                RoleSessionName = "Session1",
                RoleArn = roleArnToAssume
            };

            var assumeRoleRes = await client.AssumeRoleAsync(assumeRoleReq);

            // Now create a new client based on the credentials of the caller assuming the role.
            var client2 = new AmazonSecurityTokenServiceClient(credentials: assumeRoleRes.Credentials);

            // Get and display information about the caller that has assumed the defined role.
            var caller2 = await client.GetCallerIdentityAsync(callerIdRequest);
            Console.WriteLine($"AssumedRole Caller: {caller2.Arn}");
        }
    }
}
