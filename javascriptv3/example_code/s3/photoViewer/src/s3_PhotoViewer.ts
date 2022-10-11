/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
    SPDX-License-Identifier: Apache-2.0

    ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
    https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-photos-view.html

    Purpose:
    s3_PhotoViewer.js demonstrates how to allow viewing of photos in albums stored in an Amazon S3 bucket.

    Inputs (replace in code):
    - REGION
    - BUCKET_NAME

    Running the code:
    ts-node s3_PhotoViewer.js
    */
// snippet-start:[s3.JavaScript.s3_PhotoViewer.completeV3]
// snippet-start:[s3.JavaScript.s3_PhotoViewer.configV3]
// Load the required clients and packages
const { CognitoIdentityClient } = require("@aws-sdk/client-cognito-identity");
const {
  fromCognitoIdentityPool,
} = require("@aws-sdk/credential-provider-cognito-identity");
const { S3Client, ListObjectsCommand } = require("@aws-sdk/client-s3");

// Initialize the Amazon Cognito credentials provider
const REGION = "region"; //e.g., 'us-east-1'
const s3 = new S3Client({
  region: REGION,
  credentials: fromCognitoIdentityPool({
    client: new CognitoIdentityClient({ region: REGION }),
    identityPoolId: "IDENTITY_POOL_ID", // IDENTITY_POOL_ID e.g., eu-west-1:xxxxxx-xxxx-xxxx-xxxx-xxxxxxxxx
  }),
});

// snippet-end:[s3.JavaScript.s3_PhotoViewer.configV3]
// snippet-start:[s3.JavaScript.s3_PhotoViewer.listAlbumV3]
// A utility function to create HTML.
function getHtml(template) {
  return template.join("\n");
}
// Make the getHTML function available to the browser
window.getHTML = getHtml;

// List the photo albums that exist in the bucket
const albumBucketName = "BUCKET_NAME"; //BUCKET_NAME

const listAlbums = async () => {
  try {
    const data = await s3.send(
      new ListObjectsCommand({ Delimiter: "/", Bucket: albumBucketName })
    );
    const albums = data.CommonPrefixes.map(function (commonPrefix) {
      const prefix = commonPrefix.Prefix;
      const albumName = decodeURIComponent(prefix.replace("/", ""));
      return getHtml([
        "<li>",
        '<button style="margin:5px;" onclick="viewAlbum(\'' +
          albumName +
          "')\">",
        albumName,
        "</button>",
        "</li>",
      ]);
    });
    const message = albums.length
      ? getHtml(["<p>Click an album name to view it.</p>"])
      : "<p>You don't have any albums. You need to create an album.";
    const htmlTemplate = [
      "<h2>Albums</h2>",
      message,
      "<ul>",
      getHtml(albums),
      "</ul>",
    ];
    document.getElementById("viewer").innerHTML = getHtml(htmlTemplate);
  } catch (err) {
    return alert("There was an error listing your albums: " + err.message);
  }
};
// Make the viewAlbum function available to the browser
window.listAlbums = listAlbums;

// snippet-end:[s3.JavaScript.s3_PhotoViewer.listAlbumV3]
// snippet-start:[s3.JavaScript.s3_PhotoViewer.viewAlbumV3]

// Show the photos that exist in an album
const viewAlbum = async (albumName) => {
  try {
    const albumPhotosKey = encodeURIComponent(albumName) + "/";
    const data = await s3.send(
      new ListObjectsCommand({
        Prefix: albumPhotosKey,
        Bucket: albumBucketName,
      })
    );
    const href = "https://s3." + REGION + ".amazonaws.com/";
    const bucketUrl = href + albumBucketName + "/";
    const photos = data.Contents.map(function (photo) {
      const photoKey = photo.Key;
      const photoUrl = bucketUrl + encodeURIComponent(photoKey);
      return getHtml([
        "<span>",
        "<div>",
        "<br/>",
        '<img style="width:128px;height:128px;" src="' + photoUrl + '"/>',
        "</div>",
        "<div>",
        "<span>",
        photoKey.replace(albumPhotosKey, ""),
        "</span>",
        "</div>",
        "</span>",
      ]);
    });
    const message = photos.length
      ? "<p>The following photos are present.</p>"
      : "<p>There are no photos in this album.</p>";
    const htmlTemplate = [
      "<div>",
      '<button onclick="listAlbums()">',
      "Back To albums",
      "</button>",
      "</div>",
      "<h2>",
      "Album: " + albumName,
      "</h2>",
      message,
      "<div>",
      getHtml(photos),
      "</div>",
      "<h2>",
      "End of album: " + albumName,
      "</h2>",
      "<div>",
      '<button onclick="listAlbums()">',
      "Back To albums",
      "</button>",
      "</div>",
    ];
    document.getElementById("viewer").innerHTML = getHtml(htmlTemplate);
    document
      .getElementsByTagName("img")[0]
      .setAttribute("style", "display:none;");
  } catch (err) {
    return alert("There was an error viewing your album: " + err.message);
  }
};

// Make the viewAlbum function available to the browser
window.viewAlbum = viewAlbum;
// snippet-end:[s3.JavaScript.s3_PhotoViewer.viewAlbumV3]
// snippet-end:[s3.JavaScript.s3_PhotoViewer.completeV3]
//for unit tests only
export { listAlbums, viewAlbum };
