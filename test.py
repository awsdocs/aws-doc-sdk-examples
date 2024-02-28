import boto3

# Initialize a boto3 client for AWS Batch
batch_client = boto3.client('batch')

def cancel_all_batch_jobs():
    # List all job queues
    job_queues_response = batch_client.describe_job_queues()
    job_queues = job_queues_response['jobQueues']

    for queue in job_queues:
        job_queue_arn = queue['jobQueueArn']
        # List all jobs for each state that can be canceled
        for state in ['SUBMITTED', 'PENDING', 'RUNNABLE', 'STARTING', 'RUNNING']:
            job_list_response = batch_client.list_jobs(jobQueue=job_queue_arn, jobStatus=state)
            jobs = job_list_response['jobSummaryList']
            # Cancel each job
            for job in jobs:
                job_id = job['jobId']
                try:
                    batch_client.terminate_job(jobId=job_id, reason='Canceling job')
                    print(f"Job {job_id} canceled successfully.")
                except Exception as e:
                    print(f"Failed to cancel job {job_id}: {e}")

# Uncomment the line below to execute the function
cancel_all_batch_jobs()

