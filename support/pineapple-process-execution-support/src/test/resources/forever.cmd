@ECHO OFF 

REM run an infinite loop so the script will never ever terminate on its behalf
REM and append a '.' after each second
 	
:LOOP
	ECHO x.
	@ping 127.0.0.1 -n 2 -w 1000 > nul
GOTO LOOP