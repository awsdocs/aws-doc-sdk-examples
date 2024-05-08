import boto3

def delete_all_jobs_in_queues():
    # Initialize Boto3 AWS Batch client
    batch_client = boto3.client('batch')

    # List all job queues
    response = batch_client.describe_job_queues()
    job_queues = response['jobQueues']

    # Iterate over each job queue
    for job_queue in job_queues:
        queue_name = job_queue['jobQueueName']
        print(f"Deleting jobs in queue: {queue_name}")

        # List all jobs in the current queue
        jobs_response = batch_client.list_jobs(jobQueue=queue_name)
        jobs = jobs_response.get('jobSummaryList', [])

        # Delete each job in the queue
        for job in jobs:
            job_id = job['jobId']
            batch_client.cancel_job(jobId=job_id, reason='Deleting all jobs in queue')

        print(f"Deleted {len(jobs)} jobs in queue: {queue_name}")

if __name__ == "__main__":
    delete_all_jobs_in_queues()
