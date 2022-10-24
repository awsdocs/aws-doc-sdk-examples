
namespace Lambda_Basics
{
    /// <summary>
    /// Methods to manage AWS Identity and Access Management (IAM) roles for
    /// the Lambda Basics scenario.
    /// </summary>
    internal class LambdaRoleMethods
    {
        private readonly AmazonIdentityManagementServiceClient _client;

        public LambdaRoleMethods()
        {
            _client = new AmazonIdentityManagementServiceClient();
        }

        /// <summary>
        /// Create a new AWS Identity and Access Management (IAM) role.
        /// </summary>
        /// <param name="roleName">The name of the IAM role to create.</param>
        /// <param name="policyDocument">The policy document for the new IAM role.</param>
        /// <returns>A string representing the Amazon Resource Name (ARN) for
        /// newly created role.</returns>
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

        /// <summary>
        /// Deletes an IAM role.
        /// </summary>
        /// <param name="roleName">The name of the role to delete.</param>
        /// <returns>A Boolean value indicating the success of the operation.</returns>
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
