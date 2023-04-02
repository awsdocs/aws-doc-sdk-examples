// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Microsoft.Extensions.Configuration;

namespace SupportTests
{
    public class GlacierActionsTests
    {
        private readonly IConfiguration _configuration;
        private readonly string? _vaultName;
        private readonly string? _archiveId;
        private readonly string? _downloadFilePath;

        /// <summary>
        /// Constructor for the test class.
        /// </summary>
        public GlacierActionsTests()
        {
            _configuration = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("testsettings.json") // Load test settings from .json file.
                .AddJsonFile("testsettings.local.json",
                    true) // Optionally load local settings.
                .Build();

            _vaultName = _configuration["VaultName"];
            _archiveId = _configuration["ArchiveId"];
            _downloadFilePath = _configuration["DownloadFilePath"];
        }
    }
}