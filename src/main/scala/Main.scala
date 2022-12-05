import zhttp.service.{ChannelFactory, EventLoopGroup}
import zio._
import zio.json.EncoderOps
import zio.kafka.consumer.Consumer


object Main extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs, Any, Any] = {
    NotificationConsumer
      .stream
      .map(record => (record.value, record.offset))
      .mapZIO(notificationAndOffset =>
        FirebaseClient.request(content = notificationAndOffset._1.toJson, out = notificationAndOffset._2))
      .aggregateAsync(Consumer.offsetBatches)
      .tap(s => ZIO.log(s"to be committed: partition ${s.offsets}"))
      .mapZIO(_.commit)
      .runDrain
      .provide(FirebaseClient.live ++ ChannelFactory.auto ++ EventLoopGroup.auto())
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