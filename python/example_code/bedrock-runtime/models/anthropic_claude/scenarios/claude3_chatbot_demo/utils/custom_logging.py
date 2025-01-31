# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import logging

# ANSI color codes
COLOR_CODES = {
    "DEBUG": "\033[94m",  # Blue
    "INFO": "\033[97m",  # White
    "WARNING": "\033[93m",  # Yellow
    "ERROR": "\033[91m",  # Red
    "CRITICAL": "\033[95m",  # Purple
    "RESET": "\033[0m",  # Reset to default
}


# Custom Formatter
class ColoredFormatter(logging.Formatter):
    def format(self, record):
        level_name = record.levelname
        message = logging.Formatter.format(self, record)
        return COLOR_CODES.get(level_name, "") + message + COLOR_CODES["RESET"]


# Customizing logging handler to use the colored formatter
def setup_custom_logger(name):
    # Create a logger
    logger = logging.getLogger(name)
    logger.setLevel(logging.DEBUG)  # Setting to debug to catch all logs

    # Creating and setting the custom formatter
    formatter = ColoredFormatter("%(asctime)s - %(name)s - %(levelname)s - %(message)s")

    # Creating a stream handler (console output) and setting the custom formatter
    console_handler = logging.StreamHandler()
    console_handler.setFormatter(formatter)

    # Adding the handler to the logger
    logger.addHandler(console_handler)

    return logger
