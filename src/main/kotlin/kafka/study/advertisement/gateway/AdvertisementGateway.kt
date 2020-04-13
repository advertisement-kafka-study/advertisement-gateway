package kafka.study.advertisement.gateway

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx

class AdvertisementGateway : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    vertx.deployVerticle(AdvertisementReceiverVerticle())
    vertx.deployVerticle(AdvertisementDispatcherVerticle())
  }

  companion object {

    @JvmStatic
    fun main(args: Array<String>) {
      val vertx = Vertx.vertx()
      vertx.deployVerticle(AdvertisementGateway::class.java.name)
    }

  }

}
