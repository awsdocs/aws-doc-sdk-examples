// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import Foundation
import AWSIAM
import AWSSFN

class Example {
    let sfnClient: SFNClient
    let iamClient: IAMClient

    let username: String
    let activityName: String
    var activity: Activity?
    var stateMachineName: String
    var stateMachinei: StateMachine?
    var stateMachine: StateMachine?
    var definitionPath: String
    var runArn: String? = nil
    var iamRole: IAMClientTypes.Role?

    init(region: String, username: String, activityName: String, stateMachineName: String,
                definitionPath: String) async throws {
        let sfnConfig = try await SFNClient.SFNClientConfiguration(region: region)
        sfnClient = SFNClient(config: sfnConfig)

        let iamConfig = try await IAMClient.IAMClientConfiguration(region: region)
        iamClient = IAMClient(config: iamConfig)

        self.username = username
        self.activityName = activityName
        self.stateMachineName = stateMachineName
        self.definitionPath = definitionPath
    }

    /// Clean up artifacts created by the program.
    func cleanUp() async {
        if iamRole != nil {
            print("Deleting the IAM role: \(iamRole?.roleName ?? "<unnamed>")...")
            do {
                _ = try await iamClient.deleteRole(
                    input: DeleteRoleInput(roleName: iamRole?.roleName)
                )
            } catch {
                print("*** Unable to delete the IAM role: \(error.localizedDescription)")
            }
        }

        if activity != nil {
            await activity?.delete()
        }

        if stateMachine != nil {
            print("Deleting the State Machine...")
            await stateMachine?.delete()
        }
    }

    /// Create a new IAM role.
    /// 
    /// - Returns: The `IAMClientTypes.Role` that was created, or `nil` if it
    ///   couldn't be created.
    func createIAMRole() async -> IAMClientTypes.Role? {
        let trustPolicy = """
        {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Sid": "",
                    "Effect": "Allow",
                    "Principal": {"Service": "states.amazonaws.com"},
                    "Action": "sts:AssumeRole"
                }
            ]
        }
        """

        do {
            let output = try await iamClient.createRole(
                input: CreateRoleInput(
                    assumeRolePolicyDocument: trustPolicy,
                    roleName: tempName(prefix: "state-machine-demo-role")
                )
            )

            return output.role
        } catch {
            print("*** Error creating the IAM role: \(error.localizedDescription)")
            return nil
        }
    }

    /// Delete the IAM role.
    /// 
    /// - Throws: The AWS error, if any.
    func deleteIAMRole() async throws {
        guard let iamRole = self.iamRole else {
            return
        }

        print("Deleting the IAM role: \(iamRole.roleName ?? "<unknown>")")

        _ = try await iamClient.deleteRole(
            input: DeleteRoleInput(roleName: iamRole.roleName)
        )
    }

    /// Generate and return a unique file name that begins with the specified
    /// string.
    ///
    /// - Parameters:
    ///   - prefix: Text to use at the beginning of the returned name.
    ///
    /// - Returns: A string containing a unique filename that begins with the
    ///   specified `prefix`.
    ///
    /// The returned name uses a random number between 1 million and 1 billion to
    /// provide reasonable certainty of uniqueness for the purposes of this
    /// example.
    func tempName(prefix: String) -> String {
        return "\(prefix)-\(Int.random(in: 1000000..<1000000000))"
    }

    /// Run the example.
    func run() async {
        print("Creating the IAM role...")
        iamRole = await createIAMRole()

        if iamRole == nil {
            print("Unable to create the IAM role. Exiting.")
            return
        }

        print("Created role: \(iamRole?.roleName ?? "<unnamed>")")

        // Find or create a Step Functions activity.

        print("Finding or creating a Step Functions activity...")
        
        do {
            activity = try await Activity(client: sfnClient, name: activityName)
        } catch let error as ActivityError {
            print("Unable to create the activity. \(error.errorDescription)")
            await cleanUp()
            return
        } catch {
            print("An AWS error occurred: \(error.localizedDescription)")
            await cleanUp()
            return
        }

        guard let activity = activity else {
            print("No activity available.")
            await cleanUp()
            return
        }

        print("Created Step Functions activity with ARN \(activity.activityArn).")

        // Find or create a State Machine.

        print("Finding or creating a State Machine...")
        do {
            stateMachine = try await StateMachine(
                sfnClient: sfnClient, 
                name: stateMachineName,
                iamRole: iamRole!,
                definitionPath: definitionPath,
                activity: activity
            )
        } catch let error as StateMachineError {
            print("Unable to create the state machine: \(error.errorDescription)")
            await cleanUp()
            return
        } catch {
            print("An AWS error occurred while creating the state machine: \(error.localizedDescription)")
            await cleanUp()
            return
        }

        guard let stateMachine = stateMachine else {
            print("No state machine available.")
            await cleanUp()
            return
        }

        // Display information about the State Machine.

        do {
            try await stateMachine.describe()
        } catch let error as StateMachineError {
            print("Unable to describe the state machine: \(error.errorDescription)")
            await cleanUp()
            return
        } catch {
            print("An AWS error occurred getting state machine details: \(error.localizedDescription)")
            await cleanUp()
            return
        }

        // Run the state machine.

        do {
            runArn = try await stateMachine.start(username: username)
        } catch let error as StateMachineError {
            print("Unable to start the state machine: \(error.errorDescription)")
            await cleanUp()
            return
        } catch {
            print("An AWS error occurred while starting the state machine: \(error.localizedDescription)")
            await cleanUp()
            return
        }

        guard let runArn else {
            print("Unable to run the state machine. Exiting.")
            await cleanUp()
            return
        }

        // Step through the state machine. This function runs until the state
        // machine enters its "done" state.

        do {
            try await stateMachine.execute()
        } catch let error as StateMachineError {
            print("Error executing the state machine: \(error.errorDescription)")
            await cleanUp()
            return
        } catch {
            print("AWS error while executing the state machine: \(error.localizedDescription)")
            await cleanUp()
            return
        }

        // Finish running the state machine.

        do {
            try await stateMachine.finishExecution(arn: runArn)
        } catch let error as StateMachineError {
            print("Error while stopping the state machine: \(error.errorDescription)")
            await cleanUp()
            return
        } catch {
            print("AWS error while stopping the state machine: \(error.localizedDescription)")
            await cleanUp()
            return
        }

        await cleanUp()
    }
}
