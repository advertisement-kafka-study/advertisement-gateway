package kafka.study.advertisement.gateway.channel.input

import io.cloudevents.http.vertx.VertxCloudEvents
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.Json
import kafka.study.advertisement.gateway.domain.AdvertisementRequest
import kafka.study.advertisement.gateway.domain.Opportunity

class CloudEventsAdvertisementReceiverVerticle : AbstractVerticle() {

  override fun start() {
    vertx.createHttpServer().requestHandler {
      VertxCloudEvents.create().readFromRequest(it) {
        val ce = it.result()
        if (!ce.data.isEmpty) {
          val advRequest = Json.decodeValue(ce.data.get(), AdvertisementRequest::class.java)
          val opportunityId = advRequest.opportunityId()
          val opportunity = Opportunity(id = opportunityId, customerKey = ce.attributes.source.toString(), advReq = advRequest)
          vertx.eventBus().send("advertisement-request",Json.encode(opportunity))
        }
      }
    }.listen(9999)

  }
}
