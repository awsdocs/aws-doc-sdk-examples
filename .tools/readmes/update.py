from pathlib import Path
from subprocess import run
from sys import executable

run(
    [executable, "-m", "pip", "install", "-e", Path(__file__).parent],
    check=True,
    capture_output=False,
)
