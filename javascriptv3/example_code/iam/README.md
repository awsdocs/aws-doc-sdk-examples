# Amazon Identity and Access Management (IAM) JavaScript SDK v3 code examples
Amazon IAM enables you to manage access to AWS services and resources securely.

## Code examples
In this workspace, you can find the following AWS SDK for JavaScript version 3 (v3) examples for IAM:
### Scenario examples
- [Create an IAM user and assume a role with Amazon Security Token Service (STS)](scenarios/src/iam_basics.js)

### Single action examples
- [Access last key used](src/iam_accesskeylastused.js) (GetAccessKeyLastUsedCommand)
- [Attach a policy to a role](src/iam_attachrolepolicy.js) (ListAttachedRolePoliciesCommand)
- [Create access keys](src/iam_createaccesskeys.js) (CreateAccessKeyCommand)
- [Create an account alias](src/iam_createaccountalias.js) (CreateAccountAliasCommand)
- [Create an IAM policy](src/iam_createpolicy.js) (CreatePolicyCommand)
- [Create an IAM user](src/iam_createuser.js) (GetUserCommand)
- [Create and IAM role](src/iam_createrole.js)
- [Delete access keys](src/iam_deleteaccesskey.js) (DeleteAccessKeyCommand)
- [Delete an account alias](src/iam_deleteaccountalias.js) (DeleteAccountAliasCommand)
- [Delete a server certificate](src/iam_deleteservercert.js) (DeleteServerCertificateCommand)
- [Delete an IAM user](src/iam_deleteuser.js) (GetUserCommand, DeleteUserCommand)
- [Delete a role](src/iam_deleterole.js)
- [Detach a policy from an IAM role](src/iam_detachrolepolicy.js) (ListAttachedRolePoliciesCommand)
- [Delete a policy](src/iam_deletepolicy.js)
- [Get an IAM policy](src/iam_getpolicy.js) (GetPolicyCommand)
- [Get a server certificate](src/iam_getservercert.js) (GetServerCertificateCommand)
- [List access keys](src/iam_listaccesskeys.js) (ListAccessKeysCommand)
- [List account aliases](src/iam_listaccountaliases.js) (ListAccountAliasesCommand)
- [List server certificates](src/iam_listservercerts.js) (ListServerCertificatesCommand)
- [List IAM users](src/iam_listusers.js) (ListUsersCommand)
- [Update access key](src/iam_updateaccesskey.js) (UpdateAccessKeyCommand)
- [Update server certificate](src/iam_updateservercert.js) (UpdateServerCertificateCommand)
- [Update an IAM user](src/iam_updateuser.js) (UpdateUserCommand)

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

## Important

- As an AWS best practice, grant this code least privilege, or only the
  permissions required to perform a task. For more information, see
  [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the *AWS Identity and Access Management User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are
  available only in specific AWS Regions. For more information, see the
  [AWS Regional Services List](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.

## Running the code

### Prerequisites
- An AWS account. To create an account, see [How do I create and activate a new AWS account](https://aws.amazon.com/premiumsupport/knowledge-center/create-and-activate-aws-account/) on the AWS Premium Support website.
- AWS credentials. For details, see  [Setting credentials in Node.js](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/setting-credentials-node.html) in the
  *AWS SDK for Javascript (v3) Developer Guide*.

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

2. Install the dependencies listed in the package.json.

```
npm install node -g
cd javascriptv3/example_code/transcribe
npm install
```
3. In your text editor, update user variables specified in the ```Inputs``` section of the sample file.

4. Run sample code:
```
cd src
node [example name].js
```

## Unit tests
For more information see, the [README](../README.rst).

## Resources
- [AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3)
- [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples.html)
- [AWS SDK for JavaScript v3 API Reference Guide](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-iam/index.html) 
- [Amazon DynamoDB documentation]()