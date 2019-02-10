
# Pull base image
# ---------------
FROM jenkins/jenkins:2.163-jdk11
# defines JENKINS_HOME environment variable: JENKINS_HOME = /var/jenkins_home

LABEL maintainer="einheriii@gmail.com"

# Copy Jenkins configuration file to Jenkins staging directory - see Jenkins image for more info
COPY config.xml /usr/share/jenkins/ref/config.xml
COPY hudson.tasks.Maven.xml /usr/share/jenkins/ref/hudson.tasks.Maven.xml
COPY settings.xml /usr/share/jenkins/ref/settings.xml
COPY pineapple-build.config.xml /usr/share/jenkins/ref/jobs/pineapple-build/config.xml
COPY pineapple-build-test.config.xml /usr/share/jenkins/ref/jobs/pineapple-build-test/config.xml

#Install plugins
RUN /usr/local/bin/install-plugins.sh \
    blueocean:1.10.2 \
    disk-usage:0.28 
		
CMD /usr/local/bin/jenkins.sh
