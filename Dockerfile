FROM java:openjdk-8-alpine

WORKDIR /usr/src/app
COPY target/*.jar ./app.jar

RUN	chown -R ${SERVICE_USER}:${SERVICE_GROUP} ./app.jar

USER ${SERVICE_USER}

ARG BUILD_DATE
ARG BUILD_VERSION
ARG COMMIT

LABEL org.label-schema.vendor="Dynatrace" \
  org.label-schema.build-date="${BUILD_DATE}" \
  org.label-schema.version="${BUILD_VERSION}" \
  org.label-schema.name="Socks Shop: Cart" \
  org.label-schema.description="REST API for Cart service" \
  org.label-schema.url="https://github.com/dynatrace-sockshop/carts" \
  org.label-schema.vcs-url="github.com:microservices-demo/carts.git" \
  org.label-schema.vcs-ref="${COMMIT}" \
  org.label-schema.schema-version="1.0"

ENTRYPOINT ["java","-jar","./app.jar", "--port=8080"]
