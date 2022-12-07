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
          .withGroupId("streaming-kkkdwww111d3wwwww3wss11311444enmnmnn91n1nn1nn")
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


  /////////10M
  //04:04
  //06:29

  //08:48
  //11:09

  //agg 100 group
  //15:18
  //16:34

  //18:37
  //19:57

  //21:00
  //22:17

  //35:58
  //37:22

  //39:38
  //40:52
  //agg 10 group
  //23:17
  //27:32

  //28:26
  //33:06


  //agg 1 group
  //43:56
 }