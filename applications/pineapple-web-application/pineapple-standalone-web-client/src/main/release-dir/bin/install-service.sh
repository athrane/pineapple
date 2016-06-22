#!/bin/bash         
#
# Installs Pineapple OS service into /etc/init.d
# Parameter #1: Defines the source file which should be installed as a service
# - default value is /opt/pineapple/bin/pineapple-service.sh
# Parameter #2: Defines the user which should own the service, e.g pineapple or oracle
# - default value is pineapple 
# Parameter #3: Defines the group should own the service, e.g pineapple or oinstall
# - default value is pineapple 
#
# Example invocation: ./install-service.sh /tmp/pineapple oracle oinstall
# Example invocation: ./install-service.sh /opt/pineapple/bin/pineapple-service.sh pineapple pineapple
# Example invocation: ./install-service.sh
#
set -e

# set default values
SOURCE_FILE=${1:-/opt/pineapple/bin/pineapple-service.sh}
USER=${2:-pineapple}
GROUP=${3:-pineapple}
SERVICE=pineapple

cp $SOURCE_FILE /etc/init.d/$SERVICE
chown $USER:$GROUP /etc/init.d/$SERVICE
chmod 775 /etc/init.d/$SERVICE
/sbin/chkconfig --add /etc/init.d/$SERVICE
/sbin/service $SERVICE start
