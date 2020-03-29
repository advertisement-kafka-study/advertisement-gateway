package kafka.study.advertisement.gateway

data class AdvertisementRequest (val category:String,val requirements: AdvRequirements,val callbackData: CallbackData)

data class AdvRequirements(val timeout:Int)

data class CallbackData(val url:String)

data class Opportunity(val id:String,val customerKey:String,val advReq:AdvertisementRequest)

