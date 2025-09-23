// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[swift.sfn.scenario.activity]
import Foundation
import AWSSFN

/// Describes errors that occur on Step Functions activities.
enum ActivityError: Error {
    /// The ARN is missing from the returned activity.
    case missingArnError
    /// The activity list is missing from the response.
    case missingActivityListError
    /// No matching activity was found.
    case activityNotFoundError

    var errorDescription: String {
        switch self {
            case .missingArnError:
                return "The ARN is missing from the returned activity"
            case .missingActivityListError:
                return "The activity list is missing from the response"
            case .activityNotFoundError:
                return "No activity with the specified name was found"
        }
    }
}

/// Manage a Step Functions activity.
class Activity {
    let sfnClient: SFNClient
    let activityName: String
    var activityArn = ""

    init(client: SFNClient, name: String) async throws {
        sfnClient = client
        self.activityName = name

        try await self.findOrCreateActivity()
    }

    // snippet-start:[swift.sfn.CreateActivity]
    /// Create a new Step Functions activity.
    /// 
    /// - Throws: `ActivityError` and appropriate AWS errors.
    private func createActivity() async throws {
        let output = try await sfnClient.createActivity(
            input: CreateActivityInput(name: activityName)
        )

        guard let arn = output.activityArn else {
            throw ActivityError.missingArnError
        }

        activityArn = arn
    }
    // snippet-end:[swift.sfn.CreateActivity]

    // snippet-start:[swift.sfn.ListActivitiesPaginated]
    // snippet-start:[swift.sfn.ListActivities]
    /// Find an activity with the name specified when initializing the
    /// `Activity` object.
    ///
    /// - Throws: `ActivityError` and appropriate AWS errors.
    private func findActivity() async throws {
        let pages = sfnClient.listActivitiesPaginated(
            input: ListActivitiesInput()
        )

        for try await page in pages {
            guard let activities = page.activities else {
                throw ActivityError.missingActivityListError
            }

            for activity in activities {
                if activity.name == activityName {
                    guard let arn = activity.activityArn else {
                        throw ActivityError.missingArnError
                    }
                    self.activityArn = arn
                }
            }
        }
        
        throw ActivityError.activityNotFoundError
    }
    // snippet-end:[swift.sfn.ListActivities]
    // snippet-end:[swift.sfn.ListActivitiesPaginated]

    /// Finds an existing activity with the name given when initializing
    /// the `Activity`. If one isn't found, a new one is created.
    /// 
    /// - Throws: `ActivityError` and appropriate AWS errors.
    private func findOrCreateActivity() async throws {
        do {
            try await findActivity()
        } catch {
            try await createActivity()
        }
    }

    // snippet-start:[swift.sfn.DeleteActivity]
    /// Delete the activity described by this object.
    public func delete() async {
        do {
            _ = try await sfnClient.deleteActivity(
                input: DeleteActivityInput(activityArn: activityArn)
            )
        } catch {
            print("*** Error deleting the activity: \(error.localizedDescription)")
        }
    }
    // snippet-end:[swift.sfn.DeleteActivity]

    // snippet-start:[swift.sfn.SendTaskSuccess]
    /// Sends a task success notification to the activity.
    /// 
    /// - Parameters:
    ///   - taskToken: The task's token.
    ///   - response: The task response.
    /// 
    /// - Returns: `ActivityError` and appropriate AWS errors.
    public func sendTaskSuccess(taskToken: String, response: String) async -> Bool {
        do {
            _ = try await sfnClient.sendTaskSuccess(
                input: SendTaskSuccessInput(output: response, taskToken: taskToken)
            )

            return true
        } catch {
            print("*** Error sending task success: \(error.localizedDescription)")
            return false
        }
    }
    // snippet-end:[swift.sfn.SendTaskSuccess]
}
// snippet-end:[swift.sfn.scenario.activity]
