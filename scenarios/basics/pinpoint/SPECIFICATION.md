# Amazon Pinpoint Basics Specification

This document is a draft specification for an Amazon Pinpoint Basics scenario — a code example that demonstrates the core workflow of Amazon Pinpoint for customer engagement through email campaigns. The scenario walks through a real-world story: creating an application (project), configuring the email channel, building a segment, creating and running an email campaign, sending a direct transactional message, verifying campaign activities, and cleaning up all resources.

> **Note:** Amazon Pinpoint end of support is scheduled for October 30, 2026. APIs related to SMS, voice, mobile push, OTP, and phone number validation are not impacted and are supported by AWS End User Messaging. This example focuses on the core Pinpoint project/campaign/segment APIs for educational purposes.

### Resources

The scenario requires the following prerequisite resources, created automatically:

- **Amazon SES verified email identity** — Required for the `UpdateEmailChannel` API. The program deploys a CloudFormation stack containing an `AWS::SES::EmailIdentity` resource for a user-provided email address. The user must confirm the verification email before proceeding.
  - **CloudFormation stack** deploys: `AWS::SES::EmailIdentity`
  - After `CREATE_COMPLETE`, the program prompts the user to check their inbox and confirm the verification link.
  - The stack is deleted during cleanup via `cloudformation_client.delete_stack()`.

All Amazon Pinpoint resources (application, email channel, segment, campaign) are created using the Pinpoint service client and deleted using the corresponding Pinpoint delete APIs.

### Relevant documentation

- [What is Amazon Pinpoint?](https://docs.aws.amazon.com/pinpoint/latest/userguide/welcome.html)
- [Amazon Pinpoint Developer Guide](https://docs.aws.amazon.com/pinpoint/latest/developerguide/welcome.html)
- [Amazon Pinpoint API Reference](https://docs.aws.amazon.com/pinpoint/latest/apireference/)
- [Amazon Pinpoint Resilient Architecture Guide](https://docs.aws.amazon.com/pinpoint/latest/archguide/welcome.html)
- [Send requests to the Amazon Pinpoint API](https://docs.aws.amazon.com/pinpoint/latest/userguide/tutorials-using-postman-sample-requests.html)

### API Actions Used

- [GetApps](https://docs.aws.amazon.com/pinpoint/latest/apireference/apps.html#GetApps) — Retrieves information about all applications associated with the account.
- [CreateApp](https://docs.aws.amazon.com/pinpoint/latest/apireference/apps.html#CreateApp) — Creates a new Amazon Pinpoint application (project).
- [UpdateEmailChannel](https://docs.aws.amazon.com/pinpoint/latest/apireference/apps-application-id-channels-email.html#UpdateEmailChannel) — Enables and configures the email channel for an application.
- [CreateSegment](https://docs.aws.amazon.com/pinpoint/latest/apireference/apps-application-id-segments.html#CreateSegment) — Creates a new segment for an application.
- [CreateCampaign](https://docs.aws.amazon.com/pinpoint/latest/apireference/apps-application-id-campaigns.html#CreateCampaign) — Creates a new campaign for an application.
- [SendMessages](https://docs.aws.amazon.com/pinpoint/latest/apireference/apps-application-id-messages.html#SendMessages) — Creates and sends a direct transactional message.
- [GetCampaignActivities](https://docs.aws.amazon.com/pinpoint/latest/apireference/apps-application-id-campaigns-campaign-id-activities.html#GetCampaignActivities) — Retrieves information about all the activities for a campaign.
- [GetCampaign](https://docs.aws.amazon.com/pinpoint/latest/apireference/apps-application-id-campaigns-campaign-id.html#GetCampaign) — Retrieves information about the status, configuration, and other settings for a campaign.
- [DeleteCampaign](https://docs.aws.amazon.com/pinpoint/latest/apireference/apps-application-id-campaigns-campaign-id.html#DeleteCampaign) — Deletes a campaign from an application.
- [DeleteSegment](https://docs.aws.amazon.com/pinpoint/latest/apireference/apps-application-id-segments-segment-id.html#DeleteSegment) — Deletes a segment from an application.
- [DeleteApp](https://docs.aws.amazon.com/pinpoint/latest/apireference/apps-application-id.html#DeleteApp) — Deletes an application.

## Hello Amazon Pinpoint

The Hello example is a minimal, standalone runnable program that verifies the caller can interact with Amazon Pinpoint.

- Create an Amazon Pinpoint client.
- Call **GetApps** with no parameters (or optionally with `PageSize` set to a small number like `5`).
- Print the names and IDs of any existing applications, or a message indicating no applications exist yet.
- Handle `BadRequestException` and `InternalServerErrorException` gracefully.

## Scenario

This scenario demonstrates a complete end-to-end email marketing campaign lifecycle using Amazon Pinpoint.

The scenario uses **10 API operations** in a coherent workflow:

1. `CreateApp` — Create the Pinpoint project
2. `UpdateEmailChannel` — Enable and configure email sending
3. `CreateSegment` — Define the target audience
4. `CreateCampaign` — Create an email campaign targeting the segment
5. `SendMessages` — Send a direct transactional email outside the campaign
6. `GetCampaign` — Check campaign status and configuration
7. `GetCampaignActivities` — Inspect campaign execution results
8. `DeleteCampaign` — Clean up the campaign
9. `DeleteSegment` — Clean up the segment
10. `DeleteApp` — Clean up the application

## Errors

| Action | Exception | Handling |
|-|-|-|
| `GetApps` | `BadRequestException` | Notify the user of invalid request parameters. |
| `CreateApp` | `BadRequestException` | Notify the user that the application name or parameters are invalid. |
| `UpdateEmailChannel` | `BadRequestException` | Notify the user that the email channel configuration is invalid. |
| `CreateSegment` | `BadRequestException` | Notify the user that the segment definition is invalid. |
| `CreateCampaign` | `BadRequestException` | Notify the user that the campaign configuration is invalid. |
| `SendMessages` | `BadRequestException` | Notify the user that the message request is invalid. |
| `GetCampaign` | `NotFoundException` | Notify the user that the specified campaign was not found. |
| `GetCampaignActivities` | `NotFoundException` | Notify the user that the specified campaign was not found. |
| `DeleteCampaign` | `NotFoundException` | Notify the user the campaign was already deleted. |
| `DeleteSegment` | `NotFoundException` | Notify the user the segment was already deleted. |
| `DeleteApp` | `NotFoundException` | Notify the user the application was already deleted. |

## Metadata

| action / scenario | metadata file | metadata key |
|-|-|-|
| `GetApps` | pinpoint_metadata.yaml | pinpoint_GetApps |
| `CreateApp` | pinpoint_metadata.yaml | pinpoint_CreateApp |
| `UpdateEmailChannel` | pinpoint_metadata.yaml | pinpoint_UpdateEmailChannel |
| `CreateSegment` | pinpoint_metadata.yaml | pinpoint_CreateSegment |
| `CreateCampaign` | pinpoint_metadata.yaml | pinpoint_CreateCampaign |
| `SendMessages` | pinpoint_metadata.yaml | pinpoint_SendMessages |
| `GetCampaignActivities` | pinpoint_metadata.yaml | pinpoint_GetCampaignActivities |
| `GetCampaign` | pinpoint_metadata.yaml | pinpoint_GetCampaign |
| `DeleteCampaign` | pinpoint_metadata.yaml | pinpoint_DeleteCampaign |
| `DeleteSegment` | pinpoint_metadata.yaml | pinpoint_DeleteSegment |
| `DeleteApp` | pinpoint_metadata.yaml | pinpoint_DeleteApp |
| `Amazon Pinpoint Hello` | pinpoint_metadata.yaml | pinpoint_Hello |
| `Amazon Pinpoint Basics Scenario` | pinpoint_metadata.yaml | pinpoint_Scenario |
