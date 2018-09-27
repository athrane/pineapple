
# Pull base image
# ---------------
FROM centos:latest

# Maintainer
# ----------
MAINTAINER Allan Thrane Andersen <einheriii@gmail.com>

# Define variable
ENV JAVA_ARCHIVE openjdk-11+28_linux-x64_bin.tar.gz

# Stage pineapple binary 
COPY $JAVA_ARCHIVE /tmp/

# install java
WORKDIR /tmp
RUN chmod a+x $JAVA_ARCHIVE && \
	tar -xvzf $JAVA_ARCHIVE && \
	rm -rf $JAVA_ARCHIVE
