const { S3 } = require("@aws-sdk/client-s3");
const client = new S3({ region: "us-west-2" });
const bucketParams = {
  Bucket: <replaceable>BUCKET_NAME</replaceable>,
};
function run() {
  client.createBucket(bucketParams, function (err, data) {
    if (err) {
      console.log("Error", err);
    } else {
      console.log("Success", data.Location);
    }
  });
}
run();
