class Config:
    def __init__(self):
        self.delivery_stream_name = (
            "ENTER YOUR DELIVERY STREAM NAME HERE"
        )
        self.region = "us-east-1"
        self.sample_data_file = "../../../../../workflows/kinesis-firehose/resources/sample_records.json"


def get_config():
    return Config()
