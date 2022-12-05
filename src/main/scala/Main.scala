import zio._
import zio.http.netty.NettyRuntime
import zio.http.netty.client.ConnectionPool
import zio.http.{ClientConfig, ZClient}
import zio.json.EncoderOps
import zio.kafka.consumer.Consumer


object Main extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs, Any, Any] = {
    NotificationConsumer
      .stream
      .map(record => (record.value, record.offset))
      .mapZIOPar(1000)(notificationAndOffset =>
        FirebaseClient.request(content = notificationAndOffset._1.toJson, out = notificationAndOffset._2)
      )
      .aggregateAsync(Consumer.offsetBatches)
      .tap(s => ZIO.log(s"to be committed: partition ${s.offsets}"))
      .mapZIO(_.commit)
      .runDrain
      .provide(FirebaseClient.live,ZClient.live,ClientConfig.default,ConnectionPool.dynamic(1000,10000000,10.second),Scope.default )
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