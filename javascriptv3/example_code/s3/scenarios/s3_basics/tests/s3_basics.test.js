jest.mock("../libs/s3Client");
jest.mock("@aws-sdk/client-s3");

// Get service clients module and commands.
import "regenerator-runtime/runtime";
import { createBucket, bucket_name, object_key, object_content } from "../src/s3_basics.js";
import { uploadObject } from "../src/helpers/uploadObject.js";
import { copyObject } from "../src/helpers/copyObject.js";
import {REGION, s3Client} from "../libs/s3Client.js";

const create_bucket_params = {
  Bucket: bucket_name,
  CreateBucketConfiguration: {
    LocationConstraint: REGION,
  },
};

describe("@aws-sdk/client-s3 mock", () => {
  it("should successfully mock S3 client", async () => {
    s3Client.send.mockResolvedValue({ isMock: true });
    const response = await createBucket(create_bucket_params);
    expect(response.isMock).toEqual(true);
  });
});

const upload_object_params = {
  Bucket: bucket_name,
  CopySource: "/" + bucket_name + "/" + object_key,
  Key: "copy-destination/" + object_key,
};

describe("@aws-sdk/client-s3 mock", () => {
  it("should successfully mock S3 client", async () => {
    s3Client.send.mockResolvedValue({ isMock: true });
    const response = await uploadObject(upload_object_params);
    expect(response.isMock).toEqual(true);
  });
});

const copy_object_params = {
  Bucket: bucket_name,
  CopySource: "/" + bucket_name + "/" + object_key,
  Key: "copy-destination/" + object_key,
};

describe("@aws-sdk/client-s3 mock", () => {
  it("should successfully mock S3 client", async () => {
    s3Client.send.mockResolvedValue({ isMock: true });
    const response = await copyObject(copy_object_params);
    expect(response.isMock).toEqual(true);
  });
});

