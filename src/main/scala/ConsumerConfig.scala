import zio.{Queue, ZLayer}
import zio.kafka.consumer.Consumer.{AutoOffsetStrategy, OffsetRetrieval}
import zio.kafka.consumer.diagnostics.{DiagnosticEvent, Diagnostics}
import zio.kafka.consumer.{Consumer, ConsumerSettings}

object ConsumerConfig {

  private val BOOSTRAP_SERVERS = List("localhost:9093")

  val live: ZLayer[Any, Throwable, Consumer] =
    ZLayer.scoped(
      Consumer.make(
        ConsumerSettings(BOOSTRAP_SERVERS)
          .withGroupId("streaming-kkk1133311444mnmnnn1nnn")
          .withOffsetRetrieval(OffsetRetrieval.Auto(AutoOffsetStrategy.Earliest))
          .withPerPartitionChunkPrefetch(0)
      )
    )
  //36:25
  //39:49
  //prefetch 10000
  //43:55
  //46:36
  //prefetch 100000
  //47:10
  //50:07
  //mapZIOParUnordered 1000
  //54:08
  //56:12
  //mapZIOPar 1000
  //57:21
  //59:14

  //00:47
  //02:30

  //45:02
  //46:43


  //10M
  //12:28
}