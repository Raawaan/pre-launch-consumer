import zio._
import zio.http.model.{Header, Headers, Method}
import zio.http.{Body, Client, Response, ZClient}
import zio.http.service.{ChannelFactory, EventLoopGroup}
import zio.kafka.consumer.Offset

case class FirebaseClient() {

  def request(url: String = "http://localhost:3000/notification",
              method: Method = Method.POST,
              content: String,
              out: Offset): ZIO[Client, Throwable, Response] = {
      ZClient.request(url, method, Headers.empty, Body.fromString(content))
        .tap(_ => ZIO.log(s"consumed ${out.offset}"))
        .tapError(err => ZIO.logError(s"error ${err.getMessage}"))

  }




}
object FirebaseClient {
  val live: ULayer[FirebaseClient] = ZLayer.succeed(FirebaseClient())
}