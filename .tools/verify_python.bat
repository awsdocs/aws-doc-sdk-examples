@echo off
set MIN_VERSION=11

:: Check that the minor version of the default python3 command is at least 11.
FOR /F "tokens=1,2 delims=." %%G IN ('python --version') DO (
   SET PYTHON_VERSION_MINOR=%%H
)

IF %PYTHON_VERSION_MINOR% LSS %MIN_VERSION% (
  echo python3 default minor version less than %MIN_VERSION%
  EXIT /B 1
)

python -m pip install --upgrade pip
python -m pip install -r base_requirements.txt