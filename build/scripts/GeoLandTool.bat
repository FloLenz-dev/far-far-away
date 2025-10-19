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
@rem SPDX-License-Identifier: Apache-2.0
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  GeoLandTool startup script for Windows
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

@rem Add default JVM options here. You can also use JAVA_OPTS and GEO_LAND_TOOL_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\GeoLandTool.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.9.0.jar;%APP_HOME%\lib\gt-shapefile-27.2.jar;%APP_HOME%\lib\gt-main-27.2.jar;%APP_HOME%\lib\gt-referencing-27.2.jar;%APP_HOME%\lib\jts-core-1.19.0.jar;%APP_HOME%\lib\jai-core-1.1.3.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.9.0.jar;%APP_HOME%\lib\kotlin-stdlib-1.9.0.jar;%APP_HOME%\lib\ejml-ddense-0.34.jar;%APP_HOME%\lib\gt-http-27.2.jar;%APP_HOME%\lib\gt-metadata-27.2.jar;%APP_HOME%\lib\gt-opengis-27.2.jar;%APP_HOME%\lib\commons-pool-1.5.4.jar;%APP_HOME%\lib\jgridshift-core-1.3.jar;%APP_HOME%\lib\GeographicLib-Java-1.49.jar;%APP_HOME%\lib\net.opengis.ows-27.2.jar;%APP_HOME%\lib\org.w3.xlink-27.2.jar;%APP_HOME%\lib\jai_core-1.1.3.jar;%APP_HOME%\lib\kotlin-stdlib-common-1.9.0.jar;%APP_HOME%\lib\annotations-13.0.jar;%APP_HOME%\lib\ejml-core-0.34.jar;%APP_HOME%\lib\commons-text-1.10.0.jar;%APP_HOME%\lib\commons-lang3-3.12.0.jar;%APP_HOME%\lib\jackson-core-2.13.2.jar;%APP_HOME%\lib\re2j-1.6.jar;%APP_HOME%\lib\systems-common-2.0.2.jar;%APP_HOME%\lib\org.eclipse.emf.ecore.xmi-2.15.0.jar;%APP_HOME%\lib\org.eclipse.emf.ecore-2.15.0.jar;%APP_HOME%\lib\org.eclipse.emf.common-2.15.0.jar;%APP_HOME%\lib\si-units-2.0.1.jar;%APP_HOME%\lib\indriya-2.0.2.jar;%APP_HOME%\lib\si-quantity-2.0.1.jar;%APP_HOME%\lib\uom-lib-common-2.0.jar;%APP_HOME%\lib\unit-api-2.0.jar;%APP_HOME%\lib\javax.inject-1.jar;%APP_HOME%\lib\jakarta.annotation-api-1.3.4.jar


@rem Execute GeoLandTool
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GEO_LAND_TOOL_OPTS%  -classpath "%CLASSPATH%" MainKt %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable GEO_LAND_TOOL_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%GEO_LAND_TOOL_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
