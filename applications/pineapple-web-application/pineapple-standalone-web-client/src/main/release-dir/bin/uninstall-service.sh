#!/bin/bash         
#
# Uninstalls Pineapple OS service from /etc/init.d
#
set -e

# set default values
SERVICE=pineapple

if [ -e /etc/init.d/$SERVICE ] ;then
	/sbin/service $SERVICE stop
	/sbin/chkconfig --del $SERVICE
	rm -rf /etc/init.d/$SERVICE
	echo $SERVICE service stoped and removed
else
	echo $SERVICE service not found
fi
