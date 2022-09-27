//
// Swift Example: ListUsers
//
// An example showing how to use the Amazon S3 `IamClient` function
// `ListUsers()`.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

// snippet-start:[iam.swift.getaccountpasswordpolicy.example]
// snippet-start:[iam.swift.getaccountpasswordpolicy.main.imports]
import Foundation
import ServiceHandler
import ArgumentParser
// snippet-end:[iam.swift.getaccountpasswordpolicy.main.imports]

/// The command-line arguments and options available for this
/// example command.
// snippet-start:[iam.swift.getaccountpasswordpolicy.command]
struct ExampleCommand: ParsableCommand {
    static var configuration = CommandConfiguration(
        commandName: "getaccountpasswordpolicy",
        abstract: "Gets information about the current IAM account's password policy.",
        discussion: """
        """
    )

    /// Called by ``main()`` to do the actual running of the AWS
    /// example.
    // snippet-start:[iam.swift.getaccountpasswordpolicy.command.runasync]
    func runAsync() async throws {
        let serviceHandler = await ServiceHandler()

        do {
            let policy = try await serviceHandler.getAccountPasswordPolicy()

            let allowPasswordChanges: Bool = policy.allowUsersToChangePassword
            let passwordsExpire: Bool = policy.expirePasswords
            let passwordsExpireHard: Bool = policy.hardExpiry ?? false
            let maxPasswordAge: Int = policy.maxPasswordAge ?? 0
            let minPasswordLength: Int = policy.minimumPasswordLength ?? 6
            let passwordReuseDistance: Int = policy.passwordReusePrevention ?? 0
            let requireLowercase: Bool = policy.requireLowercaseCharacters
            let requireUppercase: Bool = policy.requireUppercaseCharacters
            let requireNumbers: Bool = policy.requireNumbers
            let requireSymbols: Bool = policy.requireSymbols

            let output = ValueList(header: "AWS account IAM password policy settings")
            output.addItem(title: "Password changes allowed", value: allowPasswordChanges)
            output.addItem(title: "Passwords expire", value: passwordsExpire)
            output.addItem(title: "Admin must reset expired passwords", value: passwordsExpireHard)
            output.addItem(title: "Days passwords are valid for", value: maxPasswordAge)
            output.addItem(title: "Minimum password length", value: minPasswordLength)
            output.addItem(title: "Days passwords can't be reused for", value: passwordReuseDistance)
            output.addItem(title: "At least 1 lowercase letter required", value: requireLowercase)
            output.addItem(title: "At least 1 UPPERCASE letter required", value: requireUppercase)
            output.addItem(title: "At least 1 digit required", value: requireNumbers)
            output.addItem(title: "At least 1 symbol required", value: requireSymbols)

            print(output.getFormattedOutput())
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.getaccountpasswordpolicy.command.runasync]
}
// snippet-end:[iam.swift.getaccountpasswordpolicy.command]

//
// Main program entry point.
//
// snippet-start:[iam.swift.getaccountpasswordpolicy.main]
@main
struct Main {
    static func main() async {
        let args = Array(CommandLine.arguments.dropFirst())

        do {
            let command = try ExampleCommand.parse(args)
            try await command.runAsync()
        } catch {
            ExampleCommand.exit(withError: error)
        }
    }    
}
// snippet-end:[iam.swift.getaccountpasswordpolicy.main]
// snippet-end:[iam.swift.getaccountpasswordpolicy.example]