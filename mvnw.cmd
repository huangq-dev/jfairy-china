@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.2.0
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars
@REM MAVEN_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM MAVEN_BATCH_PAUSE - set to 'on' to wait for a keystroke before ending
@REM MAVEN_OPTS - parameters passed to the Java VM when running Maven
@REM     e.g. to debug Maven itself, use
@REM set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM MAVEN_SKIP_RC - flag to disable loading of mavenrc files
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_PSMODULEP_SAVE=%PSModulePath%
@SET PSModulePath=
@FOR /F "usebackq tokens=1* delims==" %%A IN (`powershell -noprofile "& {$scriptDir='%~dp0'; $script='%__MVNW_ARG0_NAME__%'; icm -ScriptBlock ([Scriptblock]::Create((Get-Content -Raw '%~f0'))) -NoNewScope}"`) DO @(
  IF "%%A"=="MVN_CMD" (set __MVNW_CMD__=%%B) ELSE IF "%%B"=="" (echo %%A) ELSE (echo %%A=%%B)
)
@SET PSModulePath=%__MVNW_PSMODULEP_SAVE%
@SET __MVNW_PSMODULEP_SAVE=
@SET __MVNW_ARG0_NAME__=
@IF NOT "%__MVNW_CMD__%"=="" (%__MVNW_CMD__% %*)
@echo Cannot start maven from wrapper >&2 && exit /b 1
@GOTO :EOF

:main
@setlocal
set __MVNW_PATH__=%~dp0
set __MVNW_JAVA_EXE__=java.exe
if not defined JAVA_HOME (
  echo Error: JAVA_HOME not found in your environment. >&2
  echo Please set the JAVA_HOME variable in your environment to match the >&2
  echo location of your Java installation. >&2
  exit /b 1
)
set "JAVA_HOME=%JAVA_HOME:"=%"
set __MVNW_JAVA_EXE__=%JAVA_HOME%\bin\java.exe

if not exist "%__MVNW_JAVA_EXE__%" (
  echo Error: JAVA_HOME is set to an invalid directory. >&2
  echo JAVA_HOME = "%JAVA_HOME%" >&2
  echo Please set the JAVA_HOME variable in your environment to match the >&2
  echo location of your Java installation. >&2
  exit /b 1
)

set __MVNW_DIR__=%__MVNW_PATH__%.mvn\wrapper
set __MVNW_JAR__=%__MVNW_DIR__%\maven-wrapper.jar
set __MVNW_PROPERTIES__=%__MVNW_DIR__%\maven-wrapper.properties

set __MVNW_URL__=
for /F "usebackq tokens=1,2 delims==" %%A in ("%__MVNW_PROPERTIES__%") do (
  if "%%A"=="wrapperUrl" set __MVNW_URL__=%%B
)

if not exist "%__MVNW_JAR__%" (
  if not defined __MVNW_URL__ (
    echo Error: wrapperUrl not set in "%__MVNW_PROPERTIES__%" >&2
    exit /b 1
  )
  powershell -Command "Invoke-WebRequest -Uri '%__MVNW_URL__%' -OutFile '%__MVNW_JAR__%'"
  if %ERRORLEVEL% NEQ 0 (
    echo Error downloading Maven wrapper >&2
    exit /b 1
  )
)

set __MVNW_ARGS__=
if exist "%__MVNW_PROPERTIES__%" (
  for /F "usebackq tokens=1,2 delims==" %%A in ("%__MVNW_PROPERTIES__%") do (
    if "%%A"=="wrapperArgs" set __MVNW_ARGS__=%%B
  )
)

set __MVNW_CMD_LINE_ARGS__=%*

"%__MVNW_JAVA_EXE__%" %__MVNW_ARGS__% -jar "%__MVNW_JAR__%" %__MVNW_CMD_LINE_ARGS__%
exit /b %ERRORLEVEL%