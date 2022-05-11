package $packageName
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js.{Function2 => JSFunction2}

object Program:
  @JSExportTopLevel("$functionName")
  def run: JSFunction2[Request,Response,Any] = (req:Request,res:Response) =>
    res.status(200).send("OK")
