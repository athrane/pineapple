
# Pull base image
# ---------------
FROM centos:latest

# Maintainer
# ----------
MAINTAINER Allan Thrane Andersen <einheriii@gmail.com>

RUN yum -y install httpd
RUN echo "Hello HTTPD" > /var/www/html/index.html
EXPOSE 80
CMD  /usr/sbin/httpd -D FOREGROUND