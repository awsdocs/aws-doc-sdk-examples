# Kinesis Firehose Example

This is an example application that demonstrates how to use the AWS Kinesis Firehose service for PutRecord and PutRecordBatch operations, focusing on production-grade reliability and performance.

## Prerequisites

- Python 3.7 or later
- AWS account with appropriate permissions
- AWS credentials (access key and secret key)

## Setup

1. Clone the repository:

```
git clone https://github.com/your-repo/kinesis-firehose-example.git
cd kinesis-firehose-example
```

2. Install the required Python dependencies:

```
pip install -r requirements.txt
```

3. Set up the AWS credentials as environment variables:

```
export AWS_ACCESS_KEY_ID=your_access_key_id
export AWS_SECRET_ACCESS_KEY=your_secret_access_key
```

4. Deploy the AWS resources using the provided CDK script:

```
npx aws-cdk deploy
```

## Usage

Run the application with the required arguments:

```
python main.py --stream-name your_stream_name --bucket-name your_bucket_name --records "record1" "record2" "record3"
```

Replace `your_stream_name` and `your_bucket_name` with the names of the Kinesis Firehose Delivery Stream and S3 bucket created by the CDK script, respectively. The `--records` argument accepts a list of records to be put into the Delivery Stream.

## Testing

Run the unit tests:

```
python -m unittest discover -s tests -p 'test_*.py'
```

## Cleanup

To delete the AWS resources created by the CDK script, run:

```
npx aws-cdk destroy
```

## License

This project is licensed under the [MIT License](LICENSE).