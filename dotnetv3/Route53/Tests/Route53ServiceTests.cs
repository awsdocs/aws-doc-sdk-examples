// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Microsoft.Extensions.Configuration;

namespace Route53Tests;

public class Route53ServiceTests
{
    private readonly IConfiguration _configuration;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public Route53ServiceTests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from .json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally load local settings.
            .Build();
    }

    [Fact]
    public void Test1()
    {

    }
}