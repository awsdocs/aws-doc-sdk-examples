# AWS Simple Email Service (SES) JavaScript SDK v3 code examples
Amazon SES is a cost-effective, flexible, and scalable email service that enables developers to send mail from within any application.

## Code examples
This is a workspace where you can find the following AWS SDK for JavaScript version 3 (v3) Amazon SES examples. 

- [Create a receipt filter](src/ses_createreceiptfilter.js)
- [Create receipt rule](src/ses_createreceiptrule.js)
- [Create receipt rule set](src/ses_createreceiptruleset.js)
- [Create templated](src/ses_createtemplate.js)
- [Delete identity](src/ses_deleteidentity.js)
- [Delete receipt filter](src/ses_deletereceiptfilter.js)
- [Delete receipt rule](src/ses_deletereceiptrule.js)
- [Delete receipt rule set](src/ses_deletereceiptruleset.js)
- [Delete template](src/ses_deletetemplate.js)
- [Get template](src/ses_gettemplate.js)
- [List identities](src/ses_listidentities.js)
- [List receipt filters](src/ses_listreceiptfilters.js)
- [List templates](src/ses_listtemplates.js)
- [Send bulk templated email](src/ses_sendbulktemplatedemail.js)
- [Send email](src/ses_sendemail.js)
- [Send templated email](src/ses_sendtemplatedemail.js)
- [Update template](src/ses_updatetemplate.js)
- [Verify domain identity](src/ses_verifydomainidentity.js)
- [Verify email identity](src/ses_verifyemailidentity.js)

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see 
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-example-javascript-syntax.html).

## Getting started

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

2. Install the dependencies listed in the package.json.

**Note**: These include the client module for the AWS services required in these example, 
which is *@aws-sdk/client-ses*.
```
npm install node -g
cd javascriptv3/example_code/ses
npm install
```



3. In your text editor, update user variables specified in the ```Inputs``` section of the sample file.

4. Run sample code:
```
cd src
node [example name].js // For example, node createreceiptfilter.js
```
## Resources
- [AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3) 
- [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples.html) 
- [AWS SDK for JavaScript v3 API Reference Guide](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-ses/index.html) 
