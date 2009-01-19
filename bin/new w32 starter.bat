@echo off

cd data
java -Xss2m -Xms512m -Xmx512m -Djava.library.path="native/win32" -cp lib/audiolib/vorbisspi1.0.3.jar;lib/audiolib/tritonus_share.jar;lib/audiolib/jorbis-0.0.15.jar;lib/audiolib/jogg-0.0.7.jar;lib/jogl.jar;lib/lwjgl.jar;lib/lwjgl_util.jar;. main.SuperMain

if errorlevel 1 goto javanotfound
goto end

:javanotfound
> usermessage.vbs ECHO Option Explicit
>> usermessage.vbs ECHO Dim Title, Message
>> usermessage.vbs ECHO Title = "Java missing"
>> usermessage.vbs ECHO Message = "Java Runtime Environment (JRE) not found." + vbCrLf + "To play this game, you have to install JRE 1.6 or newer." + vbCrLf + "Get it from http://www.java.com/download/."
>> usermessage.vbs ECHO MsgBox Message, vbInformation + vbOKOnly, Title
WSCRIPT.EXE usermessage.vbs
DEL usermessage.vbs
start http://www.java.com/download/
goto end

:end