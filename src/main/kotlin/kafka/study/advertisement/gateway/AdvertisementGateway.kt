package kafka.study.advertisement.gateway

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import kafka.study.advertisement.gateway.channel.input.CloudEventsAdvertisementReceiverVerticle
import kafka.study.advertisement.gateway.channel.input.HTTPAdvertisementReceiverVerticle
import kafka.study.advertisement.gateway.channel.output.HTTPAdvertisementDispatcherVerticle
import kafka.study.advertisement.gateway.channel.output.KafkaAdvertisementDispatcherVerticle
import kafka.study.advertisement.gateway.infra.SystemData
import java.util.logging.Logger

class AdvertisementGateway : AbstractVerticle() {

  private val LOGGER: Logger = Logger.getLogger("AdvertisementGateway")

  override fun start(startPromise: Promise<Void>) {
    val sysData = SystemData(brokerHost = System.getenv("BROKER_HOST"), outputPortType = System.getenv("PORT_OUTPUT_TYPE"), inputPortType = System.getenv("PORT_INPUT_TYPE"))
    LOGGER.info("""
      ================================================================
      System configuration:

      BROKER_HOST :        ${sysData.brokerHost}
      OUTPUT_PORT_TYPE :   ${sysData.outputPortType}
      INPUT_PORT_TYPE :    ${sysData.inputPortType}

      ================================================================
    """.trimIndent())

    deployOutputVerticles(this.vertx, sysData)
    deployInputVerticles(this.vertx, sysData)

  }

}

private fun deployInputVerticles(vertx: Vertx, sysConfig: SystemData) {
  if ("cloudevents".equals(sysConfig.inputPortType)) {
    vertx.deployVerticle(CloudEventsAdvertisementReceiverVerticle())
  } else if ("http".equals(sysConfig.inputPortType)) {
    vertx.deployVerticle(HTTPAdvertisementReceiverVerticle())
  } else {
    throw IllegalArgumentException("Input Port not supported")
    System.exit(1)
  }
}

private fun deployOutputVerticles(vertx: Vertx, sysConfig: SystemData) {
  if ("kafka".equals(sysConfig.outputPortType)) {
    vertx.deployVerticle(KafkaAdvertisementDispatcherVerticle(sysConfig))
  } else if ("http".equals(sysConfig.outputPortType)) {
    vertx.deployVerticle(HTTPAdvertisementDispatcherVerticle(sysConfig))
  } else {
    throw IllegalArgumentException("Output Port not supported")
    System.exit(1)
  }
}

