# ---- Build ----
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package -DskipTests -q

# ---- Runtime ----
FROM quay.io/wildfly/wildfly:33.0.2.Final-jdk17
USER root

# Download MySQL Connector/J
ARG MYSQL_VERSION=8.4.0
RUN curl -fsSL \
    "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/${MYSQL_VERSION}/mysql-connector-j-${MYSQL_VERSION}.jar" \
    -o /tmp/mysql-connector.jar

# Install as a WildFly module
RUN mkdir -p $JBOSS_HOME/modules/com/mysql/main
COPY docker/mysql-module.xml $JBOSS_HOME/modules/com/mysql/main/module.xml
RUN mv /tmp/mysql-connector.jar $JBOSS_HOME/modules/com/mysql/main/mysql-connector.jar

# Register the driver and datasource via embedded CLI
COPY docker/configure.cli /tmp/configure.cli
RUN $JBOSS_HOME/bin/jboss-cli.sh --file=/tmp/configure.cli && \
    rm /tmp/configure.cli && \
    rm -rf $JBOSS_HOME/standalone/configuration/standalone_xml_history/current

# Deploy application
COPY --from=builder /build/target/budget-tracker-api.war $JBOSS_HOME/standalone/deployments/

RUN chown -R jboss:jboss $JBOSS_HOME

USER jboss
EXPOSE 8080

# Append memory limits to WildFly's default JAVA_OPTS without overwriting the
# --add-opens flags that WildFly 33 needs on Java 17. Last -Xmx/-Xms wins,
# so these override the defaults while keeping everything else intact.
ENV JAVA_OPTS_APPEND="-Xms64m -Xmx384m -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=192m -XX:ReservedCodeCacheSize=64m"

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "127.0.0.1", "-c", "standalone-microprofile.xml"]
