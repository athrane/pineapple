#!/bin/bash

###
# chkconfig: 35 97 02
# Description: Start the Weblogic Node Manager
###

# Standard functions
. /etc/init.d/functions

# Defines system variables
export WL_OWNER="weblogic"
export NM_HOME="/opt/oracle/nodemanager"
export USER=$(whoami)

###############################################################################
# restart
###############################################################################
restart(){
  stop
  sleep 20
  start
}

###############################################################################
# status
###############################################################################
status(){
  PCOUNT=`ps -ef |  grep "java" |  grep "weblogic.NodeManager" | grep -v grep | wc -l`
  if [ "$PCOUNT" -eq 0 ]
  then
    echo "Service is not running"
  else
    echo "Service is running in `echo $PCOUNT` processes"
  fi
}

###############################################################################
# start
###############################################################################
start(){
  echo "Launching NodeManager"

  if [ "$USER" != "$WL_OWNER" ]
    then
      su - $WL_OWNER -c "nohup $NM_HOME/startNodeManager.sh > /tmp/nohup.out &"
    else
      nohup $NM_HOME/startNodeManager.sh > /tmp/nohup.out &
  fi
}

###############################################################################
# stop
###############################################################################
stop(){
  PID=`ps -ef | grep "java" |  grep "weblogic.NodeManager" | grep -v grep | grep -v start | cut -c 10-14`
  if [ "$PID" != "" ]
    then
      echo "Stopping NodeManager"
      kill -15 $PID
    else
       echo "NodeManager not running"
  fi
}

###############################################################################
# main
###############################################################################
case "$1" in
  start)
    start
    ;;
  stop)
    stop
    ;;
  status)
    status
    ;;
  restart)
    restart
    ;;
  *)
   echo $"Usage: $0 {start|stop|status|restart}"
   RETVAL=1
esac

exit $RETVAL