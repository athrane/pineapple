# Pull base image
# ---------------
FROM pineapple/centos-openjdk-java:8

# Maintainer
# ----------
MAINTAINER Allan Thrane Andersen <einheriii@gmail.com>

# Define variable
ENV PINEAPPLE_ARCHIVE pineapple-standalone-web-client-1.10.0.zip
ENV TESTCASES_MODULES dn-testautomation-docker-tests.zip
ENV PINEAPPLE_INSTALL_DIR /opt
ENV PINEAPPLE_HOME_DIR /var/pineapple
ENV PINEAPPLE_DIR $PINEAPPLE_INSTALL_DIR/pineapple-standalone-web-client-1.10.0
ENV PINEAPPLE_USER pineapple
ENV PINEAPPLE_GROUP pineapple

# Define runtime variables to configure Pineapple
ENV PINEAPPLE_HOME $PINEAPPLE_HOME_DIR
ENV PINEAPPLE_HTTP_HOST 0.0.0.0
ENV PINEAPPLE_HTTP_PORT 8080

# Install unzip
RUN yum --assumeyes install unzip
RUN yum clean all

# Install Pineapple binary 
COPY $PINEAPPLE_ARCHIVE /tmp/
RUN unzip /tmp/$PINEAPPLE_ARCHIVE -d $PINEAPPLE_INSTALL_DIR
RUN rm -rf /tmp/$PINEAPPLE_ARCHIVE

# Creates default user, directory and make pineapple executable  
RUN chmod +x $PINEAPPLE_DIR/bin/setup.sh
RUN $PINEAPPLE_DIR/bin/setup.sh $PINEAPPLE_DIR $PINEAPPLE_USER $PINEAPPLE_GROUP

# Temporary solution to make logging work
RUN mkdir -p /var/pineapple/logs
RUN chown -R $PINEAPPLE_USER:$PINEAPPLE_GROUP /var/pineapple
RUN chmod -R 775 /var/pineapple

# Make pineapple executable
WORKDIR $PINEAPPLE_DIR

# Expose port to host
EXPOSE $PINEAPPLE_HTTP_PORT

# Define volume
VOLUME $PINEAPPLE_HOME_DIR

# set user 
USER $PINEAPPLE_USER

# Start pineapple when container is created
ENTRYPOINT $PINEAPPLE_DIR/runPineapple.sh