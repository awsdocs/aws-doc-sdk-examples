from pathlib import Path
from subprocess import run, DEVNULL, PIPE, CalledProcessError
from sys import executable

try:
    run(
        [executable, "-m", "pip", "install", "-e", Path(__file__).parent],
        check=True,
        stderr=PIPE,
        stdin=DEVNULL,
        stdout=DEVNULL,
    )
except CalledProcessError as _cpe:
    raise RuntimeError(f"Update failed: {_cpe.stderr}")
