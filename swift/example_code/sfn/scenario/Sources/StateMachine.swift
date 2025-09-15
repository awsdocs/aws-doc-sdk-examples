// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[swift.sfn.scenario.statemachine]
import Foundation
import AWSSFN
import AWSIAM

/// Describes an error the occurred while managing the state machine.
enum StateMachineError: Error {
    /// No matching role was found.
    case roleNotFoundError
    /// The ARN is missing from the returned state machine.
    case missingArnError
    /// The state machine list is missing from the response.
    case missingStateMachineListError
    /// No matching state machine was found.
    case stateMachineNotFoundError
    /// Unable to read the state machine definition file.
    case definitionFileReadError
    /// A state machine's details are missing
    case stateMachineDetailsMissingError
    /// The task token is missing from the activity task.
    case taskTokenMissingError
    /// The input is missing from the activity task.
    case inputMissingError
    /// The state machine's output is missing.
    case outputMissingError
    /// The state machine's execution has been aborted.
    case executionAborted
    /// The state machine's execution failed.
    case executionFailed
    /// The state machine timed out.
    case executionTimedOut
    /// The state machine's status is unrecognized.
    case executionStatusUnknown

    var errorDescription: String {
        switch self {
            case .roleNotFoundError:
                return "The specified role was not found or could not be created"
            case .missingArnError:
                return "The ARN is missing from the returned activity"
            case .missingStateMachineListError:
                return "The state machine list is missing from the response"
            case .stateMachineNotFoundError:
                return "No state machine with the specified name was found"
            case .definitionFileReadError:
                return "Unable to read the state machine definition file"
            case .stateMachineDetailsMissingError:
                return "The state machine's details are missing"
            case .taskTokenMissingError:
                return "The task token is missing from the activity task."
            case .inputMissingError:
                return "The input is missing from the activity task."
            case .outputMissingError:
                return "The state machine's output is missing."
            case .executionAborted:
                return "The state machine's execution was aborted."
            case .executionFailed:
                return "The state machine's execution failed."
            case .executionTimedOut:
                return "The state machine's execution timed out."
            case .executionStatusUnknown:
                return "The state machine has entered an unknown status."
        }
    }
}

/// Describes a message and a list of actions that can be taken in response
/// to that message.
struct ActionList: Decodable {
    let message: String
    let actions: [String]
}

/// Describes a message returned by an action.
struct Output: Decodable {
    let message: String
}

/// Encapsulates an AWS Step Functions state machine.
class StateMachine {
    let sfnClient: SFNClient
    let iamRole: IAMClientTypes.Role
    let activity: Activity
    let stateMachineName: String
    let definitionPath: String
    var stateMachineArn = ""

    init(sfnClient: SFNClient, name: String,
         iamRole: IAMClientTypes.Role, definitionPath: String,
         activity: Activity) async throws {

        self.sfnClient = sfnClient
        self.iamRole = iamRole
        self.stateMachineName = name
        self.definitionPath = definitionPath
        self.activity = activity

        try await findOrCreateStateMachine()
    }

    // snippet-start:[swift.sfn.ListStateMachinesPaginated]
    // snippet-start:[swift.sfn.ListStateMachines]
    /// Finds a state machine matching the name specified when initializing the `StateMachine`.
    /// - Throws: `StateMachineError` and appropriate AWS errors.
    private func findStateMachine() async throws {
        let pages = sfnClient.listStateMachinesPaginated(
            input: ListStateMachinesInput()
        )

        for try await page in pages {
            guard let stateMachines = page.stateMachines else {
                throw StateMachineError.missingStateMachineListError
            }

            for stateMachine in stateMachines {
                if stateMachine.name == stateMachineName {
                    guard let arn = stateMachine.stateMachineArn else {
                        throw StateMachineError.missingArnError
                    }
                    stateMachineArn = arn
                }
            }
        }

        throw StateMachineError.stateMachineNotFoundError
    }
    // snippet-end:[swift.sfn.ListStateMachines]
    // snippet-end:[swift.sfn.ListStateMachinesPaginated]

    // snippet-start:[swift.sfn.CreateStateMachine]
    /// Create a new state machine with the name given when initializing the
    /// `StateMachine` object.
    /// 
    /// - Throws: `StateMachineError` and appropriate AWS errors.
    private func createStateMachine() async throws {
        var definition: String

        print("Reading the state machine file from \(definitionPath)...")
        do {
            definition = try String(contentsOfFile: definitionPath, encoding: .utf8)
        } catch {
            throw StateMachineError.definitionFileReadError
        }

        // Swap in the activity's ARN into the definition string.

        definition.replace("{{DOC_EXAMPLE_ACTIVITY_ARN}}", with: activity.activityArn)

        let output = try await sfnClient.createStateMachine(
            input: CreateStateMachineInput(
                definition: definition,
                name: stateMachineName,
                roleArn: iamRole.arn
            )
        )

        guard let arn = output.stateMachineArn else {
            throw StateMachineError.missingArnError
        }

        stateMachineArn = arn
    }
    // snippet-end:[swift.sfn.CreateStateMachine]

    /// Finds a state machine matching the name given when initializing the
    /// `StateMachine` object. If it doesn't exist, a new one is created.
    /// 
    /// - Throws: `StateMachineError` and appropriate AWS errors.
    private func findOrCreateStateMachine() async throws {
        do {
            try await findStateMachine()
        } catch {
            try await createStateMachine()
        }
    }

    // snippet-start:[swift.sfn.DescribeStateMachine]
    /// Outputs a description of the state machine.
    /// 
    /// - Throws: `StateMachineError` and appropriate AWS errors.
    func describe() async throws {
        let output = try await sfnClient.describeStateMachine(
            input: DescribeStateMachineInput(
                stateMachineArn: stateMachineArn
            )
        )

        guard let name = output.name,
                let status = output.status else {
            throw StateMachineError.stateMachineDetailsMissingError
        }

        print()
        print("State machine details: ")
        print("      Name: \(name)")
        print("       ARN: \(stateMachineArn)")
        print("    Status: \(status)")
        print()
    }
    // snippet-end:[swift.sfn.DescribeStateMachine]

    // snippet-start:[swift.sfn.StartExecution]
    /// Start up the state machine.
    /// 
    /// - Parameter username: The username to use for the conversation.
    /// 
    /// - Throws: `StateMachineError` and appropriate AWS errors.
    /// - Returns: The execution ARN of the running state machine.
    func start(username: String) async throws -> String? {
        let runInput = """
                       { "name": "\(username)" }
                       """
        
        let output = try await sfnClient.startExecution(
            input: StartExecutionInput(
                input: runInput,
                stateMachineArn: stateMachineArn
            )
        )

        return output.executionArn
    }
    // snippet-end:[swift.sfn.StartExecution]

    // snippet-start:[swift.sfn.GetActivityTask]
    /// Execute the steps of the state machine until it exits.
    /// 
    /// - Throws: `StateMachineError` and appropriate AWS errors.
    func execute() async throws {
        var action: String = ""

        while action != "done" {
            let getTaskOutput = try await sfnClient.getActivityTask(
                input: GetActivityTaskInput(
                    activityArn: activity.activityArn
                )
            )

            guard let token = getTaskOutput.taskToken else {
                throw StateMachineError.taskTokenMissingError
            }
            guard let input = getTaskOutput.input else {
                throw StateMachineError.inputMissingError
            }

            let inputData = input.data(using: .utf8)!
            let inputObject = try! JSONDecoder().decode(ActionList.self, from: inputData)

            print("Task message: \(inputObject.message)")

            action = menuRequest(prompt: "Choose an action:", options: inputObject.actions)
            _ = await activity.sendTaskSuccess(taskToken: token, response: """
                        { "action": "\(action)" }
                        """
            )
        }
    }
    // snippet-end:[swift.sfn.GetActivityTask]

    // snippet-start:[swift.sfn.DescribeExecution]
    /// Wait for the execution to end, then output its final message.
    /// 
    /// - Parameter arn: The execution ARN to finish.
    /// 
    /// - Throws: `StateMachineError` and appropriate AWS errors.
    func finishExecution(arn: String) async throws {
        var status: SFNClientTypes.ExecutionStatus = .running

        while status == .running {
            let output = try await sfnClient.describeExecution(
                input: DescribeExecutionInput(
                    executionArn: arn
                )
            )

            status = output.status ?? .aborted

            switch status {
                case .running:
                    print("The state machine is still running. Waiting for it to finish.")
                    await sleep(forSeconds: 1)
                case .succeeded:
                    guard let outputString = output.output else {
                        throw StateMachineError.outputMissingError
                    }

                    let outputData = outputString.data(using: .utf8)!
                    let outputObject = try! JSONDecoder().decode(Output.self, from: outputData)
                    print("""
                        Execution completed with final message: \(outputObject.message)
                        """)
                case .aborted:
                    throw StateMachineError.executionAborted
                case .failed:
                    throw StateMachineError.executionFailed
                case .timedOut:
                    throw StateMachineError.executionTimedOut
                default:
                    throw StateMachineError.executionStatusUnknown
            }
        }
    }
    // snippet-end:[swift.sfn.DescribeExecution]

    // snippet-start:[swift.sfn.DeleteStateMachine]
    /// Delete the state machine.
    func delete() async {
        do {
            _ = try await sfnClient.deleteStateMachine(
                input: DeleteStateMachineInput(stateMachineArn: stateMachineArn)
            )
        } catch {
            print("*** Error deleting the state machine: \(error.localizedDescription)")
        }
    }
    // snippet-end:[swift.sfn.DeleteStateMachine]

    /// Sleep for the specified number of seconds.
    /// 
    /// - Parameter seconds: The number of seconds to sleep, as a floating
    ///   point value.
    func sleep(forSeconds seconds: Double) async {
        do {
            try await Task.sleep(for: .seconds(seconds))
        } catch {
            return
        }

    }

    /// Display a menu of options then request a selection.
    /// 
    /// - Parameters:
    ///   - prompt: A prompt string to display before the menu.
    ///   - options: An array of strings giving the menu options.
    ///
    /// - Returns: The string value of the selected option.
    func menuRequest(prompt: String, options: [String]) -> String {
        let numOptions = options.count

        if numOptions == 0 {
            return "done"
        }

        print(prompt)

        for (index, value) in options.enumerated() {
            print("(\(index+1)) \(value)")
        }

        repeat {
            print("Enter your selection (1 - \(numOptions)): ", terminator: "")
            if let answer = readLine() {
                guard let answer = Int(answer) else {
                    print("Please enter the number matching your selection.")
                    continue
                }

                if answer > 0 && answer <= numOptions {
                    return options[answer-1]
                } else {
                    print("Please enter the number matching your selection.")
                }
            }
        } while true
    }
}
// snippet-end:[swift.sfn.scenario.statemachine]
