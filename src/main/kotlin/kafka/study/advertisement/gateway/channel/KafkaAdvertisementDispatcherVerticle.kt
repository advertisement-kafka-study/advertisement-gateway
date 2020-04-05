package kafka.study.advertisement.gateway.channel

import io.cloudevents.kafka.CloudEventsKafkaProducer
import io.cloudevents.v1.AttributesImpl
import io.cloudevents.v1.CloudEventBuilder
import io.cloudevents.v1.kafka.Marshallers
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.Json
import kafka.study.advertisement.gateway.domain.Opportunity
import kafka.study.advertisement.gateway.infra.SystemData
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.StringSerializer
import java.net.URI
import java.util.*
import java.util.logging.Logger

class KafkaAdvertisementDispatcherVerticle(private val config: SystemData) : AbstractVerticle() {

  private val LOGGER: Logger = Logger.getLogger("AdvertisementDispatcherVerticle")

  override fun start() {

    val props = Properties()
    props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = ByteArraySerializer::class.java
    props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
    props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = config.brokerHost
    props[ProducerConfig.CLIENT_ID_CONFIG] = "advertisement-gateway"

    this.vertx.eventBus().consumer<String>("advertisement-request") {
      val opportunity = Json.decodeValue(it.body(), Opportunity::class.java)
      LOGGER.info("Receiving new opportunity. Category ${opportunity.advReq.category}")
      val ce = CloudEventBuilder.builder<Opportunity>()
        .withSource(URI.create("https://kafka.study/advertisement-gateway"))
        .withDataContentType("application/json")
        .withSubject("adv.request")
        .withId(opportunity.id)
        .withData(opportunity)
        .withType("kafka.study.adv.create").build()
      val producer = CloudEventsKafkaProducer<String, AttributesImpl, Opportunity>(props, Marshallers.binary())
      producer.send(ProducerRecord("advertisement-request",opportunity.id, ce))
    }

  }
}
