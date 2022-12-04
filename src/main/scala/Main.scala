import zhttp.http.Body
import zhttp.http.Method.POST
import zhttp.service.{ChannelFactory, Client, EventLoopGroup}
import zio._
import zio.json.{DecoderOps, EncoderOps}
import zio.kafka.consumer.{CommittableRecord, Consumer}
import zio.kafka.serde.Serde
import zio.stream.ZStream

object Main extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    NotificationConsumer
      .stream
      .broadcast(2, 100)
      .flatMap(streams => {
        (for {
          commit <- streams(0)
            .map(_.offset)
            .aggregateAsync(Consumer.offsetBatches)
            .tap(s => ZIO.log(s"to be committed: partition ${s.offsets}"))
            .mapZIO(_.commit)
            .tap(_ => ZIO.log("commit"))
          response <- streams(1)
            .map(_.value)
            .groupedWithin(100000, 1.second)
            .tap(p => ZIO.log(s"chunk to be send: ${p.size}"))
            .map(n => n.toJson)
            .mapZIO(notification => FirebaseService.send(notification))
            .tap(p => ZIO.log(s"chunk status: ${p.status.toString}"))
        } yield (commit, response))
          .runDrain
      }
      ).provide(EventLoopGroup.auto(1000), ChannelFactory.auto, Scope.default)
  }
}