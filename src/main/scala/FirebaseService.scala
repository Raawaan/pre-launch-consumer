import zhttp.http.Body
import zhttp.http.Method.POST
import zhttp.service.Client

object FirebaseService {

  private val URL = "http://localhost:3000/notification";
  def send(notification: Notification): Unit = {
    Client.request(url = URL,
                  method = POST,
                  content = Body.fromString(notification.toString))
  }
}
