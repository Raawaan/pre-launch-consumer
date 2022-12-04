import FirebaseService.URL
import zhttp.http.Body
import zhttp.http.Method.POST
import zhttp.service.{ChannelFactory, Client, EventLoopGroup}
import zio._
import zio.json.{DecoderOps, EncoderOps}
import zio.kafka.consumer.Consumer
import zio.kafka.serde.Serde
object Main extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {

       NotificationConsumer
          .stream
          .tap(r => Console.printLine(r.value))
          .groupedWithin(100000, 1.minute)
          .tap(chunk => ZIO.log(chunk.size.toString))
          .flattenChunks
          .map(_.offset)
          .aggregateAsync(Consumer.offsetBatches)
          .mapZIO(_.commit)
          .runDrain
          .provide(EventLoopGroup.auto(10),ChannelFactory.auto)

  }
}