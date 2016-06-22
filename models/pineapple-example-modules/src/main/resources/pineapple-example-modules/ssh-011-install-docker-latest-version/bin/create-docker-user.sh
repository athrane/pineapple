#!/bin/bash

# create users
/usr/sbin/groupadd docker
/usr/sbin/useradd -m -g docker docker

# set sudoers privileges
echo '%docker ALL=(ALL)       ALL' >> /etc/sudoers