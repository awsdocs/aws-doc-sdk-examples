import sys
import os
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import get_secret_value as secret

def test_batch():
    results = secret.get_secret("foo")
    assert 'not found' in results
