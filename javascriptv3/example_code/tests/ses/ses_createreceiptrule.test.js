import {
  RULE_SET_NAME,
  S3_BUCKET_NAME,
  run,
} from "../../ses/src/ses_createreceiptrule";
import {
  createReceiptRuleSet,
  deleteReceiptRuleSet,
} from "../../ses/src/libs/sesUtils";
import {
  createBucket,
  deleteBucket,
  emptyBucket,
  putBucketPolicyAllowPuts,
} from "../../s3/src/libs/s3Utils";

describe("ses_createreceiptrule", () => {
  beforeAll(async () => {
    try {
      await createBucket(S3_BUCKET_NAME);
      await putBucketPolicyAllowPuts(S3_BUCKET_NAME, "AllowSESPuts");
      await createReceiptRuleSet(RULE_SET_NAME);
    } catch (e) {
      console.error(e);
    }
  });

  afterAll(async () => {
    try {
      await emptyBucket(S3_BUCKET_NAME);
      await deleteBucket(S3_BUCKET_NAME);
      await deleteReceiptRuleSet(RULE_SET_NAME);
    } catch (e) {
      console.error(e);
    }
  });

  it("should create a receipt rule", async () => {
    const result = await run();
    expect(result.$metadata.httpStatusCode).toBe(200);
  });
});
