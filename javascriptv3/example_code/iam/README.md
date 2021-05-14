# TypeScript environment for Amazon Identity and Access Management (IAM) examples
This is a workspace where you can find the following AWS SDK for JavaScript version 3 (v3) Amazon IAM examples. 

The [AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3) is available. 

The [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples.html) contains these examples.

The [AWS SDK for JavaScript v3 API Reference Guide](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-iam/index.html) contains the API operations for the AWS SDK for JavaScript v3 Amazon IAM client module.

Amazon IAM enables you to manage access to AWS services and resources securely.




# Getting started

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

2. Install the dependencies listed in the package.json in the folder containing the example(s).

**Note**: These dependencies include the client modules for the AWS services that this example requires, 
which are *@aws-sdk/client-iam* and *@aws-sdk/client-sts*.
```
npm install ts-node -g # If using JavaScript, enter 'npm install node -g' instead
cd javascriptv3/example_code/iam
npm install
```

3. If you're using JavaScript, change the sample file extension from ```.ts``` to ```.js```.


4. In your text editor, update user variables specified in the ```Inputs``` section of the sample file.

5. Run sample code:
```
cd src
ts-node [example name].ts // e.g., ts-node iam_accesskeylastused.ts
```
