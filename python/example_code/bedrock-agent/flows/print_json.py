
import json
from datetime import datetime


# Custom JSON encoder to handle datetime objects
class DateTimeEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, datetime):
            return obj.isoformat()
        return super().default(obj)
    
def pretty_print_json(json_obj):
    """
    Pretty-prints a JSON object.

    Args:
        json_obj (dict): The JSON object to pretty-print.
    """
    print(json.dumps(json_obj, indent=2, cls=DateTimeEncoder))