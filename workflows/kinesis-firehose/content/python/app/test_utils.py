import unittest

from utils import validate_record_size
from exceptions import InvalidRecordSizeError

class TestUtils(unittest.TestCase):
    def test_validate_record_size_valid(self):
        record = 'a' * 1024 * 1024  # 1 MB
        validate_record_size(record)  # Should not raise an exception

    def test_validate_record_size_invalid(self):
        record = 'a' * (1024 * 1024 + 1)  # 1 MB + 1 byte
        with self.assertRaises(InvalidRecordSizeError):
            validate_record_size(record)

if __name__ == '__main__':
    unittest.main()