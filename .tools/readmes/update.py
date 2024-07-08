from pathlib import Path
from subprocess import check_call
from sys import executable

check_call([executable, "-m", "pip", "install", "-e", Path(__file__).parent])
