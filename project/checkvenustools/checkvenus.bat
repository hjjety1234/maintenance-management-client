
@set curdir=%~dp0
@set inputpath="%curdir%..\assets"
@md  %inputpath%\output
@set outputpath="%curdir%..\assets\output"
@set file=%curdir%\venus\module\

@set lua="%curdir%\tools\Lua\lua.exe"
@set luac="%curdir%\tools\Lua\luac.exe" -s -o
@set formatxml="%curdir%\tools\formatxml2.lua" 
@set kwl="%curdir%\tools\kwl.lua"
@set var=ture
@set var1=error

@md %outputpath%\venus
@"%curdir%\tools\WinRAR.exe" x -o+ -ibck %inputpath%\venus.zip  %outputpath%\venus

::check 
@pushd %outputpath%\venus\lib2\WD\
@set checklist=liblua.so,liblauncher.so,libcomrepository.so,libapi.so
@for %%i in (%checklist%) do @(
@if not exist %%i (
@set var=error
@echo %outputpath%\venus\lib2\WD\%%i does not exist && goto :label ))
@popd 

@pushd %outputpath%\venus\lib2\dresden\
@set checklist=libdresden.so
@for %%i in (%checklist%) do @(
@if not exist %%i (
@set var=error
@echo %outputpath%\venus\lib2\dresden\%%i does not exist && goto :label ))
@popd 

@pushd %outputpath%\venus\module\
@set checklist1=comrepository.xml
@for %%i in (%checklist1%) do @(
@if not exist %%i (
@set var=error
@echo %outputpath%\venus\module\%%i does not exist && goto :label  ))
@popd
::check 

::module
@if exist %outputpath%\venus\module\*.zip (
@for /f "usebackq delims=" %%i in (`dir  /b %outputpath%\venus\module\*.zip`) do (
@md %outputpath%\venus\module\backup
@md %outputpath%\venus\module\backup\%%~ni
@"%curdir%\tools\WinRAR.exe" x -o+ -ibck %outputpath%\venus\module\%%i %outputpath%\venus\module\backup\%%~ni )

@for /f "usebackq delims=" %%i in (`dir /s /b %outputpath%\venus\module\*.lua`) do @%lua% %kwl% "%%i" "%%i"
@for /f "usebackq delims=" %%i in (`dir /s /b %outputpath%\venus\module\*.lua`) do @%luac% "%%i" "%%i"
@for /f "usebackq delims=" %%i in (`dir /s /b %outputpath%\venus\module\*.wdml`) do @%lua% %formatxml% "%%i" "%%i"
@for /f "usebackq delims=" %%i in (`dir  /b %outputpath%\venus\module\*.zip`) do (
@"%curdir%\tools\WinRAR.exe" a -afzip -r -ep1 -ibck -m0 -o+ %outputpath%\venus\module\%%~ni.zip %outputpath%\venus\module\backup\%%~ni\ )
@rd /s /q %outputpath%\venus\module\backup )
::module

::framework
@if exist %outputpath%\venus\framework.dat (
@move  %outputpath%\venus\framework.dat %outputpath%\venus\frameworkdat.zip )
@if exist %outputpath%\venus\*.zip (
@for /f "usebackq delims=" %%i in (`dir  /b %outputpath%\venus\*.zip`) do (
@md %outputpath%\venus\backup2
@md %outputpath%\venus\backup2\%%~ni
@"%curdir%\tools\WinRAR.exe" x -o+ -ibck %outputpath%\venus\%%i %outputpath%\venus\backup2\%%~ni )

@for /f "usebackq delims=" %%i in (`dir /s /b %outputpath%\venus\*.lua`) do @%lua% %kwl% "%%i" "%%i"
@for /f "usebackq delims=" %%i in (`dir /s /b %outputpath%\venus\*.lua`) do @%luac% "%%i" "%%i"
@for /f "usebackq delims=" %%i in (`dir /s /b %outputpath%\venus\*.wdml`) do @%lua% %formatxml% "%%i" "%%i"
@for /f "usebackq delims=" %%i in (`dir  /b %outputpath%\venus\*.zip`) do (
@"%curdir%\tools\WinRAR.exe" a -afzip -r -ep1 -ibck -m0 -o+ %outputpath%\venus\%%~ni.zip %outputpath%\venus\backup2\%%~ni\ )

@move   %outputpath%\venus\frameworkdat.zip %outputpath%\venus\framework.dat
@rd /s /q %outputpath%\venus\backup2
)else (
@for /f "usebackq delims=" %%i in (`dir /s /b %outputpath%\venus\*.lua`) do @%lua% %kwl% "%%i" "%%i"
@for /f "usebackq delims=" %%i in (`dir /s /b %outputpath%\venus\*.lua`) do @%luac% "%%i" "%%i"
@for /f "usebackq delims=" %%i in (`dir /s /b %outputpath%\venus\*.wdml`) do @%lua% %formatxml% "%%i" "%%i" )


::framework
@"%curdir%\tools\WinRAR.exe" a -afzip -r -ep1 -ibck -m3 -o+ %outputpath%\venus.zip %outputpath%\venus\
@rd /s /q %outputpath%\venus
@copy %outputpath%\venus.zip %inputpath%\venus.zip
@rd /s /q %inputpath%\output

:label
@echo %var%
@if %var%==%var1% (
@popd
@rd /s /q %inputpath%\output
@cmd.exe )


