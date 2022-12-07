import zio.ZIO
import zio.json.{DecoderOps, DeriveJsonDecoder, DeriveJsonEncoder, EncoderOps, JsonDecoder, JsonEncoder, jsonField}
import zio.kafka.serde.Serde

case class Notification(
    channel: String,
    template: String,
    segments: String,
    subscribe: String,
    name: String,
    @jsonField("promotion_status")
    promotionStatus: String,
    @jsonField("payment_type")
    paymentType: String
)

private object Notification {
  implicit val encoder: JsonEncoder[Notification] = DeriveJsonEncoder.gen[Notification]
  implicit val decoder: JsonDecoder[Notification] = DeriveJsonDecoder.gen[Notification]


  val notificationSerde: Serde[Any, Notification] = Serde.string.inmapM { string =>
    ZIO.fromEither(string.fromJson[Notification].left.map(errorMessage => new RuntimeException(errorMessage)))
  } { notification => ZIO.succeed(notification.toJson)}

}