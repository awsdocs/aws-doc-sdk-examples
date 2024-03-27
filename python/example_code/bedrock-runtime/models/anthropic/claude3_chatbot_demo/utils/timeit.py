import os
import time

from ..decorators.custom_logging import setup_custom_logger

logger = setup_custom_logger(os.path.basename(__file__))


def timeit(f):
    def wrapper(*args, **kwargs):
        start_time = time.time()
        result = f(*args, **kwargs)
        end_time = time.time()
        elapsed_time = end_time - start_time
        logger.error(f"Function {f.__name__} executed in {elapsed_time:.5f} seconds.")
        return result

    return wrapper
