import logging

from exceptions import InvalidRecordSizeError

MAX_RECORD_SIZE = 1024 * 1024  # 1 MB

def validate_record_size(record):
    record_size = len(record.encode('utf-8'))
    if record_size > MAX_RECORD_SIZE:
        logging.error(f'Record size ({record_size} bytes) exceeds the maximum allowed size ({MAX_RECORD_SIZE} bytes)')
        raise InvalidRecordSizeError(f'Record size ({record_size} bytes) exceeds the maximum allowed size ({MAX_RECORD_SIZE} bytes)')