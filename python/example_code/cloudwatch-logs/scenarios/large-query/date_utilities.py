from datetime import datetime, timezone


class DateUtilities:
    """A class to help mutate dates in Python."""

    def __init__(self):
        self.datetime_format = "%Y-%m-%d %H:%M:%S.%f"

    @staticmethod
    def is_datetime(date_string, format_string):
        """

        :param date_string:
        :param format_string:
        :return:
        """
        try:
            datetime.strptime(date_string, format_string)
            return True
        except ValueError:
            return False

    @staticmethod
    def divide_date_range(date_range):
        """Splits a date range into two equal halves.

        Args:
            date_range (tuple): Start and end datetime.

        Returns:
            list of tuples: Two split date ranges.
        """
        midpoint = (date_range[0] + date_range[1]) / 2
        return [(date_range[0], round(midpoint)), (round(midpoint), date_range[1])]

    @staticmethod
    def convert_unix_timestamp_to_iso1806(
        unix_timestamp, iso1806_format="%Y-%m-%d %H:%M:%S.%f"
    ):
        """
        Converts a UNIX timetamp in milliseconds
        :param unix_timestamp:
        :param iso1806_format:
        :return:
        """
        in_seconds = unix_timestamp / 1000.0
        date_time = datetime.utcfromtimestamp(in_seconds)
        iso1806 = date_time.strftime(iso1806_format)
        return iso1806

    @staticmethod
    def convert_iso1806_to_datetime(iso1806, iso1806_format="%Y-%m-%d %H:%M:%S.%f"):
        """
        Converts a ISO1806 date to Python datetime object.
        :param iso1806:
        :param iso1806_format:
        :return: datetime
        """
        date = datetime.strptime(iso1806, iso1806_format)
        return date

    @staticmethod
    def convert_datetime_to_unix_timestamp(dt):
        """

        :param dt:
        :return:
        """
        unix_timestamp = dt.replace(tzinfo=timezone.utc).timestamp()
        return unix_timestamp * 1000

    def convert_unix_timestamp_to_datetime(self, unix_timestamp):
        """

        :param unix_timestamp:
        :return:
        """
        ts = self.convert_unix_timestamp_to_iso1806(unix_timestamp)
        dt = self.convert_iso1806_to_datetime(ts)
        return dt

    def convert_iso1806_to_unix_timestamp(self, iso1806):
        """

        :param iso1806:
        :return:
        """
        dt = self.convert_iso1806_to_datetime(iso1806)
        unix_timestamp = dt.replace(tzinfo=timezone.utc).timestamp()
        return unix_timestamp * 1000

    def compare_dates(self, date_str1, date_str2):
        """
        Compare two dates in ISO 8601 format.
        :param date_str1:
        :param date_str2:
        :return:
        """
        date1 = self.convert_iso1806_to_datetime(date_str1)
        date2 = self.convert_iso1806_to_datetime(date_str2)

        if date1 > date2:
            return date_str1
        else:
            return date_str2

    def normalize_date_range_format(self, date_range, from_format=None, to_format=None):
        """
        Normalizes date ranges received in variable format from recursive input.
        :param date_range:
        :param from_format:
        :param to_format:
        :return:
        """
        if not (to_format, from_format):
            raise "This function requires a date range, a starting format, and a target format"
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
