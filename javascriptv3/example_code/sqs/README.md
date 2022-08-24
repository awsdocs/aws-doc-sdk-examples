# Amazon Simple Queue Service (SQS) JavaScript SDK v3 code examples
Amazon SQS is a fully managed message queuing service that enables you to decouple and scale microservices, distributed systems, and serverless applications.

## Code examples
This is a workspace where you can find the following AWS SDK for JavaScript v3 SQS examples. 
- [Changing visibility](src/sqs_changingvisibility.js)(ChangeMessageVisibilityCommand)
- [Create a queue](src/sqs_createqueue.js)(CreateQueueCommand)
- [Create a dead letter queue](src/sqs_deadletterqueue.js)(SetQueueAttributesCommand)
- [Delete a queue](src/sqs_deletequeue.js)(DeleteQueueCommand)
- [Get a queue URL](src/sqs_getqueueurl.js)(GetQueueUrlCommand)
- [List queues](src/sqs_listqueues.js)(ListQueuesCommand)
- [Create long polling queue](src/sqs_longpolling_createqueue.js)(CreateQueueCommand)
- [Change time queue waits for messages](src/sqs_longpolling_existingqueue.js)(SetQueueAttributesCommand)
- [Receive messages queue using long-polling support](src/sqs_longpolling_receivemessage.js)(ReceiveMessageCommand)
- [Delete a message](src/sqs_deletemessage.js)(ReceiveMessageCommand / DeleteMessageCommand)
- [Send messages](src/sqs_sendmessage.js)(SendMessageCommand)
- [Set Queue Attributes](src/sqs_setqueueattributes.js)(SetQueueAttributesCommand)

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see 
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-example-javascript-syntax.html).

## Getting started

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) repo to your local environment. See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

2. Install ts-node or node the dependencies listed in the package.json.

**Note**: These include the client module for the AWS services required in these example, 
which is "@aws-sdk/client-sqs".
```
npm install node -g
cd javascriptv3/example_code/sqs
npm install
```


3. In your text editor, update user variables specified in the ```Inputs``` section of the sample file.

4. Run sample code:
```
cd src
node [example name].js // For example, node sqs_changingvisibility.js
```
## Resources
- [AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3) 
- [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples.html) 
- [AWS SDK for JavaScript v3 API Reference Guide](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-sqs/index.html)
