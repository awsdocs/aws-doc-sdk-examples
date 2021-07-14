
# Amazon Simple Notification Service (SNS) JavaScript SDK v3 code examples
Amazon SNS is a fully managed messaging service for both system-to-system and app-to-person (A2P) communication. 

## Code examples
This is a workspace where you can find AWS SDK for JavaScript version 3 (v3) Amazon SNS examples.

- [Check phone opt out](src/sns_checkphoneoptout.js)
- [Confirm a subscription](src/sns_confirmsubscription.js)
- [Create a topic](src/sns_createtopic.js)
- [Delete a topic](src/sns_deletetopic.js)
- [Get SMS type](src/sns_getsmstype.js)
- [Get topic attributes](src/sns_gettopicattributes.js)
- [List opted out numbers](src/sns_listnumbersoptedout.js)
- [List subscriptions](src/sns_listsubscriptions.js)
- [List topics](src/sns_listtopics.js)
- [Publish SMS](src/sns_publishsms.js)
- [Publish to topics](src/sns_publishtotopic.js)
- [Set SMS type](src/sns_setsmstype.js)
- [Set topic attributes](src/sns_settopicattributes.js)
- [Subscribe to an app](src/sns_subscribeapp.js)
- [Subscribe to an email](src/sns_subscribeemail.js)
- [Subscribe to Lambda](src/sns_subscribelambda.js)
- [Unscribe](src/sns_unsubscribe.js)

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see 
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-example-javascript-syntax.html).


## Getting started

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

2. Install the dependencies listed in the package.json.

**Note**: These include the client module for the AWS services required in these example, 
which is "@aws-sdk/client-sns".
```
npm install node -g
cd javascriptv3/example_code/sns
npm install
```



3. In your text editor, update user variables specified in the ```Inputs``` section of the sample file.

4. Run sample code:
```
cd src
node [example name].js // For example, node sns_checkphoneoptout.js
```

## Resources
- [AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3) 
- [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sns-examples.html) 
- [AWS SDK for JavaScript v3 API Reference Guide](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-sns/index.html) 

