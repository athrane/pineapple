#!/bin/bash         
#
# Manually creates Pineapple runtime  directories.
# Parameter #1: Defines the Pineapple home directory, e.g /var/pineapple or /home/weblogic
# - default value is /var/pineapple 
# Parameter #2: Defines the user which should own the directories, e.g pineapple or weblogic
# - default value is pineapple 
# Parameter #3: Defines the group should own the directories, e.g pineapple or oinstall
# - default value is pineapple 
#
# Example invocation: ./create-runtime-dirs.sh var/pineapple pineapple pineapple
# Example invocation: ./create-runtime-dirs.sh /home/weblogic weblogic oinstall
# Example invocation: ./create-runtime-dirs.sh      
# ...which will use the default values 

set -e

# set default values
HOME_DIR=${1:-/var/pineapple}
USER=${2:-pineapple}
GROUP=${3:-pineapple}

mkdir -p $HOME_DIR
mkdir -p $HOME_DIR/conf
mkdir -p $HOME_DIR/modules
mkdir -p $HOME_DIR/reports
chown -R $USER:$GROUP $HOME_DIR
chmod -R 775 $HOME_DIR
