const { CognitoIdentityClient } = require("@aws-sdk/client-cognito-identity");
const { fromCognitoIdentityPool } = require("@aws-sdk/credential-provider-cognito-identity");
const { LambdaClient, InvokeCommand } = require("@aws-sdk/client-lambda");

// Configure AWS SDK for JavaScript & set region and credentials
// Initialize the Amazon Cognito credentials provider
const region = "us-west-2";
const lambda = new LambdaClient({
    region: region,
    credentials: fromCognitoIdentityPool({
        client: new CognitoIdentityClient({region}),
        identityPoolId: "IDENTITY_POOL_ID"
    })
});

/* CLIENT UI CODE */
// Application global variables
var isSpinning = false;
const pullHandle = () => {
    if (isSpinning == false) {
        // Show the handle pulled down
        slot_handle.src = "resources/lever-dn.png";
    }
}

const slot_L = document.querySelector("#slot_L");
const slot_M = document.querySelector("#slot_M");
const slot_R = document.querySelector("#slot_R");
const winner_light = document.querySelector("#winner_light");

const initiatePull = () => {
    // Show the handle flipping back up
    slot_handle.src = "resources/lever-up.png";
    // Set all three wheels "spinning"
    slot_L.src = "resources/slotpullanimation.gif";
    slot_M.src = "resources/slotpullanimation.gif";
    slot_R.src = "resources/slotpullanimation.gif";
    winner_light.style.visibility = "hidden";

    // Set app status to spinning
    isSpinning = true;
    // Call the Lambda function to collect the spin results
    lambda.send(new InvokeCommand({
        FunctionName : "slotpull",
        InvocationType : "RequestResponse",
        LogType : "None"
    }), function(err, data) {
        if (err) {
            prompt(err);
        } else {
            pullResults = JSON.parse(
                //parse Uint8Array payload to string
                new TextDecoder("utf-8").decode(data.Payload)
            );
            displayPull();
        }
    });
}

const displayPull = () => {
    isSpinning = false;
    if (pullResults.isWinner) {
        winner_light.style.visibility = "visible";
    }
    slot_L.src = `resources/${pullResults.leftWheelImage.file.S}`;
    slot_M.src = `resources/${pullResults.middleWheelImage.file.S}`;
    slot_R.src =  `resources/${pullResults.rightWheelImage.file.S}`;
};

const slotHandle = document.querySelector("#slot_handle");
slotHandle.onmousedown = pullHandle;
slotHandle.onmouseup = initiatePull;