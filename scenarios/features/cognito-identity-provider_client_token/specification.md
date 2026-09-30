# Amazon Cognito Identity Provider - Client Token and Terms Specification

This document contains the specification for *Amazon Cognito Identity Provider - Client Token and Terms*, a feature scenario that showcases the new `GetClientToken` and `DescribeTermsByClient` API operations introduced in the CognitoClientTokenAndTerms feature release. The scenario demonstrates how to obtain machine-to-machine (M2M) access tokens through the SDK without end-user sign-in, and how to discover Terms documents (terms-of-use and privacy-policy) associated with a user pool app client.

The scenario uses a **CloudFormation prerequisite stack** to create all supporting Cognito resources (user pool, resource server with custom scopes, app client, and Terms documents) and deletes that stack at the end for clean, repeatable teardown. The new feature operations - `GetClientToken` and `DescribeTermsByClient` - remain as explicit SDK calls because demonstrating them is the purpose of the example.

### Resources

The scenario deploys an AWS CloudFormation stack with the following resources:

- **AWS::Cognito::UserPool** - An Amazon Cognito user pool to host the M2M app client.
- **AWS::Cognito::UserPoolResourceServer** - A resource server with custom scopes (`read` and `write`) for the M2M access token.
- **AWS::Cognito::UserPoolClient** - An app client configured with a client secret, the `ALLOW_CLIENT_TOKEN_AUTH` flow, and `client_credentials` OAuth flow with custom scopes.
- **AWS::Cognito::Terms** (x2) - Terms-of-use and privacy-policy documents linked to the app client.

The CloudFormation template is stored at `resources/cfn_template.yaml`.

After the stack is created, the scenario retrieves the following **stack outputs** to use in subsequent SDK calls:

| Output Key | Description |
|-|-|
| `UserPoolId` | The ID of the created user pool |
| `ClientId` | The app client ID |

The scenario then calls `DescribeUserPoolClient` to retrieve the `ClientSecret`, which is not exposed as a CloudFormation output for security reasons.

### Relevant documentation

* [What is Amazon Cognito?](https://docs.aws.amazon.com/cognito/latest/developerguide/what-is-amazon-cognito.html)
* [Amazon Cognito user pools](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-pools.html)
* [Scopes, M2M, and resource servers](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-pools-define-resource-servers.html)
* [Machine-to-machine (M2M) authorization](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-scenarios.html)
* [Amazon Cognito Identity Provider API Reference](https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/Welcome.html)
* [GetClientToken API Reference](https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/API_GetClientToken.html)
* [DescribeTermsByClient API Reference](https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/API_DescribeTermsByClient.html)
* [DescribeUserPoolClient API Reference](https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/API_DescribeUserPoolClient.html)
* [AWS::Cognito::UserPool CloudFormation resource](https://docs.aws.amazon.com/AWSCloudFormation/latest/TemplateReference/aws-resource-cognito-userpool.html)
* [AWS::Cognito::UserPoolResourceServer CloudFormation resource](https://docs.aws.amazon.com/AWSCloudFormation/latest/TemplateReference/aws-resource-cognito-userpoolresourceserver.html)
* [AWS::Cognito::UserPoolClient CloudFormation resource](https://docs.aws.amazon.com/AWSCloudFormation/latest/TemplateReference/aws-resource-cognito-userpoolclient.html)
* [AWS::Cognito::Terms CloudFormation resource](https://docs.aws.amazon.com/AWSCloudFormation/latest/TemplateReference/aws-resource-cognito-terms.html)

### API Actions Used

* [DescribeUserPoolClient](https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/API_DescribeUserPoolClient.html) - Retrieve the app client configuration including the client secret after stack deployment.
* [GetClientToken](https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/API_GetClientToken.html) - Obtain an M2M access token for the app client using its client ID and secret, without end-user sign-in.
* [DescribeTermsByClient](https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/API_DescribeTermsByClient.html) - Look up Terms documents associated with an app client by client ID, user pool ID, and terms name.
* [ListUserPoolClients](https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/API_ListUserPoolClients.html) - List app clients in a user pool (used in Hello example).

The following resources are created and deleted by the CloudFormation stack (not by individual SDK calls):
* User pool (`CreateUserPool` / `DeleteUserPool`)
* Resource server (`CreateResourceServer` / `DeleteResourceServer`)
* App client (`CreateUserPoolClient` / `DeleteUserPoolClient`)
* Terms documents (`CreateTerms` - x2)

## Hello Amazon Cognito Identity Provider

The Hello example is a standalone, minimal runnable example that verifies the SDK can connect to Amazon Cognito Identity Provider.

1. Create an Amazon Cognito Identity Provider service client.
2. Call `ListUserPoolClients` for an existing user pool, or `ListUserPools` to verify connectivity.
   - If a user pool ID is available, call `ListUserPoolClients` with `MaxResults` set to 5.
   - Display the returned app client names and IDs.
3. If no user pools exist, display a message indicating that no user pools were found and suggest creating one.

**Purpose:** Confirm that credentials, region configuration, and SDK setup are correct before running the full scenario.

## Scenario

This scenario walks through the end-to-end process of setting up machine-to-machine (M2M) authorization with Amazon Cognito using the new `GetClientToken` API, and discovering Terms documents associated with an app client using the new `DescribeTermsByClient` API. The `GetClientToken` operation provides the same functionality as the OAuth 2.0 client-credentials grant but works directly through the AWS SDK - no user pool domain or OIDC library is required.

Supporting Cognito resources (user pool, resource server, app client, and Terms documents) are deployed via a CloudFormation prerequisite stack for clean, repeatable setup and teardown. The new feature operations remain as explicit SDK calls.

### CloudFormation Template

The template is stored at `resources/cfn_template.yaml` and creates all prerequisite Cognito resources. The template contents are:

```yaml
AWSTemplateFormatVersion: "2010-09-09"
Description: >
  Prerequisite stack for the Amazon Cognito Client Token and Terms scenario.
  Creates a user pool, resource server with custom scopes, an M2M-enabled
  app client with a client secret, and terms-of-use / privacy-policy documents.

Resources:

  # ------------------------------------------------------------------
  # 1. User Pool
  # ------------------------------------------------------------------
  CognitoUserPool:
    Type: AWS::Cognito::UserPool
    Properties:
      UserPoolName: !Sub "m2m-demo-pool-${AWS::StackName}"
      DeletionProtection: INACTIVE

  # ------------------------------------------------------------------
  # 2. Resource Server with custom scopes
  # ------------------------------------------------------------------
  ResourceServer:
    Type: AWS::Cognito::UserPoolResourceServer
    Properties:
      UserPoolId: !Ref CognitoUserPool
      Identifier: my-m2m-api
      Name: M2M Demo API
      Scopes:
        - ScopeName: read
          ScopeDescription: Read access to the API
        - ScopeName: write
          ScopeDescription: Write access to the API

  # ------------------------------------------------------------------
  # 3. App Client (M2M / client-credentials)
  # ------------------------------------------------------------------
  M2MAppClient:
    DependsOn: ResourceServer
    Type: AWS::Cognito::UserPoolClient
    Properties:
      UserPoolId: !Ref CognitoUserPool
      ClientName: !Sub "m2m-demo-client-${AWS::StackName}"
      GenerateSecret: true
      ExplicitAuthFlows:
        - ALLOW_CLIENT_TOKEN_AUTH
      AllowedOAuthFlowsUserPoolClient: true
      AllowedOAuthFlows:
        - client_credentials
      AllowedOAuthScopes:
        - my-m2m-api/read
        - my-m2m-api/write

  # ------------------------------------------------------------------
  # 4a. Terms document - terms-of-use
  # ------------------------------------------------------------------
  TermsOfUse:
    Type: AWS::Cognito::Terms
    Properties:
      UserPoolId: !GetAtt CognitoUserPool.UserPoolId
      ClientId: !GetAtt M2MAppClient.ClientId
      TermsName: terms-of-use
      Enforcement: NONE
      TermsSource: LINK
      Links:
        "cognito:default": "https://example.com/terms/"

  # ------------------------------------------------------------------
  # 4b. Terms document - privacy-policy
  # ------------------------------------------------------------------
  PrivacyPolicy:
    Type: AWS::Cognito::Terms
    Properties:
      UserPoolId: !GetAtt CognitoUserPool.UserPoolId
      ClientId: !GetAtt M2MAppClient.ClientId
      TermsName: privacy-policy
      Enforcement: NONE
      TermsSource: LINK
      Links:
        "cognito:default": "https://example.com/privacy/"

Outputs:

  UserPoolId:
    Description: The ID of the Cognito user pool
    Value: !GetAtt CognitoUserPool.UserPoolId

  ClientId:
    Description: The ID of the M2M app client
    Value: !GetAtt M2MAppClient.ClientId
```

### Setup

1. **Deploy the CloudFormation prerequisite stack**
   - Prompt the user for a stack name (default: `cognito-m2m-demo-stack`).
   - Check that the stack name does not already exist. If it does, prompt the user for a different name.
   - Deploy the CloudFormation stack using the template at `resources/cfn_template.yaml`.
   - Wait for the stack to reach `CREATE_COMPLETE` status.
   - If the stack reaches any failed status, notify the user and end the scenario.
   - After a successful deployment, retrieve and display the stack output values:
     - `UserPoolId` - the ID of the created user pool.
     - `ClientId` - the ID of the M2M app client.
   - Store these values for subsequent operations.

   Example output:
   ```
   --------------------------------------------------------------------------------
   Welcome to the Amazon Cognito Client Token and Terms Scenario.
   --------------------------------------------------------------------------------
   This example creates Cognito resources in a CloudFormation stack, then
   demonstrates the new GetClientToken and DescribeTermsByClient APIs.

   Enter a name for the CloudFormation stack [cognito-m2m-demo-stack]:
   my-m2m-stack
   Deploying CloudFormation stack: my-m2m-stack
   CloudFormation stack creation started: my-m2m-stack
   Waiting for CloudFormation stack creation to complete...
   CloudFormation stack creation complete.
   Stack output UserPoolId: us-east-1_EXAMPLE
   Stack output ClientId: 1example23456789
   --------------------------------------------------------------------------------
   ```

2. **Retrieve the app client secret**
   - Call `DescribeUserPoolClient` with the `UserPoolId` and `ClientId` from the stack outputs.
   - Extract and store the `ClientSecret` from the response.
   - Display confirmation that the client secret was retrieved (do NOT display the secret itself in production scenarios).
   - Note: The client secret is not available as a CloudFormation stack output for security reasons, so an SDK call is required to retrieve it.

### Obtain an M2M access token with GetClientToken

3. **Request an access token using GetClientToken**
   - Call `GetClientToken` with:
     - `ClientId`: The app client ID from the stack outputs.
     - `Secret`: The app client secret from step 2.
     - `Scopes` (optional): Specific scopes to authorize. If omitted, all scopes configured for the app client are authorized.
   - The response contains a `ClientAuthenticationResult` object with:
     - `AccessToken`: The JWT access token for M2M authorization.
     - `ExpiresIn`: The duration in seconds until the token expires.
     - `TokenType`: The token type (e.g., `Bearer`).
   - Display the token type, expiration time, and a truncated preview of the access token.
   - Note: This operation does NOT use IAM credentials for authorization - the app client authenticates with its own client ID and secret.

4. **Request a token with specific scopes**
   - Call `GetClientToken` again, this time specifying a subset of scopes (e.g., `["my-m2m-api/read"]`).
   - Compare the returned token metadata with the previous request.
   - Display that the token was successfully scoped to the requested permissions.

### Discover Terms documents with DescribeTermsByClient

5. **Look up terms-of-use for the app client**
   - Call `DescribeTermsByClient` with:
     - `UserPoolId`: The user pool ID from the stack outputs.
     - `ClientId`: The app client ID from the stack outputs.
     - `TermsName`: `terms-of-use`
   - The response contains a `Terms` object with:
     - `TermsId`: A unique identifier for the terms document.
     - `TermsName`: The name of the terms type.
     - `ClientId`: The associated app client.
     - `UserPoolId`: The associated user pool.
     - `Enforcement`: The enforcement setting (e.g., `NONE`).
     - `TermsSource`: How the terms are provided (e.g., `LINK`).
     - `Links`: A map of language tags to URLs.
     - `CreationDate` and `LastModifiedDate`.
   - Display the terms document details, including the language-to-URL mappings.

6. **Look up privacy-policy for the app client**
   - Call `DescribeTermsByClient` with `TermsName`: `privacy-policy`.
   - Display the privacy policy details.
   - Note: `DescribeTermsByClient` lets you discover which Terms documents are associated with a client without knowing the Terms resource ID up front.

### Cleanup

7. **Delete the CloudFormation stack**
   - Prompt the user: "Do you want to delete the CloudFormation stack and all resources? (y/n)"
   - If yes:
     - Delete the CloudFormation stack.
     - Wait for the stack to reach `DELETE_COMPLETE` status (or confirm the stack no longer exists).
     - Display confirmation: "Stack deleted successfully. All Cognito resources have been removed."
   - If no:
     - Display a message: "Resources will remain. You can delete the stack later through the AWS Console or CLI."
     - Display the stack name for reference.
   - The cleanup operation should attempt to run even if errors occurred earlier in the scenario.
   - If the stack fails to delete, attempt a force-delete and notify the user.

   Example output:
   ```
   --------------------------------------------------------------------------------
   Do you want to delete the CloudFormation stack and all resources? (y/n)
   y
   CloudFormation stack 'my-m2m-stack' is being deleted. This may take a few minutes.
   Waiting for CloudFormation stack deletion to complete...
   Waiting for CloudFormation stack deletion to complete...
   CloudFormation stack 'my-m2m-stack' has been deleted.
   All Cognito resources have been removed.
   --------------------------------------------------------------------------------
   Amazon Cognito Client Token and Terms scenario completed.
   ```

### Outcome

After completing this scenario, the user will have:
- Deployed a CloudFormation stack that created a user pool, resource server with custom scopes, an M2M-enabled app client with a client secret, and Terms documents.
- Used the new `GetClientToken` API to obtain access tokens for M2M authorization directly through the SDK, without requiring a user pool domain or end-user sign-in.
- Used the new `DescribeTermsByClient` API to discover terms-of-use and privacy-policy documents associated with a specific app client.
- Cleaned up all resources by deleting the CloudFormation stack.

The user will understand how to:
- Set up M2M authentication using the `ALLOW_CLIENT_TOKEN_AUTH` flow via CloudFormation.
- Use `GetClientToken` as an alternative to the OAuth 2.0 client-credentials grant at the token endpoint.
- Manage and discover Terms documents per app client.
- Use CloudFormation for repeatable resource setup and teardown in SDK example scenarios.

## Errors

SDK code examples include basic exception handling for each action used. The table below describes the single most relevant exception to handle for each action in this scenario.

| Action | Error | Handling |
|-|-|-|
| `DescribeUserPoolClient` | `ResourceNotFoundException` | Notify the user that the specified user pool or client does not exist. Verify the stack deployed successfully and the outputs are correct. |
| `GetClientToken` | `NotAuthorizedException` | Notify the user that the client ID or secret is incorrect, or that the `ALLOW_CLIENT_TOKEN_AUTH` flow is not enabled for the app client. |
| `DescribeTermsByClient` | `ResourceNotFoundException` | Notify the user that no terms documents were found for the specified client, user pool, and terms name combination. |
| `ListUserPoolClients` | `ResourceNotFoundException` | Notify the user that the specified user pool does not exist. Verify the user pool ID. |

Note: CloudFormation stack errors (creation failures, deletion failures) should be handled by checking stack status events and displaying the failure reason to the user.

## Metadata

| action / scenario | metadata file | metadata key |
|-|-|-|
| `ListUserPoolClients` | cognito-idp_metadata.yaml | cognito-idp_Hello |
| `DescribeUserPoolClient` | cognito-idp_metadata.yaml | cognito-idp_DescribeUserPoolClient |
| `GetClientToken` | cognito-idp_metadata.yaml | cognito-idp_GetClientToken |
| `DescribeTermsByClient` | cognito-idp_metadata.yaml | cognito-idp_DescribeTermsByClient |
| `Cognito Identity Provider Client Token and Terms Scenario` | cognito-idp_metadata.yaml | cognito-idp_Scenario |
