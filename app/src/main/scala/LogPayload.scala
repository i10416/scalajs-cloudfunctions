package $packageName
import io.circe.{Decoder, Encoder}
import io.circe.Codec.AsObject

object Log:
  import cats.effect.IO
  import io.circe.syntax.*
  import scala.language.implicitConversions
  def debug(msg: String) = IO.println(LogPayload("DEBUG", msg).asJson.noSpaces)
  def info(msg: String) = IO.println(LogPayload("INFO", msg).asJson.noSpaces)
  def warn(msg: String) = IO.println(LogPayload("WARN", msg).asJson.noSpaces)
  def err(msg: String) = IO.println(LogPayload("ERROR", msg).asJson.noSpaces)

case class Operation(
    id: String,
    producer: String,
    first: Boolean,
    last: Boolean
)
object Operation:
  given Encoder[Operation] = io.circe.generic.semiauto.deriveEncoder[Operation]

/** stack driver logging payload
  *
  * @param severity
  *   Severity.(DEBUG|INFO|WARN|ERROR) or String "DEBUG"| "INFO" | "WARN" |
  *   "ERROR"
  * @param message
  *   log message
  * @param operation
  *   The value of this field is used by the Logs Explorer to group related log
  *   entries. example: {"id": "fetch_data","producer":
  *   "com/example/app","first": true}
  * @see
  *   https://cloud.google.com/logging/docs/structured-logging
  */
case class LogPayload(
    severity: Severity.Level,
    message: String,
    operation: Option[Operation] = None
    // todo: detect source location using macro
    // sourceLocation(file,line,function): string x string x string
)

object LogPayload:
  given Encoder[LogPayload] =
    io.circe.generic.semiauto.deriveEncoder[LogPayload]

/** structured logging severity enums and their typeclass instances
  *
  * {{{
  *
  * val payload = LogPayload(Severity.WARN,"message....") // OK. compile
  *
  * val payload = LogPayload("WARN","message....") // OK. compile
  *
  * val payload = LogPayload("WARn","message....") // NG. compile error
  *
  * }}}
  */
object Severity:
  val DEBUG: "DEBUG" = "DEBUG"
  val INFO: "INFO" = "INFO"
  val WARN: "WARN" = "WARN"
  val ERROR: "ERROR" = "ERROR"
  opaque type Level = "DEBUG" | "INFO" | "WARN" | "ERROR"
  given Decoder[Level] with
    import io.circe.{HCursor, DecodingFailure}
    def apply(a: HCursor): Decoder.Result[Level] =
      a.field("severity").as[String] match
        case Right("DEBUG") =>
          Right("DEBUG")
        case Right("INFO") =>
          Right("INFO")
        case Right("WARN") =>
          Right("WARN")
        case Right("ERROR") =>
          Right("ERROR")
        case Right(value) =>
          Left(DecodingFailure("Invalid severity value: $value", a.history))
        case Left(e) => Left(e)
  given Encoder[Level] with
    import io.circe.Json
    def apply(level: Level): Json = Json.fromString(level)
  given debugInstance: Conversion["DEBUG", Level] =
    new Conversion["DEBUG", Level]:
      def apply(s: "DEBUG"): Level = s
  given infoInstance: Conversion["INFO", Level] = new Conversion["INFO", Level]:
    def apply(s: "INFO"): Level = s
  given warnInstance: Conversion["WARN", Level] = new Conversion["WARN", Level]:
    def apply(s: "WARN"): Level = s
  given errInstance: Conversion["ERROR", Level] =
    new Conversion["ERROR", Level]:
      def apply(s: "ERROR"): Level = s
