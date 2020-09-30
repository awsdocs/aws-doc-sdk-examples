# TypeScript environment for Amazon Cognito examples
Environment for AWS SDK for JavaScript (V3) Amazon Cognito examples. For more information, see the [AWS documentation for this example](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/getting-started-nodejs.html).

Amazon Cognito lets you add user sign-up, sign-in, and access control to your web and mobile apps quickly and easily. Amazon Cognito scales to millions of users and supports sign-in with social identity providers, such as Facebook, Google, and Amazon, and enterprise identity providers via SAML 2.0.

This is a workspace where you can find working AWS SDK for JavaScript (V3) Amazon Cognito examples. 

# Getting started

1. Clone the repo to your local environment. See [here](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

1. Install the dependencies listed in the package.json.

**Note**: These dependencies include the client modules for the AWS services that this example requires, 
which are *@aws-sdk/client-cognito-identity* and "@aws-sdk/credential-provider-cognito-identity".
```
npm install ts-node -g  // If using JavaScript, enter 'npm install node -g' instead
cd javascriptv3/example_code/cognito 
npm install
```

3. In your text editor, update user variables specified in the ```Inputs``` section of the sample file.

4. Run sample code:
```
cd src
ts-node [example name].ts // e.g., ts-node cognito_getcreds.ts
```
