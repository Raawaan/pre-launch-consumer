import zio._
import zhttp.service.ChannelFactory
import zhttp.service.EventLoopGroup
import zhttp.http.{Body, Headers, Method, Response}
import zhttp.service.{Client => ZClient}
import zio.kafka.consumer.Offset

object FirebaseClient {

  def request(url: String="http://localhost:3000/notification",
              method: Method=Method.POST,
              headers: Headers=Headers.empty,
              content: String,
              out:Offset): ZIO[EventLoopGroup with ChannelFactory, Throwable, Offset] =
    ZClient.request(url, method, headers, Body.fromString(content))
      .map(_=>out)
      .tap(_=>ZIO.log(s"consumed ${out.offset}"))
      .tapError(err=>ZIO.logError(s"error ${err.getMessage}"))


  val live: ULayer[Any] = ZLayer.succeed(FirebaseClient)
}