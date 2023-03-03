// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace IAMTests
{
    // A class to test the methods in the S3Wrapper class
    // of the IamScenariosCommon project.
    internal class S3ScenarioTests
    {
        private readonly IConfiguration _configuration;

        // Values needed for user, role, and policies.
        private readonly string _userName;
        private readonly string _s3PolicyName;
        private readonly string _roleName;
        private readonly string _assumePolicyName;

        public S3ScenarioTests()
        {
            _configuration = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("testsettings.json") // Load test settings from .json file.
                .AddJsonFile("testsettings.local.json", 
                    true) // Optionally load local settings.
                .Build();

            _userName = _configuration["UserName"];
            _s3PolicyName = _configuration["S3PolicyName"];
            _roleName = _configuration["RoleName"];
            _assumePolicyName = _configuration[_assumePolicyName];
        }
    }
}
