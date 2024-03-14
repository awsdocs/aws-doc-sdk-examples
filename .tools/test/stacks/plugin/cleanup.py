import boto3


def terminate_runnable_jobs_in_queue(job_queue):
    """
    Deletes all jobs that are stuck in RUNNABLE
    :param job_queue:
    :return:
    """
    # Create a Boto3 client for AWS Batch
    batch_client = boto3.client('batch')

    # List all RUNNABLE jobs in the specified job queue
    runnable_jobs = batch_client.list_jobs(
        jobQueue=job_queue,
        jobStatus='RUNNABLE'
    )

    # Loop through the RUNNABLE jobs and terminate them
    for job in runnable_jobs['jobSummaryList']:
        job_id = job['jobId']
        try:
            response = batch_client.terminate_job(
                jobId=job_id,
                reason='Terminating all RUNNABLE jobs'
            )
            print(f"Terminated job ID: {job_id} in queue {job_queue}")
        except Exception as e:
            print(f"Failed to terminate job ID: {job_id} in queue {job_queue}. Error: {e}")


def discover_and_delete_runnable_jobs():
    batch_client = boto3.client('batch')

    # Discover existing job queues
    response = batch_client.describe_job_queues()
    job_queues = response['jobQueues']

    # Iterate through each queue and delete all RUNNABLE jobs
    for queue in job_queues:
        if queue['state'] == 'ENABLED':  # Filter for enabled queues if necessary
            job_queue_name = queue['jobQueueName']
            print(f"Processing queue: {job_queue_name}")
            terminate_runnable_jobs_in_queue(job_queue_name)
        else:
            print(f"Skipping disabled queue: {queue['jobQueueName']}")


if __name__ == "__main__":
    discover_and_delete_runnable_jobs()
