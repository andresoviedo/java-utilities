7za a -r -mx=0 program_data.7z .\program_data\*.*
copy /b 7zSD.sfx + config.txt + program_data.7z MyInstaller.exe
MyInstaller.exe
