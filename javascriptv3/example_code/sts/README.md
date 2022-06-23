# AWS Security Token Service (AWS STS) JavaScript SDK v3 code examples
AWS Security Token Service (AWS STS) as a web service that enables you to request temporary, 
limited-privilege credentials for AWS Identity and Access Management (IAM) users or for users 
you authenticate (federated users).

## Code examples
This is a workspace where you can find the following AWS SDK for JavaScript version 3 (v3) Amazon IAM examples. 

- [Assume an IAM role](src/sts_assumerole.js) (GetCallerIdentityCommand, AssumeRoleCommand)

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
cd javascriptv3/example_code/sts
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
- [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sts-examples.html)
- [AWS SDK for JavaScript v3 API Reference Guide](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-sts/index.html) 
