package kafka.study.advertisement.gateway

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import kafka.study.advertisement.gateway.channel.HTTPAdvertisementDispatcherVerticle
import kafka.study.advertisement.gateway.channel.KafkaAdvertisementDispatcherVerticle
import kafka.study.advertisement.gateway.infra.SystemData
import java.util.logging.Logger

class AdvertisementGateway : AbstractVerticle() {

  private val LOGGER: Logger = Logger.getLogger("AdvertisementGateway")

  override fun start(startPromise: Promise<Void>) {

    val sysData = SystemData(brokerHost = System.getenv("BROKER_HOST"), portOutputType =  System.getenv("PORT_OUTPUT_TYPE"))
    LOGGER.info("""
      ================================================================
      System configuration:

      BROKER_HOST : ${sysData.brokerHost}
      PORT_TYPE :       ${sysData.portOutputType}

      ================================================================
    """.trimIndent())

    vertx.deployVerticle(AdvertisementReceiverVerticle())

    if("kafka".equals(sysData.portOutputType)){
      vertx.deployVerticle(KafkaAdvertisementDispatcherVerticle(sysData))
    }else if("http".equals(sysData.portOutputType)){
      vertx.deployVerticle(HTTPAdvertisementDispatcherVerticle(sysData))
    }else{
      throw IllegalArgumentException("Port not supported")
      System.exit(1)
    }

  }

}
