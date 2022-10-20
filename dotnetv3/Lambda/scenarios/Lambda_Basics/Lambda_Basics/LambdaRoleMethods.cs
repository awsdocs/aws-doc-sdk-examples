
namespace Lambda_Basics
{
    /// <summary>
    /// Methods to manage AWS Identity and Access Management (IAM) roles for
    /// the Lambda Basics scenario.
    /// </summary>
    internal class LambdaRoleMethods
    {
        private AmazonIdentityManagementServiceClient _client;

        public LambdaRoleMethods()
        {
            _client = new AmazonIdentityManagementServiceClient();
        }

        /// <summary>
        /// Create a new AWS Identity and Access Management (IAM)
        /// </summary>
        /// <param name="roleName"></param>
        /// <param name="policyDocument"></param>
        /// <returns></returns>
        public async Task<string> CreateLambdaRole(string roleName, string policyDocument)
        {
            var request = new CreateRoleRequest
            {
                AssumeRolePolicyDocument = policyDocument,
                RoleName = roleName,
            };

            var response = await _client.CreateRoleAsync(request);
            return response.Role.Arn;
        }

        public async Task<bool> DeleteLambdaRole(string roleName)
        {
            var request = new DeleteRoleRequest
            {
                RoleName = roleName,
            };

            var response = await _client.DeleteRoleAsync(request);
            return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
        }
    }
}
