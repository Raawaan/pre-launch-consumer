
import zio.kafka.consumer._
import zio.kafka.serde.Serde
import zio.stream.ZStream

object NotificationConsumer {

  private def consume(): ZStream[Consumer, Throwable, CommittableRecord[String, Notification]] = {
    val KAFKA_TOPIC = "subscriber-msg-ten-million1"
    Consumer
      .subscribeAnd(Subscription.topics(KAFKA_TOPIC))
      .plainStream(Serde.string, Notification.notificationSerde)
  }

  val stream: ZStream[Any, Throwable, CommittableRecord[String, Notification]] =
    consume().provideLayer(ConsumerConfig.live)

}




