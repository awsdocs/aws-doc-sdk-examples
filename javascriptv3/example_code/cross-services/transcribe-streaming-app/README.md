# Cross-service example: Building an Amazon Transcribe streaming app

## Purpose
The cross-service example demonstrates how to build an app that records and transcribes an audio stream in real-time. It
also demonstrates how to translate the transcription and send it via email to your contacts. The app uses the following
AWS services:
- [Amazon Transcribe](https://aws.amazon.com/transcribe/)
- [Amazon Comprehend](https://aws.amazon.com/comprehend/)
- [Amazon Translate](https://aws.amazon.com/translate/)
- [Amazon Simple Email Services (SES)](https://aws.amazon.com/ses/)

The JavaScript SDK Transcribe Streaming client encapsulates the API into a JavaScript 
library that can be run on browsers, Node.js and potentially React Native. By default, 
the client uses HTTP/2 connection on Node.js, and uses WebSocket connection on browsers 
and React Native.


## Prerequisites

To build this cross-service example, you need the following:

* An AWS account. For more information see [AWS SDKs and Tools Reference Guide](https://docs.aws.amazon.com/sdkref/latest/guide/overview.html).
* A project environment to run this Node JavaScript example, and install the required AWS SDK for JavaScript and third-party modules.  For instructions, see [Create a Node.js project environment](#create-a-nodejs-project-environment) on this page.
* At least one email address verified on Amazon SES. For instructions, see [Verifying an email address on Amazon SES](#verifying-an-email-address-on-amazon-ses).
* The following AWS resources:
    - An unauthenticated AWS Identity and Access Management (IAM) user role with the following permissions:
        - ses:SendEmail
        - transcribe:StartStreamTranscriptionWebSocket
        - comprehend:DetectDominantLanguage
        - translate: TranslateText

**Note**: An unauthenticated role enables you to provide permissions to unauthenticated users to use the AWS Services. To create an authenticated role, see [Amazon Cognito Identity Pools (Federated Identities)](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-identity.html).    
 
 For instructions on creating the minimum resources required for this tutorial, see [Create the resources](#create-the-resources) on this page.


## ⚠ Important
* We recommend that you grant this code least privilege, or at most the minimum permissions required to perform the task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the *AWS Identity and Access Management User Guide*. 
* This code has not been tested in all AWS Regions. Some AWS services are available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
* Running this code might result in charges to your AWS account. We recommend you destroy the resources when you are finished. For instructions, see [Destroying the resources](#destroying-the-resources).
* Running the unit tests might result in charges to your AWS account.

## Create the resources
You can create the AWS resources required for this cross-service example using either of the following:
- [The Amazon CloudFormation](#create-the-resources-using-amazon-cloudformation)
- [The AWS Management Console](#create-the-resources-using-the-aws-management-console)

### Create the resources using Amazon CloudFormation
To run the stack using the AWS CLI:

1. Install and configure the AWS CLI following the instructions in the AWS CLI User Guide.

2. Open the AWS Command Console from the *./transcribe-streaming-app* folder.

3. Run the following command, replacing *STACK_NAME* with a unique name for the stack.
```
aws cloudformation create-stack --stack-name STACK_NAME --template-body file://setup.yaml --capabilities CAPABILITY_IAM
```
**Important**: The stack name must be unique within an AWS Region and AWS account. You can specify up to 128 characters, and numbers and hyphens are allowed.

4. Open [AWS CloudFormation in the AWS Management Console](https://aws.amazon.com/cloudformation/), and open the **Stacks** page.

![ ](images/cloud_formation_stacks.png)

5. Choose the **Resources** tab. The **Physical ID** of the **IDENTITY_POOL_ID** you require for this cross-service example is displayed.

![ ](images/cloud_formation_resources_tab.png)

For more information on the create-stack command parameters, see the [AWS CLI Command Reference guide](https://docs.aws.amazon.com/cli/latest/reference/cloudformation/create-stack.html), and the [AWS CloudFormation User Guide](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-cli-creating-stack.html).

### Create the resources using the AWS Management Console
####Create an unauthenticated user role
4. Open [AWS Cognito in the AWS Management Console](https://aws.amazon.com/cloudformation/), and open the *Stacks* page.
5. Choose **Manage Identity Pools**.
6. Choose **Create new identity pool**.
7. In the **Identity pool name** field, give your identity pool a name.
7. Select the **Enable access to unauthenticated identities** checkbox.
8. Choose **Create Pool**.
9. Choose **Allow**.
10. Take note of the **Identity pool ID**, which is highlighted in red in the **Get AWS Credentials** section.

![ ](images/identity_pool_ids.png)

11.Choose **Edit identity pool**.
12. Take note of the name of the role in the **Unauthenticated role** field.

####Adding permissions to an unauthenticated user role
13. Open [IAM in the AWS Management Console](https://aws.amazon.com/iam/), and open the *Roles* page.
14. Search for the unauthenticated role you just created.
15. Open the role. 
16. Click the down arrow beside the policy name.
17. Choose **Edit Policy**.
18. Choose the **JSON** tab.
18. Delete the existing content, and paste the code below into it.
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": [
                "mobileanalytics:PutEvents",
                "cognito-sync:*"
            ],
            "Resource": "*",
            "Effect": "Allow"
        },
        {
            "Action": "ses:SendEmail",
            "Resource": "*",
            "Effect": "Allow"
        },
        {
            "Action": "transcribe:StartStreamTranscriptionWebSocket",
            "Resource": "*",
            "Effect": "Allow"
        },
        {
            "Action": "comprehend:DetectDominantLanguage",
            "Resource": "*",
            "Effect": "Allow"
        },
        {
            "Action": "translate:TranslateText",
            "Resource": "*",
            "Effect": "Allow"
        }
    ]
}
```
19. Choose **Review Policy**.
20. Choose **Save Changes**.   

### Verifying an email address on Amazon SES 
1. Open [AWS SES in the AWS Management Console](https://aws.amazon.com/SES/), and open the *Email Addresses* page.
2. Choose **Verify a New Email Address**.
3. Enter a working email address, and choose **Verify This Email Address**.
4. Open the email in your email application, and verify it.

## Create a Node.js project environment

1. Clone the [AWS Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. 
See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for 
instructions.

2. Run the following commands in sequence in the AWS CLI command line to install the AWS service client modules and third-party modules listed in the *package.json*:

```
npm install node -g
cd javascriptv3/example_code/cross-services/transcribe-streaming-app
npm install
```
## Building the code
This app runs from the browser, so we create the interface using HTML and CSS. 
The app uses JavaScript to provide basic interactive features, and Node.js to invoke the AWS Services.

### Creating the HTML and CSS
In **index.html**, the **head** section invoke the **recorder.css**, which applies styles to the HTML,
and the **index.js**, which contains the following JavaScript and Node.js functions used in the app.

Each button on the interface invokes one of these functions when clicked.

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>title</title>
    <link rel="stylesheet" type="text/css" href="recorder.css">
    <script type="text/javascript" src="./main.js"></script>

</head>
<body>
<h1>Record, translate, transcribe, and email</h1>
<ol>
    <li>Record your voice.</li>
    <li>Select a target language to translate the transcription into.</li>
    <li>Select <b>Translate</b>.</li>
    <li>Enter an email to send the transcription to.</li>
    <li>Select <b>Send email</b>.</li>
</ol>
<p>
    <button id="record" onclick="startRecord()"></button>
    <button id="stopRecord" disabled onclick="stopRecord()">Stop and clear</button>
</p>
<h2>Transcription</h2>
<div id="output"></div>
<div id = "transButton">
<select id="list" style="padding: 10px;">
    <option value="nan">Select lang.</option>
    <option value="en">English</option>
    <option value="fr">French</option>
    <option value="de">German</option>
    <option value="it">Italian</option>
    <option value="es">Spanish</option>
    <option value="ko">Korean</option>
    <option value="pt">Portugese</option>
    <option value="zh">Chinese (Simp.)</option>
    <option value="zh-TW">Chinese (Trad.)</option>
</select><button id="button1" onclick="translateText()">Translate</button><button id="button2" onclick="clearTranscription()">Clear</button></div>

<h2>Translation</h2>
<div id="translated"></div>
<div id="emailAddress">
    <p><input type="text" id="email" name="email" placeholder="Enter email"><button id="button" onclick="sendEmail()">Send email</button></p>
    <p><b>* </b>The email must be registered with Amazon SES.</p>
</div>
<script type="text/javascript" src="./main.js"></script>
</body>

</html>
```

### Creating the JavaScript and Node.js
The **./src/libs/** folders contains a file for each of the AWS Service clients required. You must
replace "REGION" with your AWS Region, and replace "IDENTITY_POOL_ID" with the Amazon Cognito identity pool id
you created in [Create the resources](#create-the-resources) on this page. Here's an example of one of these client configuration files:
 
 
```javascript
import { CognitoIdentityClient } from "@aws-sdk/client-cognito-identity";
import {
    fromCognitoIdentityPool,
} from "@aws-sdk/credential-provider-cognito-identity";
import {TranscribeStreamingClient} from "@aws-sdk/client-transcribe-streaming";
import {TranslateClient} from "@aws-sdk/client-translate";

const REGION = "REGION";
const IDENTITY_POOL_ID = "IDENTITY_POOL_ID"; // An Amazon Cognito Identity Pool ID.

// Create an Amazon Transcribe service client object.
const transcribeClient = new TranscribeStreamingClient({
    region: REGION,
    credentials: fromCognitoIdentityPool({
        client: new CognitoIdentityClient({ region: REGION }),
        identityPoolId: IDENTITY_POOL_ID
    }),
});

export { transcribeClient };
```
**./src/index.js** first imports all the required AWS Service and third party modules. 

It contains the following functions that are triggered by the buttons on the interface:
- **startRecord** - starts voice recording in your browser, and converts it into an audio stream in real-time. It encodes the audio stream so that it can be used by the
 AWS JavaScript SDK Transcribe Streaming client, which transcribes the audio in real-time.
- **stopRecord** - stops the recording.
- **clearTranscription** - clears the transcription field.
- **translateText** - translates the transcription into the selected language.
- **sendEmail** - emails the selected language to the entered email address. In this function, 
replace "SENDER_EMAIL" with an email address you verified on Amazon SES in [Create the resources](#create-the-resources) 
on this page. 

It also contains several helper functions that are describe in the inline code comments.

**Important**: You must bundle all the JavaScript and Node.js code required for the app into a single
 file (**main.js**) to run the app. For instructions, see [Bundling the scripts](#bundling-the-scripts).

```javascript
import { transcribeClient } from "./libs/transcribeClient.js";
import { sesClient } from "./libs/sesClient.js";
import { translateClient } from "./libs/translateClient.js";
import { comprehendClient } from "./libs/comprehendClient.js";
import { DetectDominantLanguageCommand } from "@aws-sdk/client-comprehend";
import { StartStreamTranscriptionCommand } from "@aws-sdk/client-transcribe-streaming";
import { TranslateTextCommand } from "@aws-sdk/client-translate";
import { SendEmailCommand } from "@aws-sdk/client-ses";
import MicrophoneStream from "microphone-stream";
import getUserMedia from "get-user-media-promise";

// Helper function to encode PCM audio.
const pcmEncodeChunk = (chunk) => {
  const input = MicrophoneStream.toRaw(chunk);
  var offset = 0;
  var buffer = new ArrayBuffer(input.length * 2);
  var view = new DataView(buffer);
  for (var i = 0; i < input.length; i++, offset += 2) {
    var s = Math.max(-1, Math.min(1, input[i]));
    view.setInt16(offset, s < 0 ? s * 0x8000 : s * 0x7fff, true);
  }
  return Buffer.from(buffer);
};

window.startRecord = async () => {
  try {
    console.log("Recording started");
    var record = document.getElementById("record");
    var stop = document.getElementById("stopRecord");
    record.disabled = true;
    record.style.backgroundColor = "blue";
    stop.disabled = false;

    // Start the browser microphone.
    const micStream = new MicrophoneStream();
    micStream.setStream(
      await window.navigator.mediaDevices.getUserMedia({
        video: false,
        audio: true,
      })
    );

    // Acquire the microphone audio stream.
    const audioStream = async function* () {
      for await (const chunk of micStream) {
        yield {
          AudioEvent: {
            AudioChunk: pcmEncodeChunk(
              chunk
            ) /* pcm Encoding is optional depending on the source. */,
          },
        };
      }
    };

    const command = new StartStreamTranscriptionCommand({
      // The language code for the input audio. Valid values are en-GB, en-US, es-US, fr-CA, and fr-FR.
      LanguageCode: "en-US",
      // The encoding used for the input audio. The only valid value is pcm.
      MediaEncoding: "pcm",
      // The sample rate of the input audio in Hertz.
      MediaSampleRateHertz: 44100,
      AudioStream: audioStream(),
    });

    // Send the speech stream to Amazon Transcribe.
    const data = await transcribeClient.send(command);
    console.log("Success", data.TranscriptResultStream);
    for await (const event of data.TranscriptResultStream) {
      for (const result of event.TranscriptEvent.Transcript.Results || []) {
        if (result.IsPartial === false) {
          const noOfResults = result.Alternatives[0].Items.length;
          // Print results to browser window.
          for (let i = 0; i < noOfResults; i++) {
            console.log(result.Alternatives[0].Items[i].Content);
            const outPut = result.Alternatives[0].Items[i].Content + " ";
            const outputDiv = document.getElementById("output");
            outputDiv.insertAdjacentHTML("beforeend", outPut);
          }
        }
      }
    }
    console.log("Success. ", data);
    client.destroy();
  } catch (err) {
    console.log("Error. ", err);
  }
};

window.stopRecord = function () {
  window.location.reload();
};

window.translateText = async () => {
  try {
    const outPut = document.getElementById("output").innerHTML;
    const data = await comprehendClient.send(
      new DetectDominantLanguageCommand({ Text: outPut })
    );
    const langCode = data.Languages[0].LanguageCode;
    try {
      const selectedValue = document.getElementById("list").value;
      const translateParams = {
        Text: outPut,
        SourceLanguageCode: langCode /* required */,
        TargetLanguageCode: selectedValue /* required */,
      };
      const data = await translateClient.send(
        new TranslateTextCommand(translateParams)
      );
      document.getElementById("translated").innerHTML = data.TranslatedText;
    } catch (err) {
      console.log("Error translating language. ", err);
    }
  } catch (err) {
    console.log("Error detecting language of text. ", err);
  }
};

window.clearTranscription = async () => {
  document.getElementById("output").innerHTML = "";
};

// Helper function to send an email to user.
window.sendEmail = async () => {
  const toEmail = document.getElementById("email").value;
  const outputDiv = document.getElementById("output").innerHTML;
  const translatedDiv = document.getElementById("translated").innerHTML;
  const fromEmail = "SENDER_EMAIL";
  try {
    // Set the parameters
    const params = {
      Destination: {
        /* required */
        CcAddresses: [
          /* more items */
        ],
        ToAddresses: [
          toEmail, //RECEIVER_ADDRESS
          /* more To-email addresses */
        ],
      },
      Message: {
        /* required */
        Body: {
          /* required */
          Html: {
            Charset: "UTF-8",
            Data:
              "<h1>Hello!</h1><p>Here is your Amazon Transcribe recording:</p>" +
              "<h1>Original</h1>" +
              "<p>" +
              outputDiv +
              "</p>" +
              "<h1>Translation (if available)</h1>" +
              "<p>" +
              translatedDiv +
              "</p>",
          },
          Text: {
            Charset: "UTF-8",
            Data:
              "Hello,\\r\\n" +
              "Here is your Amazon Transcribe transcription:" +
              "\n" +
              outputDiv,
          },
        },
        Subject: {
          Charset: "UTF-8",
          Data: "Your Amazon Transcribe transcription.",
        },
      },
      Source: fromEmail, // SENDER_EMAIL
    };
    const data = await sesClient.send(new SendEmailCommand(params));
    alert("Success. Email sent.");
  } catch (err) {
    console.log("Error", err);
  }
};
```

### Bundling the scripts
This is a static site consisting only of HTML, CSS, and client-side JavaScript. 
However, a build step is required to enable the modules to work natively in the browser.

To bundle the JavaScript and Node.js for this example in a single file named main.js, 
enter the following commands in sequence in the AWS CLI command line:

```
cd javascriptv3/example_code/cross-services/transcribe-streaming-app/src
webpack index.js --mode development --target web --devtool false -o main.js
```
## Run the app
Open the index.html in your favorite browser, and follow the onscreen instructions.

## Destroying the resources
4. Open [AWS CloudFormation in the AWS Management Console](https://aws.amazon.com/cloudformation/), and open the *Stacks* page.

![ ](images/cloud_formation_stacks.png)

5. Select the stack you created in [Create the resources](#create-the-resources) on this page.

6. Choose **Delete**.
