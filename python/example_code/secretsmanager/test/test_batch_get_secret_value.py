import sys
import os
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import batch_get_secret_value as batch

def test_batch():
    results = batch.batch_get_secrets("foo")
    assert not results
