#!/bin/bash         
#
# Creates default user and directory setup for Pineapple:
# - Creates a pineapple group.
# - Creates a pineapple user which is member of the pineapple group.
# - Set permissions for the Pineapple installation directory.
# - Create the Pineapple log directory /var/log/pineapple.	
# - Makes runPineapple.sh executable.	
#
# Parameter #1: Defines the Pineapple installation directory, e.g. /opt/pineapple
# - default value is /opt/pineapple 
# Parameter #2: Defines the user which should own the directories, e.g pineapple or weblogic
# - default value is pineapple 
# Parameter #3: Defines the group should own the directories, e.g pineapple or oinstall
# - default value is pineapple 
#
# Example invocation: ./setup.sh /opt/pineapple pineapple pineapple
# Example invocation: ./setup.sh /home/weblogic weblogic oinstall
# Example invocation: ./setup.sh      
# ...which will use the default values 

# set default values
PINEAPPLE_INSTALL_DIR=${1:-/opt/pineapple}
PINEAPPLE_USER=${2:-pineapple}
PINEAPPLE_GROUP=${3:-pineapple}

# create user and group 
/usr/sbin/groupadd $PINEAPPLE_GROUP 
/usr/sbin/useradd -g $PINEAPPLE_GROUP $PINEAPPLE_USER

# set permissions for the Pineapple installation directory 
chown -R $PINEAPPLE_USER:$PINEAPPLE_GROUP $PINEAPPLE_INSTALL_DIR
chmod -R 775 $PINEAPPLE_INSTALL_DIR

# create log directory
mkdir -p /var/log/pineapple
chown -R $PINEAPPLE_USER:$PINEAPPLE_GROUP /var/log/pineapple
chmod -R 775 /var/log/pineapple

# make script executable
chmod +x $PINEAPPLE_INSTALL_DIR/runPineapple.sh