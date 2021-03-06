package kafka.study.advertisement.gateway.channel.input

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.common.hash.Hashing
import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import kafka.study.advertisement.gateway.domain.AdvertisementRequest
import kafka.study.advertisement.gateway.domain.Opportunity
import java.nio.charset.StandardCharsets

class HTTPAdvertisementReceiverVerticle : AbstractVerticle() {

  override fun start() {
    DatabindCodec.mapper().registerModule(KotlinModule())
    var router = Router.router(vertx)
    router.route().handler(BodyHandler.create())
    router.post("/api/advertisements").handler { receiveAdvRequest(it, this.vertx) }
    vertx.createHttpServer().requestHandler(router).listen(9999)
  }
}

fun receiveAdvRequest(routingContext: RoutingContext,vertx: Vertx) {
  val customerKey = routingContext.request().getHeader("X-Customer-Key")
  if (customerKey == null) {
    routingContext.response().error(422)
  } else {
    val advReq = Json.decodeValue(routingContext.bodyAsJson.toBuffer(), AdvertisementRequest::class.java)
    val opportunityId = advReq.opportunityId()
    val opportunity = Opportunity(id = opportunityId, customerKey = customerKey, advReq = advReq)
    vertx.eventBus().send("advertisement-request",Json.encode(opportunity))
    routingContext.response().jsonCreated(opportunity)
  }
}

fun HttpServerResponse.error(newStatusCode: Int) = this.setStatusCode(newStatusCode).end()

fun HttpServerResponse.jsonCreated(opportunity: Opportunity) = this.putHeader("Content-Type", "application/json").putHeader("Location", "/opportunities/${opportunity.id}").putHeader("X-opportunity-id",opportunity.id).setStatusCode(201).end(Json.encode(opportunity))

