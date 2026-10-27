# Amazon SES v2 Search and Filter Specification

**DRAFT** — This specification describes a scenario demonstrating the new filter support for list operations in Amazon Simple Email Service (SES) API v2. The scenario showcases how to use the newly introduced filter parameters on the `ListConfigurationSets`, `ListEmailIdentities`, and `ListTenants` API actions, enabling users to efficiently search and narrow down results rather than retrieving and manually filtering large result sets. This feature is particularly valuable for accounts with many configuration sets, identities, or tenants, where targeted lookups improve operational efficiency.

### Resources

No additional AWS resources beyond an active Amazon SES v2 account are required for this scenario. The account should have:
- At least one verified email identity (email address or domain)
- At least one configuration set
- At least one tenant (for the tenant filtering portion)

If the account does not already have these resources, the scenario's setup steps create them.

### Relevant documentation

- [What is Amazon SES?](https://docs.aws.amazon.com/ses/latest/dg/Welcome.html)
- [Amazon SES API v2 Reference](https://docs.aws.amazon.com/ses/latest/APIReference-V2/Welcome.html)
- [Using configuration sets in Amazon SES](https://docs.aws.amazon.com/ses/latest/dg/using-configuration-sets.html)
- [Creating and verifying identities in Amazon SES](https://docs.aws.amazon.com/ses/latest/dg/creating-identities.html)
- [Tenant management in Amazon SES](https://docs.aws.amazon.com/ses/latest/dg/tenants.html)
- [ListConfigurationSets API Reference](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_ListConfigurationSets.html)
- [ListEmailIdentities API Reference](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_ListEmailIdentities.html)
- [ListTenants API Reference](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_ListTenants.html)

### API Actions Used

- [ListConfigurationSets](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_ListConfigurationSets.html)
- [ListEmailIdentities](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_ListEmailIdentities.html)
- [ListTenants](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_ListTenants.html)
- [CreateConfigurationSet](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_CreateConfigurationSet.html)
- [CreateEmailIdentity](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_CreateEmailIdentity.html)
- [CreateTenant](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_CreateTenant.html)
- [DeleteConfigurationSet](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_DeleteConfigurationSet.html)
- [DeleteEmailIdentity](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_DeleteEmailIdentity.html)
- [DeleteTenant](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_DeleteTenant.html)
