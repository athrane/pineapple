#!/bin/bash

###
# chkconfig: 35 97 02
# Description: Start Pineapple
###

#
# Source function library.
#
if [ -f /etc/rc.d/init.d/functions ]; then
    . /etc/rc.d/init.d/functions
fi

# Define service script variables
prog=pineapple
piddir=/var/run/$prog
pidfile=$piddir/$prog.pid
lockfile=/var/lock/subsys/$prog
RETVAL=0

# Define Pineapple variables
export PINEAPPLE_HTTP_PORT=7099
export PINEAPPLE_HTTP_HOST=0.0.0.0
PINEAPPLE_USER=pineapple
PINEAPPLE_GROUP=pineapple
PINEAPPLE_INSTALL=/opt/pineapple
export PINEAPPLE_HOME=/home/${PINEAPPLE_USER}/.pineapple
PINEAPPLE_EXE=runPineapple.sh
PINEAPPLE_LOG=/var/log/pineapple/pineapple-service.log

# Create Pineapple PID directory owned by the service user
createPidDir() {
  mkdir -p $piddir
  chown -R $PINEAPPLE_USER:$PINEAPPLE_GROUP $piddir
  chmod -R 775 $piddir  
}

rh_status() {
  status -p $pidfile $PINEAPPLE_EXE
  RETVAL=$?
  return $RETVAL
}

rh_status_q() {
  rh_status >/dev/null 2>&1
}

# Wait for 60 seconds to see if Jetty starts up correctly
validateJettyIsRunning() {
  echo -n "Starting pineapple web server: "
  timeout 60s grep -q 'Successfully started Jetty' <(sudo -u $PINEAPPLE_USER tail -f $PINEAPPLE_LOG)
  RETVAL=$?
  [ $RETVAL -eq 0 ] && success || failure
  echo
  return $REVAL
}

start(){
  echo  -n "Starting pineapple daemon process: "  
  createPidDir
  daemon --user=$PINEAPPLE_USER $PINEAPPLE_INSTALL/$PINEAPPLE_EXE > $PINEAPPLE_LOG 2>&1 &
  RETVAL=$?
  [ $RETVAL -eq 0 ] && touch $lockfile
  [ $RETVAL -eq 0 ] && success || failure
  echo
  validateJettyIsRunning
  return $RETVAL
}

stop(){
  echo -n $"Stopping $prog: "
  killproc -p $pidfile $PINEAPPLE_INSTALL/$PINEAPPLE_EXE
  RETVAL=$?
  echo
  [ $RETVAL -eq 0 ] && rm -f $lockfile
  return $RETVAL
}

case "$1" in
  start)
    rh_status_q && exit 0
    start
    ;;
  stop)
    if ! rh_status_q; then
      rm -f $lockfile
      exit 0
    fi
    stop
    ;;
  status)
    rh_status
    ;;
  restart)
    stop
    start
    ;;
  *)
   echo $"Usage: $0 {start|stop|status|restart}"
   RETVAL=1
esac

exit $RETVAL
