/** This example was created with Cascade in Windsurf (the world's first agentic IDE) under the guidance of Oleg
  * Strygun.
  *
  * It demonstrates a simple HTTP/2 server using ZIO Quartz H2 with a basic route that handles query parameters and
  * accesses the ZIO environment.
  */

import zio.{ZIO, Task, Chunk, Promise, ExitCode, ZIOApp, ZLayer}
import zio.ZIOAppDefault
import zio.stream.{ZStream, ZPipeline, ZSink}
import io.quartz.QuartzH2Server
import io.quartz.http2._
import io.quartz.http2.model.{Headers, Method, ContentType, Request, Response}
import io.quartz.http2.model.Method._
import io.quartz.http2.routes.Routes
import io.quartz.http2.model.Cookie
import io.quartz.http2.routes.HttpRouteIO
import io.quartz.http2.routes.WebFilter

import zio.logging.LogFormat
import zio.logging.backend.SLF4J
import zio.LogLevel

import io.quartz.util.MultiPart
import io.quartz.http2.model.StatusCode
import java.io.File

// Define query parameter for name
object name extends QueryParam("name")

object Run extends ZIOAppDefault {

  override val bootstrap =
    zio.Runtime.removeDefaultLoggers ++ SLF4J.slf4j ++ zio.Runtime.enableWorkStealing

  // The WebFilter uses Either.cond to conditionally filter requests
  // Either.cond takes three parameters:
  // 1. A condition (Boolean) - if true, returns Right(value), if false, returns Left(error)
  // 2. The value to return in the Right case (the request passes through)
  // 3. The value to return in the Left case (the request is rejected with this response)
  //
  // In this example, the condition is always true, so all requests pass through
  // If you change the condition to something like r.uri.getPath().endsWith(".html"),
  // then only requests ending with .html would pass through, and all others would be
  // rejected with the Forbidden response
  val filter: WebFilter[Any] = (r: Request) =>
    ZIO.succeed(
      Either.cond(true, r, Response.Error(StatusCode.Forbidden).asText("Access denied to: " + r.uri.getPath()))
    )

  val R: HttpRouteIO[String] = {

    // GET / route that returns just Response.Ok() without any text
    case GET -> Root => ZIO.succeed(Response.Ok())

    // GET /hello route with name parameter
    // If the parameter is empty, it will display "Hello World!"
    // Otherwise, it will display "Hello, <name>"
    case GET -> Root / "hello" :? name(userName) =>
      for {
        envText <- ZIO.environmentWith[String](env => env.get)
        responseText = if (userName.trim.isEmpty) s"$envText World!" else s"$envText, $userName"
      } yield Response.Ok().asText(responseText)

    // GET /doc route to serve static files from web_root directory
    case GET -> "doc" /: remainingPath =>
      val FOLDER_PATH = "web_root/"
      val FILE = if (remainingPath.toString.isEmpty) "index.html" else remainingPath.toString
      val BLOCK_SIZE = 16000

      for {
        jpath <- ZIO.attempt(new File(FOLDER_PATH + FILE))
        present <- ZIO.attempt(jpath.exists())
        response <-
          if (present) {
            ZIO.succeed(
              Response
                .Ok()
                .asStream(ZStream.fromFile(jpath, BLOCK_SIZE))
                .contentType(ContentType.contentTypeFromFileName(FILE))
            )
          } else {
            ZIO.succeed(Response.Error(StatusCode.NotFound).asText(s"File not found: $FILE"))
          }
      } yield response
  }

  def run = {
    val env = ZLayer.fromZIO(ZIO.succeed("Hello"))
    (for {
      ctx <- QuartzH2Server.buildSSLContext("TLS", "keystore.jks", "password")

      exitCode <- new QuartzH2Server("localhost", 8443, 16000, ctx).startIO(R, filter, sync = false)

      // For Linux IO-Uring support, comment out the line above and uncomment the line below
      // exitCode <- new QuartzH2Server("localhost", 8443, 16000, ctx).startIO_linuxOnly(1, R, filter)
    } yield (exitCode)).provideSomeLayer(env)
  }
}
