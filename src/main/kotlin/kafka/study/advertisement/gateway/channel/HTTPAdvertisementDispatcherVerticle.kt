package kafka.study.advertisement.gateway.channel

import io.cloudevents.CloudEvent
import io.cloudevents.extensions.DistributedTracingExtension
import io.cloudevents.extensions.ExtensionFormat
import io.cloudevents.http.vertx.VertxCloudEvents
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpClientRequest
import io.vertx.core.json.Json
import kafka.study.advertisement.gateway.domain.Opportunity
import kafka.study.advertisement.gateway.infra.SystemData
import java.net.URI
import java.util.*
import java.util.logging.Logger

class HTTPAdvertisementDispatcherVerticle(private val config: SystemData) : AbstractVerticle() {

  private val LOGGER: Logger = Logger.getLogger("AdvertisementDispatcherVerticle")

  override fun start() {

    this.vertx.eventBus().consumer<String>("advertisement-request") {
      val opportunity = Json.decodeValue(it.body(), Opportunity::class.java)
      LOGGER.info("Receiving new opportunity. Category ${opportunity.advReq.category}")

      val request: HttpClientRequest = vertx.createHttpClient().post(8080, config.brokerHost, "/")

      val handler = request.handler({ resp -> })

      val dt = DistributedTracingExtension()
      dt.traceparent = UUID.randomUUID().toString()
      dt.tracestate = UUID.randomUUID().toString()

      val tracing: ExtensionFormat = DistributedTracingExtension.Format(dt)

      val cloudEvent: CloudEvent<io.cloudevents.v02.AttributesImpl, String> = io.cloudevents.v02.CloudEventBuilder.builder<String>()
        .withSource(URI.create("https://kafka.study/advertisement-gateway"))
        .withContenttype("application/json")
        .withId(opportunity.advReq.category)
        .withData(Json.encode(opportunity))
        .withExtension(tracing)
        .withType("kafka.study.adv.request").build()

      VertxCloudEvents.create().writeToHttpClientRequest(cloudEvent, request)
      request.end()

    }

  }
}
