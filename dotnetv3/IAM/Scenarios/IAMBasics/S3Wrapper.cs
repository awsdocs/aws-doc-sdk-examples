// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace IAMBasics;

/// <summary>
/// A class to perform S3 actions for the IAM Basics scenario.
/// </summary>
internal class S3Wrapper
{
    private readonly IAmazonS3 _s3Service;
    private readonly IAmazonSecurityTokenService _stsService;

    /// <summary>
    /// Constructor for the IAMWrapper class.
    /// </summary>
    /// <param name="IAMService">An IAM client object.</param>
    public S3Wrapper(IAmazonS3 s3Service)
    {
        _s3Service = s3Service;
    }

    public async Task<Credentials> AssumeS3Role(string roleName, string roleSession, string roleToAssume)
    {
        // Create the request to use with the AssumeRoleAsync call.
        var request = new AssumeRoleRequest()
        {
            RoleSessionName = roleSession,
            RoleArn = roleToAssume,
        };

        var response = await _s3Service.AssumeRoleAsync(request);

        return response.Credentials;

    }
}
