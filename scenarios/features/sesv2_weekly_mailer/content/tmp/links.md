---
combined: true
debug:
  engine: bedrock
  finish: max_tokens
  id: null
  model: anthropic.claude-3-sonnet-20240229-v1:0
  usage: null
isolated: true
prompt: >
  Generate titles for each of these actions in the SESv2 API.


  Output the data in a JSON array, where each entry is an object with a key
  `action` containing the action, and a key `title` with the generated title.


  Title should be human readable and in the imperative case. Titles inform
  builders what they will use the action to achieve.


  The following actions are supported:


  - BatchGetMetricData

  - CancelExportJob

  - CreateConfigurationSet

  - CreateConfigurationSetEventDestination

  - CreateContact

  - CreateContactList

  - CreateCustomVerificationEmailTemplate

  - CreateDedicatedIpPool

  - CreateDeliverabilityTestReport

  - CreateEmailIdentity

  - CreateEmailIdentityPolicy

  - CreateEmailTemplate

  - CreateExportJob

  - CreateImportJob

  - DeleteConfigurationSet

  - DeleteConfigurationSetEventDestination

  - DeleteContact

  - DeleteContactList

  - DeleteCustomVerificationEmailTemplate

  - DeleteDedicatedIpPool

  - DeleteEmailIdentity

  - DeleteEmailIdentityPolicy

  - DeleteEmailTemplate

  - DeleteSuppressedDestination

  - GetAccount

  - GetBlacklistReports

  - GetConfigurationSet

  - GetConfigurationSetEventDestinations

  - GetContact

  - GetContactList

  - GetCustomVerificationEmailTemplate

  - GetDedicatedIp

  - GetDedicatedIpPool

  - GetDedicatedIps

  - GetDeliverabilityDashboardOptions

  - GetDeliverabilityTestReport

  - GetDomainDeliverabilityCampaign

  - GetDomainStatisticsReport

  - GetEmailIdentity

  - GetEmailIdentityPolicies

  - GetEmailTemplate

  - GetExportJob

  - GetImportJob

  - GetMessageInsights

  - GetSuppressedDestination

  - ListConfigurationSets

  - ListContactLists

  - ListContacts

  - ListCustomVerificationEmailTemplates

  - ListDedicatedIpPools

  - ListDeliverabilityTestReports

  - ListDomainDeliverabilityCampaigns

  - ListEmailIdentities

  - ListEmailTemplates

  - ListExportJobs

  - ListImportJobs

  - ListRecommendations

  - ListSuppressedDestinations

  - ListTagsForResource

  - PutAccountDedicatedIpWarmupAttributes

  - PutAccountDetails

  - PutAccountSendingAttributes

  - PutAccountSuppressionAttributes

  - PutAccountVdmAttributes

  - PutConfigurationSetDeliveryOptions

  - PutConfigurationSetReputationOptions

  - PutConfigurationSetSendingOptions

  - PutConfigurationSetSuppressionOptions

  - PutConfigurationSetTrackingOptions

  - PutConfigurationSetVdmOptions

  - PutDedicatedIpInPool

  - PutDedicatedIpPoolScalingAttributes

  - PutDedicatedIpWarmupAttributes

  - PutDeliverabilityDashboardOption

  - PutEmailIdentityConfigurationSetAttributes

  - PutEmailIdentityDkimAttributes

  - PutEmailIdentityDkimSigningAttributes

  - PutEmailIdentityFeedbackAttributes

  - PutEmailIdentityMailFromAttributes

  - PutSuppressedDestination

  - SendBulkEmail

  - SendCustomVerificationEmail

  - SendEmail

  - TagResource

  - TestRenderEmailTemplate

  - UntagResource

  - UpdateConfigurationSetEventDestination

  - UpdateContact

  - UpdateContactList

  - UpdateCustomVerificationEmailTemplate

  - UpdateEmailIdentityPolicy

  - UpdateEmailTemplate
---
[
  {
    "action": "BatchGetMetricData",
    "title": "Retrieve Batched Metric Data"
  },
  {
    "action": "CancelExportJob",
    "title": "Cancel an Export Job"
  },
  {
    "action": "CreateConfigurationSet",
    "title": "Create a Configuration Set" 
  },
  {
    "action": "CreateConfigurationSetEventDestination",
    "title": "Create a Configuration Set Event Destination"
  },
  {
    "action": "CreateContact",
    "title": "Create a Contact"
  },
  {
    "action": "CreateContactList",
    "title": "Create a Contact List"
  },
  {
    "action": "CreateCustomVerificationEmailTemplate",
    "title": "Create a Custom Verification Email Template"
  },
  {
    "action": "CreateDedicatedIpPool",
    "title": "Create a Dedicated IP Pool"
  },
  {
    "action": "CreateDeliverabilityTestReport",
    "title": "Create a Deliverability Test Report"
  },
  {
    "action": "CreateEmailIdentity",
    "title": "Create an Email Identity"
  },
  {
    "action": "CreateEmailIdentityPolicy",
    "title": "Create an Email Identity Policy"
  },
  {
    "action": "CreateEmailTemplate",
    "title": "Create an Email Template"
  },
  {
    "action": "CreateExportJob",
    "title": "Create an Export Job"
  },
  {
    "action": "CreateImportJob",
    "title": "Create an Import Job"
  },
  {
    "action": "DeleteConfigurationSet",
    "title": "Delete a Configuration Set"
  },
  {
    "action": "DeleteConfigurationSetEventDestination",
    "title": "Delete a Configuration Set Event Destination"
  },
  {
    "action": "DeleteContact",
    "title": "Delete a Contact"
  },
  {
    "action": "DeleteContactList",
    "title": "Delete a Contact List"
  },
  {
    "action": "DeleteCustomVerificationEmailTemplate",
    "title": "Delete a Custom Verification Email Template"
  },
  {
    "action": "DeleteDedicatedIpPool",
    "title": "Delete a Dedicated IP Pool"
  },
  {
    "action": "DeleteEmailIdentity",
    "title": "Delete an Email Identity"
  },
  {
    "action": "DeleteEmailIdentityPolicy",
    "title": "Delete an Email Identity Policy"
  },
  {
    "action": "DeleteEmailTemplate",
    "title": "Delete an Email Template"
  },
  {
    "action": "DeleteSuppressedDestination",
    "title": "Delete a Suppressed Destination"
  },
  {
    "action": "GetAccount",
    "title": "Get Account Details"
  },
  {
    "action": "GetBlacklistReports",
    "title": "Get Blacklist Reports"
  },
  {
    "action": "GetConfigurationSet",
    "title": "Get a Configuration Set"
  },
  {
    "action": "GetConfigurationSetEventDestinations",
    "title": "Get Configuration Set Event Destinations"
  },
  {
    "action": "GetContact",
    "title": "Get a Contact"
  },
  {
    "action": "GetContactList",
    "title": "Get a Contact List"
  },
  {
    "action": "GetCustomVerificationEmailTemplate",
    "title": "Get a Custom Verification Email Template"
  },
  {
    "action": "GetDedicatedIp",
    "title": "Get a Dedicated IP"
  },
  {
    "action": "GetDedicatedIpPool",
    "title": "Get a Dedicated IP Pool"
  },
  {
    "action": "GetDedicatedIps",
    "title": "Get Dedicated IPs"
  },
  {
    "action