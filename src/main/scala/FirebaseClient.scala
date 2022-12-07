import zio._
import zio.http.model.{Header, Headers, Method}
import zio.http.{Body, Client, Response, ZClient}
import zio.http.service.{ChannelFactory, EventLoopGroup}
import zio.kafka.consumer.{Offset, OffsetBatch}

case class FirebaseClient() {

  def request(url: String = "http://localhost:3000/notification",
              method: Method = Method.POST,
              content: String,
              out: Chunk[Offset]): ZIO[Client, Throwable,Chunk[Offset]] = {
      ZClient.request(url, method, Headers.empty, Body.fromString(content))
        .tapError(err => ZIO.logError(s"error ${err.getMessage}"))
        .map(_=>out)

  }




}
object FirebaseClient {
  val live: ULayer[FirebaseClient] = ZLayer.succeed(FirebaseClient())
}