import {TextractClient,DetectDocumentTextCommand}from'@aws-sdk/client-textract';/**
 * Fetch the S3 object from the event and analyze it using Textract.
 *
 * @param {import("@types/aws-lambda").EventBridgeEvent<"Object Created">} eventBridgeS3Event
 */
const handler = async (eventBridgeS3Event) => {
  const textractClient = new TextractClient();

  const detectDocumentTextCommand = new DetectDocumentTextCommand({
    Document: {
      S3Object: {
        Bucket: eventBridgeS3Event.bucket,
        Name: eventBridgeS3Event.object,
      },
    },
  });

  const {Blocks} = await textractClient.send(detectDocumentTextCommand);

  const extractedWords = Blocks.filter((b) => b.BlockType === "WORD").map(
    (b) => b.Text
  );

  return extractedWords.join(" ");
};export{handler};