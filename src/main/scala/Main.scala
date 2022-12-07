import zio._
import zio.http.netty.NettyRuntime
import zio.http.netty.client.ConnectionPool
import zio.http.{Client, ClientConfig, ZClient}
import zio.json.EncoderOps
import zio.kafka.consumer.EmptyOffsetBatch.commit
import zio.kafka.consumer.{Consumer, Offset, OffsetBatch}
import zio.prelude.{AssociativeBothCovariantOps, AssociativeBothOps}


object Main extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs, Any, Any] = {
    NotificationConsumer
      .stream
      .groupedWithin(10, 1.second)
      .mapZIOPar(100)(chunk => {
        val not = chunk.map(_.value).toJson
        val offsets = chunk.map(_.offset)
        ZIO.serviceWithZIO[FirebaseClient](_.request(content = not,out=offsets))
      }
      ).flattenChunks
      .aggregateAsync(Consumer.offsetBatches)
      .tap(s => ZIO.log(s"committed ${s.offsets} "))
      .mapZIO(_.commit)
      .runDrain
      .provide(ZClient.live, ClientConfig.default, ConnectionPool.dynamic(100, 10000, 1.second), Scope.default, FirebaseClient.live)
  }
}

//NotificationConsumer
//  .stream
//  .map(record => (record.value, record.offset))
//  .tap(s => ZIO.log(s"to be committed: and sent "))
//  .mapZIO(notificationAndOffset => {
//
//    val notifications = notificationAndOffset._1
//      .toJson
//
//    val offsetBatch = notificationAndOffset._2
//
//    FirebaseService
//      .send(notifications)
//      .tap(response => ZIO.log(s"sent response: ${response.status}")) *>
//      offsetBatch
//        .commit
//        .tap(_ => ZIO.log("committed"))
//  })
//  .runDrain
//  .provide(EventLoopGroup.auto(1000), ChannelFactory.auto, Scope.default)