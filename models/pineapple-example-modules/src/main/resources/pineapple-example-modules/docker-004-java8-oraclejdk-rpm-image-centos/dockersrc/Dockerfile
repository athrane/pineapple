
# Pull base image
# ---------------
FROM centos:latest

# Maintainer
# ----------
MAINTAINER Allan Thrane Andersen <einheriii@gmail.com>

# Define variable
ENV JAVA_ARCHIVE jdk-8u40-linux-x64.rpm

# Stage pineapple binary 
COPY $JAVA_ARCHIVE /tmp/

# install java
WORKDIR /tmp
RUN chmod a+x $JAVA_ARCHIVE
RUN yum --assumeyes install $JAVA_ARCHIVE
RUN rm -rf $JAVA_ARCHIVE