FROM quay.io/wildfly/wildfly:latest-jdk17

USER root
RUN microdnf install -y which && microdnf clean all

# create modules directory for postgresql
RUN mkdir -p $JBOSS_HOME/modules/system/layers/base/org/postgresql/main

COPY docker/wildfly/module.xml $JBOSS_HOME/modules/system/layers/base/org/postgresql/main/module.xml

COPY docker/wildfly/postgresql-42.7.10.jar $JBOSS_HOME/modules/system/layers/base/org/postgresql/main/postgresql-42.7.10.jar
COPY docker/wildfly/start-wildfly.sh /opt/jboss/wildfly/start-wildfly.sh

RUN chmod +x /opt/jboss/wildfly/start-wildfly.sh && \
    chown -R jboss:0 $JBOSS_HOME/modules && \
    chown -R jboss:0 $JBOSS_HOME/standalone && \
    chown jboss:0 /opt/jboss/wildfly/start-wildfly.sh

USER jboss

CMD ["/opt/jboss/wildfly/start-wildfly.sh"]
