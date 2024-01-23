import boto3
import csv
import io

def update_csv_in_s3(bucket_name, object_key, tool_name_to_update, new_status):
    # Create a Boto3 client for S3
    s3_client = boto3.client('s3')

    # Retrieve the object
    response = s3_client.get_object(Bucket=bucket_name, Key=object_key)
    data = response['Body'].read().decode('utf-8')

    # Read the CSV data into Python object
    lines = csv.reader(io.StringIO(data))
    headers = next(lines)  # Skip the header row
    updated_data = [headers]

    # Update the specific record
    for row in lines:
        if row[0] == tool_name_to_update:
            row[1] = new_status
        updated_data.append(row)

    # Convert the updated data back to CSV format
    output_buffer = io.StringIO()
    writer = csv.writer(output_buffer)
    writer.writerows(updated_data)
    updated_csv_data = output_buffer.getvalue()

    # Upload the updated CSV to S3, overwriting the original
    s3_client.put_object(Bucket=bucket_name, Key=object_key, Body=updated_csv_data)

# Usage
bucket_name = 'test-wt-ex'
object_key = 'consolidated-results.csv'
tool_name_to_update = 'so'
new_status = 'SUCCEEDED'

update_csv_in_s3(bucket_name, object_key, tool_name_to_update, new_status)

