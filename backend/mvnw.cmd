@echo off
setlocal
set MAVEN_PROJECTBASEDIR=%~dp0
set "MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%"
if defined JAVA_HOME (
  set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
) else (
  set "JAVA_EXE=java"
)
"%JAVA_EXE%" -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" -cp "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain %*
