import os
import sys
import boto3
from botocore.config import Config
import logging
from cloudwatch_query import CloudWatchQuery
from date_utilities import DateUtilities

# Configure logging at the module level.
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(levelname)s - %(filename)s:%(lineno)d - %(message)s",
)


class CloudWatchLogsQueryRunner:
    def __init__(self):
        self.date_utilities = DateUtilities()
        self.cloudwatch_logs_client = self.create_cloudwatch_logs_client()

    def create_cloudwatch_logs_client(self):
        """Create and return a CloudWatch Logs client with retry configuration."""
        try:
            return boto3.client("logs", config=Config(retries={"max_attempts": 10}))
        except Exception as e:
            logging.error(f"Failed to create CloudWatch Logs client: {e}")
            sys.exit(1)

    def fetch_environment_variables(self):
        """Fetch and validate required environment variables."""
        try:
            query_start_date = int(os.environ["QUERY_START_DATE"])
            query_end_date = int(os.environ["QUERY_END_DATE"])
        except KeyError:
            logging.error(
                "Both QUERY_START_DATE and QUERY_END_DATE environment variables are required."
            )
            sys.exit(1)
        except ValueError as e:
            logging.error(f"Error parsing date environment variables: {e}")
            sys.exit(1)

        return query_start_date, query_end_date

    def convert_dates_to_iso1806(self, start_date, end_date):
        """Convert UNIX timestamp dates to ISO 8601 format."""
        start_date_iso1806 = self.date_utilities.convert_unix_timestamp_to_iso1806(
            start_date
        )
        end_date_iso1806 = self.date_utilities.convert_unix_timestamp_to_iso1806(
            end_date
        )
        return start_date_iso1806, end_date_iso1806

    def execute_query(self, start_date_iso1806, end_date_iso1806):
        """Create CloudWatchQuery instance and execute the query."""
        cloudwatch_query = CloudWatchQuery(
            self.cloudwatch_logs_client,
            "/workflows/cloudwatch-logs/large-query",
            [start_date_iso1806, end_date_iso1806],
        )
        cloudwatch_query.query_logs()
        logging.info("Query executed successfully.")
        logging.info(
            f"Queries completed in {cloudwatch_query.query_duration} seconds. Total logs found: {len(cloudwatch_query.query_results)}"
        )


def main():
    logging.info("Starting a recursive CloudWatch logs query...")

    runner = CloudWatchLogsQueryRunner()
    query_start_date, query_end_date = runner.fetch_environment_variables()
    start_date_iso1806, end_date_iso1806 = runner.convert_dates_to_iso1806(
        query_start_date, query_end_date
    )
    runner.execute_query(start_date_iso1806, end_date_iso1806)


if __name__ == "__main__":
    main()
