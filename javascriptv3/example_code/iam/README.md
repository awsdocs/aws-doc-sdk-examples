# Amazon Identity and Access Management (IAM) JavaScript SDK v3 code examples
Amazon IAM enables you to manage access to AWS services and resources securely.

## Code examples
This is a workspace where you can find the following AWS SDK for JavaScript version 3 (v3) Amazon IAM examples. 

- [Access last key used](src/iam_accesskeylastused.js)
- [Attach a policy to a role](src/iam_attachrolepolicy.js)
- [Create access keys](src/iam_createaccesskeys.js)
- [Create an account alias](src/iam_createaccountalias.js)
- [Create an IAM policy](src/iam_createpolicy.js)
- [Create an IAM user](src/iam_createuser.js)
- [Delete access keys](src/iam_deleteaccesskey.js)
- [Delete an account alias](src/iam_deleteaccountalias.js)
- [Delete a server certificate](src/iam_deleteservercert.js)
- [Delete an IAM user](src/iam_deleteuser.js)
- [Detact a policy from an IAM role](src/iam_detachrolepolicy.js)
- [Get an IAM policy](src/iam_getpolicy.js)
- [Get a server certificate](src/iam_getservercert.js)
- [List access keys](src/iam_listaccesskeys.js)
- [List account aliases](src/iam_listaccountaliases.js)
- [List server certificated](src/iam_listservercerts.js)
- [List IAM users](src/iam_listusers.js)
- [Update access key](src/iam_updateaccesskey.js)
- [Update server certificate](src/iam_updateservercert.js)
- [Update an IAM user](src/iam_updateuser.js)
- [Assume a role](src/sts_assumerole.js)

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see 
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-example-javascript-syntax.html).

## Getting started

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

2. Install the dependencies listed in the package.json in the folder containing the example(s).

**Note**: These dependencies include the client modules for the AWS services that this example requires, 
which are *@aws-sdk/client-iam* and *@aws-sdk/client-sts*.
```
npm install node -g
cd javascriptv3/example_code/iam
npm install
```
3. In your text editor, update user variables specified in the ```Inputs``` section of the sample file.

4. Run sample code:
```
cd src
node [example name].js // For example, node iam_accesskeylastused.js
```

## Resources
[AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3)
[AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples.html)
[AWS SDK for JavaScript v3 API Reference Guide](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-iam/index.html) 
