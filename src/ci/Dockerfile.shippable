

# Pull base image for builder image
# ----------------------------------
FROM openjdk:11.0-jdk-oraclelinux7 as builder

LABEL maintainer="einheriii@gmail.com"

ARG PINEAPPLE_VERSION=1.12.0
ARG PINEAPPLE_HOME=/var/pineapple
ARG PINEAPPLE_INSTALL=/opt/pineapple

# Map arguments into environment variables
ENV PINEAPPLE_VERSION=${PINEAPPLE_VERSION} \
	PINEAPPLE_HOME=${PINEAPPLE_HOME} \
    PINEAPPLE_INSTALL=${PINEAPPLE_INSTALL}
		
RUN set -eux; \
	yum install -y unzip; \
	rm -rf /var/cache/yum

# Install Pineapple binary 
COPY pineapple-standalone-web-client-${PINEAPPLE_VERSION}.zip /tmp/archive.zip 
RUN unzip /tmp/archive.zip -d /opt; \
	mv /opt/pineapple-standalone-web-client-${PINEAPPLE_VERSION} ${PINEAPPLE_INSTALL}

# make script executable
RUN mkdir -p ${PINEAPPLE_HOME} \
	&& chmod +x ${PINEAPPLE_INSTALL}/runPineapple.sh 

# Pull base image for final image
# --------------------------------
FROM openjdk:11.0-jdk-oraclelinux7 

LABEL maintainer="einheriii@gmail.com"

ARG PINEAPPLE_HOME=/var/pineapple
ARG PINEAPPLE_INSTALL=/opt/pineapple
ARG PINEAPPLE_HTTP_HOST=0.0.0.0
ARG PINEAPPLE_HTTP_PORT=8080

# Pineapple is run with user `pineapple`, uid = 1000
# If a volume is mounted from the host or a data container, ensure the same UID is used.
ARG PINEAPPLE_USER=pineapple
ARG PINEAPPLE_GROUP=pineapple
ARG PINEAPPLE_UID=1000
ARG PINEAPPLE_GID=1000

# Map arguments into environment variables
ENV PINEAPPLE_HOME=${PINEAPPLE_HOME} \
    PINEAPPLE_INSTALL=${PINEAPPLE_INSTALL} \
	PINEAPPLE_HTTP_HOST=${PINEAPPLE_HTTP_HOST} \
	PINEAPPLE_HTTP_PORT=${PINEAPPLE_HTTP_PORT} 
	
# Copy directories from builder
COPY --from=builder ${PINEAPPLE_INSTALL} ${PINEAPPLE_INSTALL}
COPY --from=builder ${PINEAPPLE_HOME} ${PINEAPPLE_HOME}

# create user, gruoup and home directory
RUN groupadd -g ${PINEAPPLE_GID} ${PINEAPPLE_GROUP} \
	&& useradd -m -u ${PINEAPPLE_UID} -g ${PINEAPPLE_GID} -o -s /bin/bash ${PINEAPPLE_USER} \	
  	&& chown ${PINEAPPLE_UID}:${PINEAPPLE_GID} ${PINEAPPLE_HOME} \	
  	&& chown ${PINEAPPLE_UID}:${PINEAPPLE_GID} ${PINEAPPLE_INSTALL}
	
# Set work directory
WORKDIR ${PINEAPPLE_INSTALL}

# Expose port to host
EXPOSE ${PINEAPPLE_HTTP_PORT}

# Define volume
VOLUME ${PINEAPPLE_HOME}

# Define user
USER ${PINEAPPLE_USER}

# Start pineapple when container is created
ENTRYPOINT ${PINEAPPLE_INSTALL}/runPineapple.sh