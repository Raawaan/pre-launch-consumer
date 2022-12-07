import zio._
import zio.http.model.{Headers, Method}
import zio.http.{Body, Client, ZClient}
import zio.kafka.consumer.Offset

case class FirebaseClient() {

  def request(url: String = "http://localhost:3000/notification",
              method: Method = Method.POST,
              content: String,
              out: Offset): ZIO[Client, Throwable,Offset] = {
      ZClient.request(url, method, Headers.empty, Body.fromString(content))
        .tapError(err => ZIO.logError(s"error ${err.getMessage}"))
        .map(_=>out)

  }




}
object FirebaseClient {
  val live: ULayer[FirebaseClient] = ZLayer.succeed(FirebaseClient())
}