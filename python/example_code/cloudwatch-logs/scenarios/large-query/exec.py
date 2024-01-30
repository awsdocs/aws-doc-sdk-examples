import os
import sys
import boto3
from botocore.config import Config
import logging
from cloudwatch_query import CloudWatchQuery  # Assuming cloud_watch_query.py contains the CloudWatchQuery class

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(filename)s:%(lineno)d - %(message)s')


def main():
    # Start of the script logging
    logging.info("Starting a recursive CloudWatch logs query...")

    # Environment variables validation
    if "QUERY_START_DATE" not in os.environ or "QUERY_END_DATE" not in os.environ:
        logging.error("Both QUERY_START_DATE and QUERY_END_DATE environment variables are required.")
        sys.exit(1)

    # Parsing environment variables for start and end dates
    try:
        query_start_date = int(os.environ["QUERY_START_DATE"])
        query_start_date_iso1806 = CloudWatchQuery.convert_unix_timestamp_to_iso1806(query_start_date)
        query_end_date = int(os.environ["QUERY_END_DATE"])
        query_end_date_iso1806 = CloudWatchQuery.convert_unix_timestamp_to_iso1806(query_end_date)
    except ValueError as e:
        logging.error("Error parsing date environment variables: %s", e)
        sys.exit(1)

    # Creating CloudWatch Logs client
    try:
        cloudwatch_logs_client = boto3.client(
            "logs", config=Config(retries={"max_attempts": 10})
        )
    except Exception as e:
        logging.error("Failed to create CloudWatch Logs client: %s", e)
        sys.exit(1)

    # Creating an instance of CloudWatchQuery
    cloudwatch_query = CloudWatchQuery(
        cloudwatch_logs_client,
        "/workflows/cloudwatch-logs/large-query",
        [query_start_date_iso1806, query_end_date_iso1806]
    )

    # Executing the CloudWatch logs query
    results = cloudwatch_query.execute_query()
    logging.info("Query executed successfully.")
    # Logging the results of the query
    logging.info(
        "Queries completed in %s seconds. Total logs found: %s",
        cloudwatch_query.query_duration,
        len(results)
    )

if __name__ == "__main__":
    main()
