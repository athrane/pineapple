
# Pull base image
# ---------------
FROM centos:latest

# Maintainer
# ----------
MAINTAINER Allan Thrane Andersen <einheriii@gmail.com>

# Define variable
ENV JAVA_ARCHIVE java-1.8.0-openjdk-devel

# install java
RUN yum -y install $JAVA_ARCHIVE

# clear YUM cache
RUN yum clean all

