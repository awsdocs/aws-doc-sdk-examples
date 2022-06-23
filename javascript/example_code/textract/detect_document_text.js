/*Example showing processing a document on local machine.*/ 

const aws = require('aws-sdk');
/*Initializing Textract from AWS SDK JS*/
const textract = new aws.Textract({ apiVersion: "2018-06-27" });
/*File system package for importing images and converting the image data to ByteArray */
const fs = require('fs');

exports.handler = async(event, context) => {
    let textractParams = {
        Document: {
            Bytes: fs.readFileSync("simple-document-image.jpg")
        },
    };
    try {
        let response = await textract.detectDocumentText(textractParams).promise();
        console.log(JSON.stringify(response),null,2)
    }catch(e){
        console.log("Error: ",e)
    }
} 