// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

using System.Diagnostics;

namespace BedrockRuntimeTests
{
    public abstract class ActionTest_Base
    {
        protected string _projectRoot;
        private readonly ProcessStartInfo _processStartInfo;

        protected ActionTest_Base()
        {
            _projectRoot = GetProjectRoot();
            _processStartInfo = new ProcessStartInfo
            {
                RedirectStandardOutput = true,
                UseShellExecute = false,
                CreateNoWindow = true
            };
        }

        private string GetProjectRoot()
        {
            string currentDirectory = Directory.GetCurrentDirectory();
            DirectoryInfo directory = new DirectoryInfo(currentDirectory);

            while (directory != null && directory.Name != "Bedrock-runtime")
            {
                if (directory.Parent == null)
                {
                    throw new Exception("Project root directory 'Bedrock-runtime' not found.");
                }
                directory = directory.Parent;
            }

            return directory?.FullName ?? throw new Exception("Project root directory not found.");
        }

        protected string getTestFilePath(string model, string action, string? subDir = null)
        {
            var exeName = subDir != null ? subDir + "_" + action : action;
            var exePath = Path.Combine("bin", "Debug", "net8.0", exeName + ".exe");
            var modelPath = Path.Combine(_projectRoot, "Models", model);
            var subDirPath = subDir != null ? subDir + "_" + action : action;
            return Path.Combine(modelPath, subDirPath, exePath);
        }

        protected (int exitCode, string standardOutput) runTest(string executable)
        {
            _processStartInfo.FileName = executable;

            using (var process = new Process())
            {
                process.StartInfo = _processStartInfo;
                process.Start();

                var standardOutput = process.StandardOutput.ReadToEnd();

                process.WaitForExit();

                var exitCode = process.ExitCode;

                return (exitCode, standardOutput);
            }
        }
    }
}