import zhttp.http.{Body, Response}
import zhttp.http.Method.POST
import zhttp.service.{ChannelFactory, Client, EventLoopGroup}
import zio.{Chunk, ZIO}

object FirebaseService {

  private val URL = "http://localhost:3000/notification";
  def send(notification: String): ZIO[EventLoopGroup with ChannelFactory, Throwable, Response] = {
    Client.request(url = URL,
                  method = POST,
                  content = Body.fromString(notification))
  }
}
