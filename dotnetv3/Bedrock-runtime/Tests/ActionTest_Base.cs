// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

using System.Reflection;
using Microsoft.CodeAnalysis.CSharp.Scripting;
using Microsoft.CodeAnalysis.Scripting;

namespace BedrockRuntimeTests
{
    public abstract class ActionTest_Base
    {
        private readonly string baseDirectory;

        protected ActionTest_Base()
        {
            baseDirectory = AppDomain.CurrentDomain.BaseDirectory;
        }

        protected async Task<string> test(string filePath)
        {
            validateExtension(filePath);

            var scriptCode = File.ReadAllText(filePath);
            var scriptOptions = addAssemblies();

            using (var outputWriter = new StringWriter())
            {
                var originalOutput = Console.Out;
                Console.SetOut(outputWriter);

                try
                {
                    await CSharpScript.RunAsync(scriptCode, scriptOptions);
                    return outputWriter.ToString();
                }
                finally
                {
                    Console.SetOut(originalOutput);
                }
            }
        }

        private ScriptOptions addAssemblies()
        {
            return ScriptOptions.Default
                .WithImports("System")
                .AddReferences([
                    Assembly.LoadFrom(Path.Combine(baseDirectory, "AWSSDK.BedrockRuntime.dll")),
                    Assembly.LoadFrom(Path.Combine(baseDirectory, "AWSSDK.Core.dll"))]);
        }

        private static void validateExtension(string filePath)
        {
            if (!Path.GetExtension(filePath).Equals(".csx", StringComparison.OrdinalIgnoreCase))
                throw new ArgumentException("Invalid file extension. Only CSX files are allowed.");
        }

        protected string getPath(string model, string action, string? subDir = null)
        {
            string projectRoot = GetProjectRoot();
            subDir = subDir != null ? $"{subDir}_{action}" : action;
            string modelDirectory = Path.Combine(projectRoot, "Models", model, subDir);
            return Path.Combine(modelDirectory, $"{action}.csx");
        }

        private string GetProjectRoot()
        {
            string currentDirectory = baseDirectory;
            while (!Directory.Exists(Path.Combine(currentDirectory, "Models")))
            {
                currentDirectory = Directory.GetParent(currentDirectory)?.FullName
                    ?? throw new Exception("Script directory 'Models' not found.");
            }
            return currentDirectory;
        }
    }
}
