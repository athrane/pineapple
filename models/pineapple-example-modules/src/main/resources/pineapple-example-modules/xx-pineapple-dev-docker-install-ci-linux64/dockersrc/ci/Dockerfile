
# Pull base image
# ---------------
FROM jenkins
# defines JENKINS_HOME environment variable: JENKINS_HOME = /var/jenkins_home

# Maintainer
# ----------
MAINTAINER Allan Thrane Andersen <einheriii@gmail.com>

# Copy Jenkins configuration file to Jenkins staging directory - see Jenkins image for more info
COPY config.xml /usr/share/jenkins/ref/config.xml
COPY hudson.tasks.Maven.xml /usr/share/jenkins/ref/hudson.tasks.Maven.xml
COPY settings.xml /usr/share/jenkins/ref/settings.xml
COPY pineapple-build.config.xml /usr/share/jenkins/ref/jobs/pineapple-build/config.xml
COPY pineapple-build-test.config.xml /usr/share/jenkins/ref/jobs/pineapple-build-test/config.xml

# Setup plugins
COPY plugins.txt /usr/share/jenkins/ref/
RUN /usr/local/bin/plugins.sh /usr/share/jenkins/ref/plugins.txt

CMD /usr/local/bin/jenkins.sh