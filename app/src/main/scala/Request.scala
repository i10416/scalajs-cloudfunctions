package $packageName
import scala.scalajs._
import scala.scalajs.js.annotation._
import scala.scalajs.js.JSON
import io.circe.Decoder
import org.http4s.Uri
/**
 * Interface for cloud functions request type
*/
@js.native
trait Request extends js.Object {
  val method: String = js.native
  @JSName("url")
  val rawUrl: String = js.native
  @JSName("query")
  val unsafeQuery: js.Any = js.native
  val connection: Connection = js.native
  @JSName("body")
  val unsafeBody: js.Any = js.native
}
extension (r: Request)
  def show: String =
    s"Request(method:${r.method},url:${r.rawUrl},query:${r.query},conn:${r.connection})"
  def url: Uri = Uri.unsafeFromString(r.rawUrl)
  def body: String = JSON.stringify(r.unsafeBody)
  def json: Either[io.circe.ParsingFailure, io.circe.Json] =
    io.circe.parser.parse(body)
  def query: String = JSON.stringify(r.unsafeQuery)
  def queryAs[T: Decoder]: Either[io.circe.Error, T] =
    io.circe.parser.parse(query).flatMap(_.as[T])
  def bodyAs[T: Decoder]: Either[io.circe.Error, T] =
    io.circe.parser.parse(body).flatMap(_.as[T])

@js.native
trait Connection extends js.Object {
  val remoteAddress: String = js.native
}

/**
 * Interface for cloud functions response type
*/
@js.native
trait Response extends js.Object {
  def set(headerKey: String, value: String): Response = js.native
  def status(statusCode: Int): Response = js.native
  def send(content: String): Unit = js.native
  @JSName("send")
  def send(content: js.Object): Unit = js.native
}
