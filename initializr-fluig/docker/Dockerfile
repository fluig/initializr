#
# Copyright 2018, TOTVS S.A.
# All rights reserved.
#

# base image
FROM docker.fluig.com/fluig/oracle-java8

# add our user and group
RUN groupadd -r fluig && useradd -r -g fluig fluig

# variables and arguments for fluig
ENV FLUIG_HOME "/opt/fluig/"
ARG FLUIG_VERSION
# keep version numbers in ENV to make it easier to maintain
ENV FLUIG_VERSION ${FLUIG_VERSION:-latest}

# define workdir
WORKDIR ${FLUIG_HOME}

# create docker entrypoint file with root
USER root

# copy artifacts to docker image
ADD --chown=fluig:fluig ./fluig-initializr.jar ${FLUIG_HOME}

# copy entrypoint
COPY docker-entrypoint.sh /usr/local/bin/

# remove any prior docker-entrypoint.sh if exist
# and add backwards compatibility
RUN set -ex && \
   rm -f /docker-entrypoint.sh && \
   # backwards compatibility
   ln -s /usr/local/bin/docker-entrypoint.sh /docker-entrypoint.sh

# expose fluig ports
EXPOSE 8080

# don't let fluig run as root
USER fluig

ENTRYPOINT ["docker-entrypoint.sh"]

CMD ["java", "-Xms512m", "-Xmx1024m", "-jar", "fluig-initializr.jar"]
