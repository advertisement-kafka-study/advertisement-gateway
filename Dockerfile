FROM vertx/vertx3
ENV VERTICLE_NAME kafka.study.advertisement.gateway.AdvertisementGateway
ENV VERTICLE_FILE target/advertisement-gateway-1.0.0-SNAPSHOT.jar
ENV VERTICLE_HOME /usr/verticles
EXPOSE 9999
COPY $VERTICLE_FILE $VERTICLE_HOME/

WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]
