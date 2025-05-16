import os.path

FNAME = 'VERSION'

root = os.path.dirname(os.path.abspath(__file__))
with open(os.path.join(root, FNAME), 'r', encoding='utf-8') as f:
    __version__ = f.read().strip()
