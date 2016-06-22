
# Pull base image
# ---------------
FROM centos:latest

# Maintainer
# ----------
MAINTAINER Allan Thrane Andersen <einheriii@gmail.com>

RUN yum -y install httpd
RUN yum clean all 
COPY mod_proxy.conf /etc/httpd/conf.modules.d/
EXPOSE 80
CMD /usr/sbin/httpd -D FOREGROUND
