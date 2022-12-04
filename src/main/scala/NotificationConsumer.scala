import com.fasterxml.jackson.annotation.JsonProperty
import zio.{ZIO, ZLayer}
import zio.json._
import zio.kafka.consumer.Consumer.{AutoOffsetStrategy, OffsetRetrieval}
import zio.kafka.consumer._
import zio.kafka.serde.Serde
import zio.stream.ZStream

object NotificationConsumer {

  private val BOOSTRAP_SERVERS = List("localhost:9092")

  private val consumer: ZLayer[Any, Throwable, Consumer] =
    ZLayer.scoped(
      Consumer.make(
        ConsumerSettings(BOOSTRAP_SERVERS)
          .withGroupId("streaming-k")
          .withOffsetRetrieval(OffsetRetrieval.Auto(AutoOffsetStrategy.Earliest))
      )
    )

  private def consume(): ZStream[Consumer, Throwable, CommittableRecord[String, Notification]] = {
    val KAFKA_TOPIC = "notification"
    Consumer
      .subscribeAnd(Subscription.topics(KAFKA_TOPIC))
      .plainStream(Serde.string, Notification.notificationSerde)
  }

  val stream: ZStream[Any, Throwable, CommittableRecord[String, Notification]] =
    consume().provideLayer(consumer)

}

case class Notification(
   channel: String,
   template: String,
   segments: String,
   subscribe: String,
   name: String,
   @JsonProperty("promotion_status")
   promotionStatus: String,
   @JsonProperty("payment_type")
   paymentType: String
)

private object Notification {
  implicit val encoder: JsonEncoder[Notification] = DeriveJsonEncoder.gen[Notification]
  implicit val decoder: JsonDecoder[Notification] = DeriveJsonDecoder.gen[Notification]


  val notificationSerde: Serde[Any, Notification] = Serde.string.inmapM { string =>
    ZIO.fromEither(string.fromJson[Notification].left.map(errorMessage => new RuntimeException(errorMessage)))
    } { notification => ZIO.succeed(notification.toJson)
  }

}


