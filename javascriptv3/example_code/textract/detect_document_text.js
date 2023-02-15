/*Example showing processing a document on local machine.*/

/*TextractClient, DetectDocumentTextCommand modules from AWS SDK JS v3 */
const { TextractClient, DetectDocumentTextCommand } = require("@aws-sdk/client-textract");

/*Initializing TextractClient from AWS SDK JS v3*/
const client = new TextractClient({ apiVersion: "2018-06-27" });

/*File system package for importing images and converting the image data to ByteArray */
const fs = require('fs');

exports.handler = async (event, context) => {
    const textractParams = {
        Document: {
            Bytes: fs.readFileSync("simple-document-image.jpg")
        }
    };
    try {
        const response = await client.send(new DetectDocumentTextCommand(textractParams));
        console.log(JSON.stringify(response, null, 2));
    } catch (e) {
        console.log("Error: ", e);
    }
} 