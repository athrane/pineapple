#!/bin/sh

clear

resolvePath() {
	pushd . > /dev/null
	SCRIPT_PATH="${BASH_SOURCE[0]}";
	  while([ -h "${SCRIPT_PATH}" ]) do 
		cd "`dirname "${SCRIPT_PATH}"`"
		SCRIPT_PATH="$(readlink "`basename "${SCRIPT_PATH}"`")"; 
	  done
	cd "`dirname "${SCRIPT_PATH}"`" > /dev/null
	SCRIPT_PATH="`pwd`";
	popd  > /dev/null
	#echo "DEBUG Pineapple directory=[${SCRIPT_PATH}]"
	#echo "DEBUG pwd   =[`pwd`]"
}

# set default values if they aren't defined already
PINEAPPLE_HOME="${PINEAPPLE_HOME:-${HOME}/.pineapple}"
PINEAPPLE_HTTP_HOST="${PINEAPPLE_HTTP_HOST:-localhost}"
PINEAPPLE_HTTP_PORT="${PINEAPPLE_HTTP_PORT:-8080}"

# resolve path
resolvePath 

echo
echo Pineapple configuration details:
echo
echo - Current directory        : ${SCRIPT_PATH}
echo - Pineapple home directory : ${PINEAPPLE_HOME}
echo - Pineapple host name      : ${PINEAPPLE_HTTP_HOST}
echo - Pineapple port number    : ${PINEAPPLE_HTTP_PORT}
echo - Pineapple endpoint       : http://${PINEAPPLE_HTTP_HOST}:${PINEAPPLE_HTTP_PORT}
echo - Java home                : n/a
echo
echo Starting Pineapple - a browser window will be opened unless your computer forbids it.

# define variables
piddir=/var/run/pineapple
pidfile=${piddir}/pineapple.pid
VM_OPTS="-Dsun.lang.ClassLoader.allowArraySyntax=true"
PINEAPPLE_OPTS="-Dpineapple.jettystarter.home=${SCRIPT_PATH}" 
PINEAPPLE_OPTS="${PINEAPPLE_OPTS} -Dpineapple.jettystarter.host=${PINEAPPLE_HTTP_HOST}" 
PINEAPPLE_OPTS="${PINEAPPLE_OPTS} -Dpineapple.jettystarter.port=${PINEAPPLE_HTTP_PORT}"
PINEAPPLE_OPTS="${PINEAPPLE_OPTS} -Dpineapple.jettystarter.stdoutlogging=true"
PINEAPPLE_OPTS="${PINEAPPLE_OPTS} -Dpineapple.home.dir=${PINEAPPLE_HOME}"
LOG4J_OPTS="-Dslf4j=false -Dlog4j.configuration=file:${SCRIPT_PATH}/conf/log4j.properties" 
PINAPPLE_JAR="${SCRIPT_PATH}/lib/pineapple-jetty-starter-1.0.0.jar"

# run in background if daemon piddir is defined
if [ -d "$piddir" ]
then
  echo Daemon piddir [$piddir] found. Will start as background process.
  exec java ${JVM_OPTS} ${PINEAPPLE_OPTS} ${LOG4J_OPTS} ${JETTY_OPTS} -jar ${PINAPPLE_JAR} /dev/null 2>&1 & echo $! >> $pidfile
else
  echo Daemon piddir [$piddir] not found. Will start as foreground process.
  exec java ${JVM_OPTS} ${PINEAPPLE_OPTS} ${LOG4J_OPTS} ${JETTY_OPTS} -jar ${PINAPPLE_JAR} /dev/null 2>&1
fi