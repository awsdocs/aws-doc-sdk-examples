## Spec for Using Kinesis Firehose
Create a robust, production-grade scrip demonstrating how to use Amazon Kinesis Data Firehose to put individual records (`PutRecord`) and batches of records (`PutRecordBatch`) to a delivery stream.

### Pre-requisites
1. **AWS Account**: Ensure you have an active AWS account.
2. **AWS CLI Configured**: Configure AWS CLI with appropriate permissions to create resources with the CDK.
3. **AWS CDK configured**: Configure the AWS Cloud Development Kit (CDK) to deploy required resources

### Infrastructure Setup
1. **Create Kinesis Firehose Delivery Stream**:
   - Go to the AWS Management Console.
   - Navigate to Kinesis -> Data Firehose.
   - Create a new delivery stream and configure the destination (e.g., S3, Redshift, Elasticsearch Service, or HTTP Endpoint).
   - Note down the delivery stream name.

### Script Specification

#### 1. Setup AWS SDK
- Initialize the AWS SDK in your chosen language.
- Ensure the necessary permissions are set up (IAM roles/policies) to allow interaction with the Firehose service.

#### 2. Define Configuration Parameters
- Define configuration parameters such as `delivery_stream_name`, region, batch size, and logging configurations.

#### 3. PutRecord API
- Function to put a single record into the Firehose stream.
- **Input**: Data record (e.g., a JSON object or plain text).
- **Output**: Response from Firehose service indicating success or failure.
- **Error Handling**: Implement retries with exponential back-off and jitter.

#### 4. PutRecordBatch API
- Function to put a batch of records into the Firehose stream.
- **Input**: List of data records.
- **Output**: Response from Firehose service indicating success or failure for each record.
- **Error Handling**: Implement retries with exponential back-off and jitter.
- **Pagination**: Handle large batches by splitting them into smaller chunks.
- **Batch Sizing**: Optimize batch size to balance throughput and cost, considering the hard limits (500 records or 4MB per request).

#### 5. Logging
- Implement structured logging to capture the success and failure of API calls.
- Log retries, exceptions, batch operations, and any critical information for debugging and monitoring.
- Provide real-time updates on the script's activity.

#### 6. Monitoring Metrics
- Check `IncomingBytes` and `IncomingRecords` metrics to ensure there is incoming traffic.
- Monitor `FailedPutCount` for batch operations.

### Example Flow
1. **Initialize SDK and Set Configuration Parameters**:
   - Configure the AWS SDK with access keys and region.
   - Define the delivery stream name.
   - Set up logging configurations.

2. **PutRecord Function**:
   - Prepare a data record.
   - Use `PutRecord` API to send the data to the delivery stream.
   - Implement retries with exponential back-off and jitter for transient errors.
   - Log the result of the operation.
   - Handle and log exceptions.

3. **PutRecordBatch Function**:
   - Prepare a batch of data records.
   - Use `PutRecordBatch` API to send the data batch to the delivery stream.
   - Implement retries with exponential back-off and jitter for transient errors.
   - Handle large batches by splitting them into smaller chunks.
   - Optimize batch size to balance throughput and cost, considering the hard limits (500 records or 4MB per request).
   - Log the result of each operation and check `FailedPutCount` in the response.
   - Handle and log exceptions.

4. **Monitor Metrics**:
   - Check `IncomingBytes` and `IncomingRecords` to ensure incoming traffic.
   - Continuously monitor `FailedPutCount` for batch operations.

### Example Pseudocode

```python
import boto3
import logging
import time
import random

# Initialize logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Initialize the Kinesis Firehose client
firehose_client = boto3.client('firehose', region_name='us-east-1')

# Define the delivery stream name
delivery_stream_name = 'your-delivery-stream-name'

# Batch size configuration
max_batch_size_records = 500
max_batch_size_bytes = 4 * 1024 * 1024  # 4MB

def exponential_backoff_with_jitter(retry_count):
    base_delay = 0.1  # Base delay in seconds
    max_delay = 2.0   # Maximum delay in seconds
    delay = min(max_delay, base_delay * (2 ** retry_count))
    jitter = random.uniform(0, delay)
    return delay + jitter

def put_record(data):
    data_bytes = data.encode('utf-8')
    max_retries = 5
    for retry in range(max_retries):
        try:
            response = firehose_client.put_record(
                DeliveryStreamName=delivery_stream_name,
                Record={'Data': data_bytes}
            )
            if response['ResponseMetadata']['HTTPStatusCode'] == 200:
                logger.info("Record successfully sent")
                return
        except Exception as e:
            logger.error(f"Failed to send record: {e}")
        time.sleep(exponential_backoff_with_jitter(retry))
    logger.error("Max retries reached. Failed to send record")

def put_record_batch(data_batch):
    records = [{'Data': data.encode('utf-8')} for data in data_batch]
    max_retries = 5
    for retry in range(max_retries):
        try:
            response = firehose_client.put_record_batch(
                DeliveryStreamName=delivery_stream_name,
                Records=records
            )
            failed_count = response['FailedPutCount']
            if failed_count > 0:
                logger.warning(f"Failed to send {failed_count} records")
                for idx, record_response in enumerate(response['RequestResponses']):
                    if 'ErrorCode' in record_response:
                        logger.error(f"Record {idx} failed: {record_response['ErrorMessage']}")
            else:
                logger.info("All records successfully sent")
                return
        except Exception as e:
            logger.error(f"Failed to send record batch: {e}")
        time.sleep(exponential_backoff_with_jitter(retry))
    logger.error("Max retries reached. Failed to send record batch")

# Example usage
put_record("Single record data")

def put_records_in_batches(data_records):
    total_records = len(data_records)
    total_bytes = sum(len(data.encode('utf-8')) for data in data_records)
    if total_records > max_batch_size_records or total_bytes > max_batch_size_bytes:
        logger.info("Splitting data into smaller batches")
        for i in range(0, total_records, max_batch_size_records):
            put_record_batch(data_records[i:i + max_batch_size_records])
    else:
        put_record_batch(data_records)

# Example batch usage
data_batch = ["Batch record data 1", "Batch record data 2", ...]
put_records_in_batches(data_batch)

def monitor_metrics():
    cloudwatch = boto3.client('cloudwatch', region_name='us-east-1')
    metrics = ['IncomingBytes', 'IncomingRecords']
    for metric in metrics:
        response = cloudwatch.get_metric_statistics(
            Namespace='AWS/Firehose',
            MetricName=metric,
            Dimensions=[{'Name': 'DeliveryStreamName', 'Value': delivery_stream_name}],
            StartTime=time.time() - 600,  # last 10 minutes
            EndTime=time.time(),
            Period=60,
            Statistics=['Sum']
        )
        for point in response['Datapoints']:
            logger.info(f"{metric} - {point['Timestamp']}: {point['Sum']}")

# Monitor metrics
monitor_metrics()
```

### Batch Sizing Considerations
- **Hard Limits**: The `PutRecordBatch` API has a hard limit of 500 records or 4MB per request.
- **Throughput**: Larger batches improve throughput but may increase latency.
- **Cost**: Smaller batches may lead to higher costs due to more frequent API calls.
- **Optimization**: Adjust the batch size to find the best balance between throughput, latency, and cost. Monitor and adjust based on the specific workload and requirements.

### Notes
- **Error Handling**: Comprehensive error handling with retries and exponential back-off with jitter.
- **Logging**: Structured and informative logging for real-time monitoring and debugging.
- **Pagination**: Handle large data batches by splitting them into smaller chunks.
- **Monitoring**: Regularly check and log metrics to ensure the system is functioning correctly.