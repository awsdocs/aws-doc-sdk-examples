@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  athena startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and ATHENA_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\athena-1.0-SNAPSHOT.jar;%APP_HOME%\lib\http-client-engine-crt-jvm-0.28.0.jar;%APP_HOME%\lib\athena-jvm-0.33.1-beta.jar;%APP_HOME%\lib\secretsmanager-jvm-0.33.1-beta.jar;%APP_HOME%\lib\aws-config-jvm-0.33.1-beta.jar;%APP_HOME%\lib\aws-http-jvm-0.33.1-beta.jar;%APP_HOME%\lib\aws-endpoint-jvm-0.33.1-beta.jar;%APP_HOME%\lib\aws-json-protocols-jvm-0.28.1.jar;%APP_HOME%\lib\aws-xml-protocols-jvm-0.28.1.jar;%APP_HOME%\lib\aws-protocol-core-jvm-0.28.1.jar;%APP_HOME%\lib\aws-signing-default-jvm-0.28.1.jar;%APP_HOME%\lib\http-auth-aws-jvm-0.28.1.jar;%APP_HOME%\lib\aws-signing-common-jvm-0.28.1.jar;%APP_HOME%\lib\http-client-engine-default-jvm-0.28.1.jar;%APP_HOME%\lib\http-client-engine-okhttp-jvm-0.28.1.jar;%APP_HOME%\lib\http-client-jvm-0.28.1.jar;%APP_HOME%\lib\aws-core-jvm-0.33.1-beta.jar;%APP_HOME%\lib\smithy-client-jvm-0.28.1.jar;%APP_HOME%\lib\aws-credentials-jvm-0.28.1.jar;%APP_HOME%\lib\http-auth-jvm-0.28.1.jar;%APP_HOME%\lib\crt-util-jvm-0.28.0.jar;%APP_HOME%\lib\http-auth-api-jvm-0.28.1.jar;%APP_HOME%\lib\http-jvm-0.28.1.jar;%APP_HOME%\lib\identity-api-jvm-0.28.1.jar;%APP_HOME%\lib\telemetry-defaults-jvm-0.28.1.jar;%APP_HOME%\lib\logging-slf4j2-jvm-0.28.1.jar;%APP_HOME%\lib\telemetry-api-jvm-0.28.1.jar;%APP_HOME%\lib\serde-json-jvm-0.28.1.jar;%APP_HOME%\lib\serde-xml-jvm-0.28.1.jar;%APP_HOME%\lib\serde-form-url-jvm-0.28.1.jar;%APP_HOME%\lib\serde-jvm-0.28.1.jar;%APP_HOME%\lib\runtime-core-jvm-0.28.1.jar;%APP_HOME%\lib\aws-crt-kotlin-jvm-0.8.0.jar;%APP_HOME%\lib\kotlinx-coroutines-jdk8-1.7.3.jar;%APP_HOME%\lib\okhttp-coroutines-jvm-5.0.0-alpha.11.jar;%APP_HOME%\lib\kotlinx-coroutines-core-jvm-1.7.3.jar;%APP_HOME%\lib\okhttp-jvm-5.0.0-alpha.11.jar;%APP_HOME%\lib\okio-jvm-3.3.0.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.9.0.jar;%APP_HOME%\lib\gson-2.10.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.9.0.jar;%APP_HOME%\lib\atomicfu-jvm-0.22.0.jar;%APP_HOME%\lib\kotlin-stdlib-1.9.10.jar;%APP_HOME%\lib\kotlin-stdlib-common-1.9.10.jar;%APP_HOME%\lib\annotations-23.0.0.jar;%APP_HOME%\lib\slf4j-api-2.0.6.jar;%APP_HOME%\lib\aws-crt-0.27.4.jar


@rem Execute athena
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %ATHENA_OPTS%  -classpath "%CLASSPATH%"  %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable ATHENA_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%ATHENA_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
