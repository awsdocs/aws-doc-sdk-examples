import logging
import sys
import time
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


class MaxRetriesExceededError(Exception):
    pass


def wait(seconds, tick=12):
    """
    Waits for a specified number of seconds, while also displaying an animated
    spinner.

    :param seconds: The number of seconds to wait.
    :param tick: The number of frames per second used to animate the spinner.
    """
    progress = '|/-\\'
    waited = 0
    while waited < seconds:
        for frame in range(tick):
            sys.stdout.write(f"\r{progress[frame % len(progress)]}")
            sys.stdout.flush()
            time.sleep(1/tick)
        waited += 1
    sys.stdout.write("\r")
    sys.stdout.flush()


class ExponentialRetry:
    def __init__(self, func, error_code, max_sleep=32):
        self.func = func
        self.error_code = error_code
        self.max_sleep = max_sleep

    def run(self, *func_args, **func_kwargs):
        """
        Retries the specified function with a simple exponential backoff algorithm.
        This is necessary when AWS is not yet ready to perform an action because all
        resources have not been fully deployed.

        :param func: The function to retry.
        :param error_code: The error code to retry. Other errors are raised again.
        :param func_args: The positional arguments to pass to the function.
        :param func_kwargs: The keyword arguments to pass to the function.
        :return: The return value of the retried function.
        """
        sleepy_time = 1
        func_return = None
        while sleepy_time <= self.max_sleep and func_return is None:
            try:
                func_return = self.func(*func_args, **func_kwargs)
                logger.info("Ran %s, got %s.", self.func.__name__, func_return)
            except ClientError as error:
                if error.response['Error']['Code'] == self.error_code:
                    print(f"Sleeping for {sleepy_time} to give AWS time to "
                          f"connect resources.")
                    time.sleep(sleepy_time)
                    sleepy_time = sleepy_time*2
                else:
                    logger.error(
                        "%s raised an error and cannot be retried.", self.func.__name__)
                    raise
        if sleepy_time > self.max_sleep:
            raise MaxRetriesExceededError(
                f"{self.func.__name__} exceeded the allowable number of retries.")
        return func_return
