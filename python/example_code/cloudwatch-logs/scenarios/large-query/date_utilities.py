# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
from datetime import datetime, timezone


class DateUtilities:
    """A class to help mutate dates in Python."""

    def __init__(self):
        """Initialize the DateUtilities class with default datetime format."""
        self.datetime_format = "%Y-%m-%d %H:%M:%S.%f"

    @staticmethod
    def is_datetime(date_string, format_string):
        """
        Checks if the given date string matches the specified format.

        :param date_string: The date string to be checked.
        :type date_string: str
        :param format_string: The format string to check against.
        :type format_string: str
        :return: True if the date_string matches the format_string, False otherwise.
        :rtype: bool
        """
        try:
            datetime.strptime(date_string, format_string)
            return True
        except ValueError:
            return False

    @staticmethod
    def divide_date_range(date_range):
        """
        Splits a date range into two equal halves.

        :param date_range: Start and end datetime in a tuple.
        :type date_range: tuple
        :return: List of tuples with two split date ranges.
        :rtype: list of tuples
        """
        midpoint = (date_range[0] + date_range[1]) / 2
        return [(date_range[0], round(midpoint)), (round(midpoint), date_range[1])]

    @staticmethod
    def convert_unix_timestamp_to_iso8601(
        unix_timestamp, iso8601_format="%Y-%m-%d %H:%M:%S.%f"
    ):
        """
        Converts a UNIX timestamp in milliseconds to a date string in the specified format.

        :param unix_timestamp: UNIX timestamp in milliseconds.
        :type unix_timestamp: int
        :param iso8601_format: The format string for the output date string, defaults to "%Y-%m-%d %H:%M:%S.%f".
        :type iso8601_format: str
        :return: The formatted date string.
        :rtype: str
        """
        in_seconds = unix_timestamp / 1000.0
        date_time = datetime.utcfromtimestamp(in_seconds)
        iso8601 = date_time.strftime(iso8601_format)
        return iso8601

    @staticmethod
    def convert_iso8601_to_datetime(iso8601, iso8601_format="%Y-%m-%d %H:%M:%S.%f"):
        """
        Converts a date string in ISO 8601 format to a Python datetime object.

        :param iso8601: The ISO 8601 formatted date string.
        :type iso8601: str
        :param iso8601_format: The format string of the input date, defaults to ISO 8601 format.
        :type iso8601_format: str
        :return: The corresponding Python datetime object.
        :rtype: datetime
        """
        date = datetime.strptime(iso8601, iso8601_format)
        return date

    @staticmethod
    def convert_datetime_to_unix_timestamp(dt):
        """
        Converts a Python datetime object to a UNIX timestamp in milliseconds.

        :param dt: The datetime object to be converted.
        :type dt: datetime
        :return: UNIX timestamp in milliseconds.
        :rtype: int
        """
        unix_timestamp = dt.replace(tzinfo=timezone.utc).timestamp()
        return unix_timestamp * 1000

    def convert_unix_timestamp_to_datetime(self, unix_timestamp):
        """
        Converts a UNIX timestamp in milliseconds to a Python datetime object.

        :param unix_timestamp: UNIX timestamp in milliseconds.
        :type unix_timestamp: int
        :return: The corresponding Python datetime object.
        :rtype: datetime
        """
        ts = self.convert_unix_timestamp_to_iso8601(unix_timestamp)
        dt = self.convert_iso8601_to_datetime(ts)
        return dt

    def convert_iso8601_to_unix_timestamp(self, iso8601):
        """
        Converts a date string in ISO 8601 format to a UNIX timestamp in milliseconds.

        :param iso8601: The ISO 8601 formatted date string.
        :type iso8601: str
        :return: UNIX timestamp in milliseconds.
        :rtype: int
        """
        dt = self.convert_iso8601_to_datetime(iso8601)
        unix_timestamp = dt.replace(tzinfo=timezone.utc).timestamp()
        return unix_timestamp * 1000

    def convert_datetime_to_iso8601(self, datetime_obj):
        """
        Converts a Python datetime object to ISO 1806 format.

        :param dt: The datetime object to be converted.
        :type dt: datetime
        :return: ISO 1806.
        :rtype: str
        """
        unix_timestamp = datetime_obj.replace(tzinfo=timezone.utc).timestamp()
        iso8601 = self.convert_unix_timestamp_to_iso8601(round(unix_timestamp * 1000))
        return iso8601

    def compare_dates(self, date_str1, date_str2):
        """
        Compares two dates in ISO 8601 format and returns the later one.

        :param date_str1: The first date string in ISO 8601 format.
        :type date_str1: str
        :param date_str2: The second date string in ISO 8601 format.
        :type date_str2: str
        :return: The later of the two dates.
        :rtype: str
        """
        date1 = self.convert_iso8601_to_datetime(date_str1)
        date2 = self.convert_iso8601_to_datetime(date_str2)

        if date1 > date2:
            return date_str1
        else:
            return date_str2

    def normalize_date_range_format(self, date_range, from_format=None, to_format=None):
        """
        Normalizes date ranges received in variable formats to a specified format.

        :param date_range: The date range to be normalized.
        :type date_range: tuple
        :param from_format: The current format of the date range.
        :type from_format: str, optional
        :param to_format: The target format for the date range.
        :type to_format: str, optional
        :return: The normalized date range.
        :rtype: tuple
        :raises Exception: If required parameters are missing.
        """
        if not (to_format, from_format):
            raise Exception(
                "This function requires a date range, a starting format, and a target format"
            )
        if "unix_timestamp" in to_format and "datetime" in from_format:
            if not self.is_datetime(date_range[0], self.datetime_format):
                start_date = self.convert_unix_timestamp_to_datetime(date_range[0])
            else:
                start_date = date_range[0]

            if not self.is_datetime(date_range[1], self.datetime_format):
                end_date = self.convert_unix_timestamp_to_datetime(date_range[1])
            else:
                end_date = date_range[1]
        else:
            return date_range
        return start_date, end_date
