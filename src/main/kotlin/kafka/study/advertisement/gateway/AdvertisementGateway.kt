package kafka.study.advertisement.gateway

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise

class AdvertisementGateway : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    vertx.deployVerticle(AdvertisementReceiverVerticle())
    vertx.deployVerticle(AdvertisementDispatcherVerticle())
  }

}
