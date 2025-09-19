// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[swift.support.scenario.scenario]
import AWSClientRuntime
import AWSSupport
import Foundation

/// The implementation of the scenario example's primary functionality.
class Scenario {
    let region: String
    let supportClient: SupportClient

    init(region: String) async throws {
        self.region = region

        let supportConfig = try await SupportClient.SupportClientConfiguration(region: region)
        supportClient = SupportClient(config: supportConfig)
    }

    /// Ask the user to enter an integer at the keyboard.
    /// 
    /// - Parameters:
    ///   - min: The minimum value to allow; default: 0.
    ///   - max: The maximum value to allow.
    /// 
    /// - Returns: The integer entered by the user.
    /// 
    /// This function keeps asking until the user enters a valid integer in
    /// the specified range.
    func inputInteger(min: Int, max: Int) -> Int {
        if max < min {
            return -1
        }
        if min < 0 {
            return -1
        }

        repeat {
            print("Enter your selection (\(min) - \(max)): ", terminator: "")
            if let answer = readLine() {
                guard let answer = Int(answer) else {
                    print("Please enter the number matching your selection.")
                    continue
                }

                if answer >= min && answer <= max {
                    return answer
                } else {
                    print("Please enter a number between \(min) and \(max).")
                }
            }
        } while true
    }

    /// Runs the example.
    /// 
    /// - Throws: Throws exceptions received from AWS
    func run() async throws {
        //======================================================================
        // 1. Get and display a list of available services, using
        //    DescribeServices.
        //======================================================================

        let services = await getServices().sorted { $0.name ?? "<unnamed>" < $1.name ?? "<unnamed>" }
        if services.count == 0 {
            print("No services found. Exiting.")
            return
        }

        print("Select a service:")
        for (index, service) in services.enumerated() {
            let numberPart = String(format: "%*d", 3, index+1)
            print("    \(numberPart)) \(service.name ?? "<unnamed>")")
        }

        let selectedService = services[inputInteger(min: 1, max: services.count) - 1]
        let selectedServiceName = selectedService.name ?? "<unnamed>"

        //======================================================================
        // 2. Display categories for the selected service, and let the user
        //    select a category.
        //======================================================================
        
        guard let categories = selectedService.categories else {
            print("The selected service has no categories listed!")
            return
        }
        print("The selected service (\(selectedServiceName)) has \(categories.count) categories:")
        for (index, category) in categories.enumerated() {
            let numberPart = String(format: "%*d", 3, index+1)
            print("    \(numberPart)) \(category.name ?? "<unnamed>")")
        }

        let selectedCategory = categories[inputInteger(min: 1, max: categories.count) - 1]

        //======================================================================
        // 3. Get and display severity levels and select one from the list
        //    (DescribeSeverityLevels).
        //======================================================================

        let severityLevels = await getSeverityLevels()
        print("Select a severity level:")
        for (index, severityLevel) in severityLevels.enumerated() {
            let numberPart = String(format: "%*d", 3, index+1)
            print("    \(numberPart)) \(severityLevel.name ?? "<unnamed>")")
        }

        let selectedSeverityLevel = severityLevels[inputInteger(min: 1, max: severityLevels.count) - 1]

        //======================================================================
        // 4. Create a support case using the selected service, category, and
        //    severity level. Set the subject to "Test case - please ignore".
        //    Get the new caseId (CreateCase).
        //======================================================================

        let caseID = await createCase(
            service: selectedService,
            category: selectedCategory,
            severity: selectedSeverityLevel,
            subject: "Test case - please ignore",
            body: "Please ignore this test case created by the AWS Support scenario example for the AWS SDK for Swift."
        )

        guard let caseID else {
            print("An error occurred while creating the case.")
            return
        }
        print("Created a case with ID ", caseID)

        print("Waiting for the change to propagate...")
        try await Task.sleep(nanoseconds: 10_000_000_000)

        //======================================================================
        // 5. Get a list of open cases for the current day. The list should
        //    contain the new case (DescribeCases).
        //======================================================================

        print("Getting today's open cases...")
        let today = Calendar.current.startOfDay(for: .now)
        let isoDateFormatter = ISO8601DateFormatter()

        let openCases = await getCases(
            afterTime: isoDateFormatter.string(from: today),
            includeResolved: false
        )

        for currentCase in openCases {
            print("    Case: \(currentCase.caseId ?? "<unknown>"): current status is \(currentCase.status ?? "<unknown>")")
        }

        //======================================================================
        // 6. Generate a file and add it to the case using an attachment set
        //    (AddAttachmentsToSet).
        //======================================================================

        print("Creating an attachment and attachment set...")

        let attachText = "Example file attachment text. Please ignore"
        let attachName = "\(UUID()).txt"

        let attachmentSetID = await createAttachment(name: attachName, body: Data(attachText.utf8))
        guard let attachmentSetID else {
            print("Attachment set couldn't be created.")
            return
        }
        print("Created attachment set with ID \(attachmentSetID)")

        //======================================================================
        // 7. Add communication with the attachment to the support case
        //    (AddCommunicationToCase).
        //======================================================================

        print("Adding a communication with the attachment to the case...")
        if !(await addCommunicationToCase(caseId: caseID,
                                  body: "Please see the attachment for details.",
                                  attachmentSet: attachmentSetID)) {
            print("Unable to attach the attachment to the case.")
            return
        }
        print("Added communication to the case.")

        print("Waiting for the change to propagate...")
        try await Task.sleep(nanoseconds: 10_000_000_000)

        //======================================================================
        // 8. List the support case's communications (DescribeCommunications).
        //    One communication will be added when the case is created, and a
        //    second is created in step 7. Get the attachment ID of the step 7
        //    communication. It will be needed in the next step.
        //======================================================================

        var attachmentIDList: [String] = []

        print("Listing the case's communications...")
        let communications = await getCommunications(caseId: caseID)
        guard let communications else {
            return
        }

        print("Found \(communications.count) communications on the case:")

        for communication in communications {
            print("    \(communication.submittedBy ?? "<unknown>") at \(communication.timeCreated ?? "<undefined>")")

            guard let attachmentList = communication.attachmentSet else {
                print("    Attachment list is missing.")
                continue
            }

            if attachmentList.count > 0 {           
                print("        \\--> \(attachmentList.count) attachments")
                for attachment in attachmentList {
                    guard let id = attachment.attachmentId else {
                        print("    Attachment ID missing.")
                        continue
                    }
                    attachmentIDList.append(id)
                }
            }
        }

        //======================================================================
        // 9. Describe the attachment set included with the communication
        //    (DescribeAttachment).
        //======================================================================

        print("Describing all attachments to the case...")
        for attachmentID in attachmentIDList {
            guard let attachment = await getAttachment(id: attachmentID) else {
                print("Attachment details not obtained successfully.")
                return
            }

            print("Filename: \(attachment.fileName ?? "<undefined>")")
            print("Contents:")

            guard let bodyData = attachment.data else {
                print("<no attachment body found>")
                continue
            }

            guard let body = String(data: bodyData, encoding: .utf8) else {
                print("<body is not a string>")
                continue
            }

            print(body)
            print(String(repeating: "=", count: 78))
        }

        //======================================================================
        // 10. Resolve the support case (ResolveCase).
        //======================================================================

        print("Resolving the case...")
        guard let status = await resolveCase(id: caseID) else {
            print("Unable to resolve the case.")
            return
        }
        print("Resolved the case. Status was previously \(status.previousStatus); now \(status.finalStatus).")

        //======================================================================
        // 11. Get a list of resolved cases for the current day after waiting
        //     for it to be resolved. This should now include the
        //     just-resolved case (DescribeCases).
        //======================================================================

        print("Getting today's resolved cases...")
        let allCases = await getCases(
            afterTime: isoDateFormatter.string(from: today),
            includeResolved: true
        )

        for currentCase in allCases {
            if currentCase.status == "resolved" {
                print("    Case: \(currentCase.caseId ?? "<unknown>"): final status is \(currentCase.status ?? "<unknown>")")
            }
        }

        print("End of example.")
    }

    // snippet-start:[swift.support.DescribeServices]
    /// Return an array of the user's services.
    ///
    /// - Parameter supportClient: The `SupportClient` to use when calling
    ///   `describeServices()`.
    ///
    /// - Returns: An array of services.
    func getServices() async -> [SupportClientTypes.Service] {
        do {
            let output = try await supportClient.describeServices(
                input: DescribeServicesInput()
            )

            guard let services = output.services else {
                return []
            }

            return services
        } catch let error as AWSServiceError {
            // SubscriptionRequiredException isn't a modeled error, so we
            // have to catch AWSServiceError and then look at its errorCode to
            // see if it's SubscriptionRequiredException.
            if error.errorCode == "SubscriptionRequiredException" {
                print("*** You need a subscription to use AWS Support.")
                return []
            } else {
                print("*** An unknown error occurred getting support information.")
                return []
            }
        } catch {
            print("*** Error getting service information: \(error.localizedDescription)")
            return []
        }
    }
    // snippet-end:[swift.support.DescribeServices]

    // snippet-start:[swift.support.DescribeSeverityLevels]
    /// Returns the severity levels that can be applied to cases.
    /// 
    /// - Returns: An array of `SupportClientTypes.SeverityLevel` objects
    ///   describing the available severity levels.
    /// 
    /// The returned array is empty if there are either no available severity
    /// levels, or if an error occurs.
    func getSeverityLevels() async -> [SupportClientTypes.SeverityLevel] {
        do {
            let output = try await supportClient.describeSeverityLevels(
                input: DescribeSeverityLevelsInput(
                    language: "en"
                )
            )

            guard let severityLevels = output.severityLevels else {
                return []
            }

            return severityLevels
        } catch let error as AWSServiceError {
            // SubscriptionRequiredException isn't a modeled error, so we
            // have to catch AWSServiceError and then look at its errorCode to
            // see if it's SubscriptionRequiredException.
            if error.errorCode == "SubscriptionRequiredException" {
                print("*** You need a subscription to use AWS Support.")
                return []
            } else {
                print("*** An unknown error occurred getting the category list.")
                return []
            }
        } catch {
            print("*** Error getting available severity levels: \(error.localizedDescription)")
            return []
        }
    }
    // snippet-end:[swift.support.DescribeSeverityLevels]

    // snippet-start:[swift.support.CreateCase]
    /// Create a new AWS Support case.
    /// 
    /// - Parameters:
    ///   - service: The AWS service for which to create a case.
    ///   - category: The category under which to file the case.
    ///   - severity: The severity to apply to the case.
    ///   - subject: A brief description of the case.
    ///   - body: A more detailed description of the case.
    /// 
    /// - Returns: A string containing the new case's ID, or `nil` if unable
    ///   to create the case.
    func createCase(service: SupportClientTypes.Service, category: SupportClientTypes.Category,
                    severity: SupportClientTypes.SeverityLevel, subject: String,
                    body: String) async -> String? {
        do {
            let output = try await supportClient.createCase(
                input: CreateCaseInput(
                    categoryCode: category.code,
                    communicationBody: body,
                    language: "en",
                    serviceCode: service.code,
                    severityCode: severity.code,
                    subject: subject
                )
            )

            return output.caseId
        } catch let error as AWSServiceError {
            // SubscriptionRequiredException isn't a modeled error, so we
            // have to catch AWSServiceError and then look at its errorCode to
            // see if it's SubscriptionRequiredException.
            if error.errorCode == "SubscriptionRequiredException" {
                print("*** You need a subscription to use AWS Support.")
                return nil
            } else {
                print("*** An unknown error occurred creating the new AWS Support case.")
                return nil
            }
        } catch is CaseCreationLimitExceeded {
            print("*** Unable to create a new case because you have exceeded your case creation limit.")
            return nil
        } catch {
            print("*** Error getting available severity levels: \(error.localizedDescription)")
            return nil
        }
    }
    // snippet-end:[swift.support.CreateCase]

    // snippet-start:[swift.support.DescribeCases]
    /// Get a list of cases and their details, optionally after a particular
    /// starting date and time.
    /// 
    /// - Parameters:
    ///   - afterTime: The timestamp of the earliest cases to return, or nil
    ///     to include all cases.
    ///   - includeResolved: A Bool indicating whether or not to include cases
    ///     whose status is `resolved`.
    /// 
    /// - Returns: An array of `SupportClientTypes.CaseDetails` objects
    ///   describing all matching cases.
    func getCases(afterTime: String? = nil, includeResolved: Bool = false) async -> [SupportClientTypes.CaseDetails] {
        do {
            let pages = supportClient.describeCasesPaginated(
                input: DescribeCasesInput(
                    afterTime: afterTime,
                    includeResolvedCases: includeResolved
                )
            )

            var allCases: [SupportClientTypes.CaseDetails] = []

            for try await page in pages {
                guard let caseList = page.cases else {
                    print("No cases returned.")
                    continue
                }

                for aCase in caseList {
                    allCases.append(aCase)
                }
            }

            return allCases
        } catch let error as AWSServiceError {
            // SubscriptionRequiredException isn't a modeled error, so we
            // have to catch AWSServiceError and then look at its errorCode to
            // see if it's SubscriptionRequiredException.
            if error.errorCode == "SubscriptionRequiredException" {
                print("*** You need a subscription to use AWS Support.")
                return []
            } else {
                print("*** An unknown error occurred getting the AWS Support case list.")
                return []
            }
        } catch {
            print("*** Error getting the list of cases: \(error.localizedDescription)")
            return []
        }
    }
    // snippet-end:[swift.support.DescribeCases]

    // snippet-start:[swift.support.AddAttachmentsToSet]
    /// Create a new AWS support case attachment set with a single attachment.
    /// 
    /// - Parameters:
    ///   - name: The attachment's filename.
    ///   - body: The body of the attachment, as a `Data` object.
    /// 
    /// - Returns: A string containing the new attachment set's ID.
    func createAttachment(name: String, body: Data) async -> String? {
        do {
            let output = try await supportClient.addAttachmentsToSet(
                input: AddAttachmentsToSetInput(
                    attachments: [
                        SupportClientTypes.Attachment(data: body, fileName: name)
                    ]
                )
            )
        
            guard let attachmentSetID = output.attachmentSetId else {
                print("No attachment set ID returned.")
                return nil
            }

            return attachmentSetID
        } catch let error as AWSServiceError {
            // SubscriptionRequiredException isn't a modeled error, so we
            // have to catch AWSServiceError and then look at its errorCode to
            // see if it's SubscriptionRequiredException.
            if error.errorCode == "SubscriptionRequiredException" {
                print("*** You need a subscription to use AWS Support.")
                return nil
            } else {
                print("*** An unknown error occurred creating the attachment set.")
                return nil
            }
        } catch is AttachmentLimitExceeded {
            print("*** Too many attachment sets have been created in too short a time. Try again later.")
            return nil
        } catch is AttachmentSetSizeLimitExceeded {
            print("*** You have exceeded the limit on the maximum number of attachments (3)")
            print("or attachment size (5 MB per attachment).")
            return nil
        } catch {
            print("*** Error creating the attachment set: \(error.localizedDescription)")
            return nil
        }
    }
    // snippet-end:[swift.support.AddAttachmentsToSet]
    
    // snippet-start:[swift.support.AddCommunicationToCase]
    /// Add a communication to an AWS Support case, including an optional
    /// attachment set.
    /// 
    /// - Parameters:
    ///   - caseId: The ID of the case to add the communication to.
    ///   - body: The body text of the communication.
    ///   - attachmentSet: The attachment ID of an attachment set to add to
    ///     the communication, or `nil` if no attachment is desired.
    /// 
    /// - Returns: A `Bool` indicating whether or not the communication was
    ///   successfully attached to the case.
    func addCommunicationToCase(caseId: String, body: String, attachmentSet: String?) async -> Bool {
        do {
            let output = try await supportClient.addCommunicationToCase(
                input: AddCommunicationToCaseInput(
                    attachmentSetId: attachmentSet,
                    caseId: caseId,
                    ccEmailAddresses: [],
                    communicationBody: body
                )
            )

            return output.result
        } catch let error as AWSServiceError {
            // SubscriptionRequiredException isn't a modeled error, so we
            // have to catch AWSServiceError and then look at its errorCode to
            // see if it's SubscriptionRequiredException.
            if error.errorCode == "SubscriptionRequiredException" {
                print("*** You need a subscription to use AWS Support.")
            } else {
                print("*** An unknown error occurred creating the communication.")
            }
            return false
        } catch is AttachmentSetExpired {
            print("*** The specified attachment set is expired.")
            return false
        } catch is CaseIdNotFound {
            print("*** The specified case ID doesn't exist.")
            return false
        } catch is AttachmentSetIdNotFound {
            print("The specified attachment set ID doesn't exist.")
            return false
        } catch {
            print("*** Error creating the communication: \(error.localizedDescription)")
            return false
        }
    }
    // snippet-end:[swift.support.AddCommunicationToCase]

    // snippet-start:[swift.support.DescribeCommunications]
    /// Get a list of the communications associated with a specific case.
    /// 
    /// - Parameter caseId: The ID of the case for which to get
    ///   communications.
    ///
    /// - Returns: An array of `SupportClientTypes.Communication` records,
    ///   each describing one communication on the case. Returns `nil` if an
    ///   error occurs.
    func getCommunications(caseId: String) async -> [SupportClientTypes.Communication]? {
        do {
            let output = try await supportClient.describeCommunications(
                input: DescribeCommunicationsInput(
                    caseId: caseId
                )
            )

            return output.communications
        } catch let error as AWSServiceError {
            // SubscriptionRequiredException isn't a modeled error, so we
            // have to catch AWSServiceError and then look at its errorCode to
            // see if it's SubscriptionRequiredException.
            if error.errorCode == "SubscriptionRequiredException" {
                print("*** You need a subscription to use AWS Support.")
            } else {
                print("*** An unknown error occurred creating the communication.")
            }
            return nil
        } catch is CaseIdNotFound {
            print("*** The specified case ID doesn't exist.")
            return nil
        } catch {
            print("*** Error getting the communications list: \(error.localizedDescription)")
            return nil
        }
    }
    // snippet-end:[swift.support.DescribeCommunications]

    // snippet-start:[swift.support.DescribeAttachment]
    /// Return information about a specific attachment, given its ID.
    /// 
    /// - Parameter id: The attachment's ID.
    /// 
    /// - Returns: A `SupportClientTypes.Attachment` object describing the
    ///   specified attachment, or `nil` if no matching attachment is found or
    ///   an error occurs.
    func getAttachment(id: String) async -> SupportClientTypes.Attachment? {
        do {
            let output = try await supportClient.describeAttachment(
                input: DescribeAttachmentInput(
                    attachmentId: id
                )
            )

            return output.attachment
        } catch let error as AWSServiceError {
            // SubscriptionRequiredException isn't a modeled error, so we
            // have to catch AWSServiceError and then look at its errorCode to
            // see if it's SubscriptionRequiredException.
            if error.errorCode == "SubscriptionRequiredException" {
                print("*** You need a subscription to use AWS Support.")
            } else {
                print("*** An unknown error occurred retrieving the attachment.")
            }
            return nil
        } catch is AttachmentIdNotFound {
            print("*** The specified attachment ID doesn't exist.")
            return nil
        } catch is DescribeAttachmentLimitExceeded {
            print("*** Too many attachment description requests in too short a period. Try again later.")
            return nil
        } catch {
            print("*** Error getting the communications list: \(error.localizedDescription)")
            return nil
        }
    }
    // snippet-end:[swift.support.DescribeAttachment]

    // snippet-start:[swift.support.ResolveCase]
    /// Resolve the specified AWS Support case.
    /// 
    /// - Parameter id: The ID of the case to resolve.
    /// 
    /// - Returns: A tuple containing the case's state prior to being
    ///   resolved, as well as its state after being resolved. Returns `nil`
    ///   if an error occurs resolving the case.
    func resolveCase(id: String) async -> (previousStatus: String, finalStatus: String)? {
        do {
            let output = try await supportClient.resolveCase(
                input: ResolveCaseInput(caseId: id)
            )

            return (output.initialCaseStatus ?? "<unknown>", output.finalCaseStatus ?? "<unknown>")
        } catch let error as AWSServiceError {
            // SubscriptionRequiredException isn't a modeled error, so we
            // have to catch AWSServiceError and then look at its errorCode to
            // see if it's SubscriptionRequiredException.
            if error.errorCode == "SubscriptionRequiredException" {
                print("*** You need a subscription to use AWS Support.")
            } else {
                print("*** An unknown error occurred resolving the case.")
            }
            return nil
        } catch is CaseIdNotFound {
            print("*** The specified case ID doesn't exist.")
            return nil
        } catch {
            print("*** Error resolving the case: \(error.localizedDescription)")
            return nil
        }
    }
    // snippet-end:[swift.support.ResolveCase]
}
// snippet-end:[swift.support.scenario.scenario]
