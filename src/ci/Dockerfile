# Pull base image
# ---------------
FROM pineapple/centos-openjdk-java:8

# Maintainer
# ----------
MAINTAINER Allan Thrane Andersen <einheriii@gmail.com>

# Define variable
ENV PINEAPPLE_ARCHIVE pineapple-standalone-web-client-1.10.0.zip \
	PINEAPPLE_INSTALL_DIR /opt \
	PINEAPPLE_HOME_DIR /var/pineapple \
	PINEAPPLE_DIR $PINEAPPLE_INSTALL_DIR/pineapple-standalone-web-client-1.10.0 \
	PINEAPPLE_USER pineapple \
	PINEAPPLE_GROUP pineapple \
	PINEAPPLE_HOME $PINEAPPLE_HOME_DIR \
	PINEAPPLE_HTTP_HOST 0.0.0.0 \
	PINEAPPLE_HTTP_PORT 8080

# Install unzip
RUN yum --assumeyes install unzip && \
	yum clean all

# Install Pineapple binary 
# Create default user, directory and make pineapple executable  
RUN ls -la $SHIPPABLE_BUILD_DIR
RUN cd $SHIPPABLE_BUILD_DIR
RUN pwd
COPY $SHIPPABLE_BUILD_DIR/$PINEAPPLE_ARCHIVE /tmp/  
RUN unzip /tmp/$PINEAPPLE_ARCHIVE -d $PINEAPPLE_INSTALL_DIR && \
	rm -rf /tmp/$PINEAPPLE_ARCHIVE && \
	chmod +x $PINEAPPLE_DIR/bin/setup.sh && \
	$PINEAPPLE_DIR/bin/setup.sh $PINEAPPLE_DIR $PINEAPPLE_USER $PINEAPPLE_GROUP

# Temporary solution to make logging work
RUN mkdir -p /var/pineapple/logs && \
	chown -R $PINEAPPLE_USER:$PINEAPPLE_GROUP /var/pineapple && \
	chmod -R 775 /var/pineapple

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