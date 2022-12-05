import zio._
import zhttp.service.ChannelFactory
import zhttp.service.EventLoopGroup
import zhttp.http.{Body, Headers, Method, Response}
import zhttp.service.{Client => ZClient}
import zio.kafka.consumer.Offset

object FirebaseClient {

  def request(url: String = "http://localhost:3000/notification",
              method: Method = Method.POST,
              content: String,
              out: Offset): ZIO[EventLoopGroup with ChannelFactory, String, Offset] = {
    ZClient.request(url, method, Headers(("Connection", "close"),("Keep-Alive" ,"timeout=300")), Body.fromString(content))
      .tap(_ => ZIO.log(s"consumed ${out.offset}"))
      .tapError(err => ZIO.logError(s"error ${err.getMessage}"))
      .mapError(_ => "")
      .map(_ => out)

  }


  val live: ULayer[Any] = ZLayer.succeed(FirebaseClient)
}