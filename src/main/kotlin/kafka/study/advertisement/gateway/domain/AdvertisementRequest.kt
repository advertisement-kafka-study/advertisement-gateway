package kafka.study.advertisement.gateway.domain

import com.google.common.hash.Hashing
import io.vertx.core.json.Json
import java.nio.charset.StandardCharsets

data class AdvertisementRequest(val category: String, val requirements: AdvRequirements, val callbackData: CallbackData) {

  fun opportunityId(): String = Hashing.sha256().hashString(Json.encode(this), StandardCharsets.UTF_8).toString();

}

data class AdvRequirements(val timeout: Int)

data class CallbackData(val url: String)

data class Opportunity(val id: String, val customerKey: String, val advReq: AdvertisementRequest)
