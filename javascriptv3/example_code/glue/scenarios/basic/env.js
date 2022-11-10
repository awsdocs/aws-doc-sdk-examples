const BUCKET_NAME = "";
const ROLE_NAME = "";
const PYTHON_SCRIPT_KEY = "flight_etl_job_script.py";
const S3_TARGET_PATH = "s3://crawler-public-us-east-1/flight/2016/csv";
const DATABASE_NAME = "doc-example-database";
const TABLE_PREFIX = "doc-example-";
const TABLE_NAME = "doc-example-csv";
const CRAWLER_NAME = "s3-flight-data-crawler";
const JOB_NAME = "flight_etl_job";

export {
  BUCKET_NAME,
  ROLE_NAME,
  PYTHON_SCRIPT_KEY,
  S3_TARGET_PATH,
  DATABASE_NAME,
  TABLE_PREFIX,
  TABLE_NAME,
  CRAWLER_NAME,
  JOB_NAME,
};
