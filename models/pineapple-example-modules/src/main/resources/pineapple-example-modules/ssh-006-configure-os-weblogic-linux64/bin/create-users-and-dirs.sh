#!/bin/bash

# create users
/usr/sbin/groupadd oinstall
/usr/sbin/groupadd oracle
/usr/sbin/useradd -m -g oinstall -G oracle weblogic

# set sudoers privileges
echo 'Cmnd_Alias FMW_OP_CMDS=/sbin/ifconfig, /sbin/arping' >> /etc/sudoers
echo '%oinstall ALL = NOPASSWD: FMW_OP_CMDS' >> /etc/sudoers

# create binaries directories
mkdir -p /u01/app/oracle/product/fmw
chown -R weblogic:oinstall /u01/app/oracle/product/fmw
chmod -R 775 /u01/app/oracle/product/fmw

# create shared directories 
mkdir -p /u01/app/oracle/admin
mkdir -p /u01/app/oracle/admin/shared/aservers
mkdir -p /u01/app/oracle/admin/shared/mservers
mkdir -p /u01/app/oracle/admin/shared/certs
mkdir -p /u01/app/oracle/admin/shared/clusters
chown -R weblogic:oinstall /u01/app/oracle/admin
chmod -R 775 /u01/app/oracle/admin

# create unshared node manager directories 
mkdir -p /u01/app/oracle/admin/nodemanager
chown -R weblogic:oinstall /u01/app/oracle/admin/nodemanager
chmod -R 775 /u01/app/oracle/admin/nodemanager

# create shared application directories 
mkdir -p /u01/app/oracle/admin/appfiles
chown -R weblogic:oinstall /u01/app/oracle/admin/appfiles
chmod -R 775 /u01/app/oracle/admin/appfiles

# create log directories
mkdir -p /u01/logs/domains
mkdir -p /u01/logs/nodemanager
chown -R weblogic:oinstall /u01/logs
chmod -R 775 /u01/logs

