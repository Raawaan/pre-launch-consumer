import zio.ZLayer
import zio.kafka.consumer.Consumer.{AutoOffsetStrategy, OffsetRetrieval}
import zio.kafka.consumer._
import zio.kafka.serde.Serde
import zio.stream.ZStream

object NotificationConsumer {

  private val BOOSTRAP_SERVERS = List("localhost:9093")

  private val consumer: ZLayer[Any, Throwable, Consumer] =
    ZLayer.scoped(
      Consumer.make(
        ConsumerSettings(BOOSTRAP_SERVERS)
          .withGroupId("streaming-kafka")
          .withOffsetRetrieval(OffsetRetrieval.Auto(AutoOffsetStrategy.Earliest))
      )
    )

  private def consume(): ZStream[Consumer, Throwable, CommittableRecord[String,String]] = {
    val KAFKA_TOPIC = "subscriber-msg-million"
    Consumer
      .subscribeAnd(Subscription.topics(KAFKA_TOPIC))
      .plainStream(Serde.string, Serde.string)
  }

  val stream: ZStream[Any, Throwable, CommittableRecord[String,String]] = consume().provideLayer(consumer)

}
