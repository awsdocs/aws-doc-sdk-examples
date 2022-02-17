import logging
import time
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


class MaxRetriesExceededError(Exception):
    pass


def exponential_retry(error_code, error_message=None):
    """
    Retries the specified function with a simple exponential backoff algorithm.
    This is necessary when AWS is not yet ready to perform an action because all
    resources have not been fully deployed.

    :param func: The function to retry.
    :param error_code: The error code to retry. Other errors are raised again.
    :return: The return value of the retried function.
    """
    def decorator_retry(func):
        def wrapper_retry(*args, **kwargs):
            sleepy_time = 1
            max_sleep = 32
            func_return = None
            while sleepy_time <= max_sleep and func_return is None:
                try:
                    func_return = func(*args, **kwargs)
                except ClientError as error:
                    if error.response['Error']['Code'] == error_code and \
                            (error_message is None or
                             error_message in error.response['Error']['Message']):
                        logger.info(
                            "Got %s. Sleeping for %s and retrying.", error_code,
                            sleepy_time)
                        time.sleep(sleepy_time)
                        sleepy_time = sleepy_time*2
                    else:
                        logger.exception(
                            "%s raised an error and cannot be retried.", func.__name__)
                        raise
            if sleepy_time > max_sleep:
                raise MaxRetriesExceededError(
                    f"{func.__name__} exceeded the allowable number of retries.")
            return func_return
        return wrapper_retry
    return decorator_retry
