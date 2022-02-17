import unittest
import importlib
import logging
import jsonpickle
import json
from aws_xray_sdk.core import xray_recorder

logger = logging.getLogger()
xray_recorder.configure(
  context_missing='LOG_ERROR'
)
#function = importlib.import_module(lambda_function)

xray_recorder.begin_segment('test_init')
function = __import__('lambda_function')
handler = function.lambda_handler
xray_recorder.end_segment()

class TestFunction(unittest.TestCase):

  def test_function(self):
    xray_recorder.begin_segment('test_function')
    file = open('event.json', 'rb')
    try:
      ba = bytearray(file.read())
      event = jsonpickle.decode(ba)
      logger.warning('## EVENT')
      logger.warning(jsonpickle.encode(event))
      context = {'requestid' : '1234'}
      result = handler(event, context)
      print(str(result))
      self.assertRegex(str(result), 'FunctionCount', 'Should match')
    finally:
      file.close()
    file.close()
    xray_recorder.end_segment()

if __name__ == '__main__':
    unittest.main()