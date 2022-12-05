import zio._
import zio.http.model.{Header, Headers, Method}
import zio.http.{Body, ZClient}
import zio.http.service.{ChannelFactory, EventLoopGroup}
import zio.kafka.consumer.Offset

object FirebaseClient {

  def request(url: String = "http://localhost:3000/notification",
              method: Method = Method.POST,
              content: String,
              out: Offset): ZIO[http.Client, Throwable, Offset] = {
    ZClient.request(url, method,  Headers.empty, Body.fromString(content))
      .tap(_ => ZIO.log(s"consumed ${out.offset}"))
      .tapError(err => ZIO.logError(s"error ${err.getMessage}"))
      .map(_ => out)

  }

  val live: ULayer[Any] = ZLayer.succeed(FirebaseClient)
}