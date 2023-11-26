package mindthegap

import org.slf4j.{LoggerFactory, MDC}
import zio._

/**
 * demonstrates propagating fibers bound state thread local backed state - MDC
 */
object MdcExample extends ZIOAppDefault {
  def run = {
    ThreadLocalBridge.live { // enable fiber ref to thread bridge
      for {
        zioHandler <- ZioHandler.make
        _ <- zioHandler.handle("msg1", "requestId1") zipPar
             zioHandler.handle("msg2", "requestId2")
      } yield ()
    }
  }
}

trait WithRequestId {
  def withRequestId[R, E, A](requestId: String)(a: => ZIO[R, E, A]): ZIO[R, E, A]
}

object WithRequestId {
  def make = for {
    ref <- ThreadLocalBridge.makeFiberRef(Option.empty[String]) {
      case Some(requestId) => MDC.put("request_id", requestId)
      case None => MDC.remove("request_id")
    }
  } yield new WithRequestId {
    def withRequestId[R, E, A](requestId: String)(a: => ZIO[R, E, A]): ZIO[R, E, A] = {
      ref.locally(Some(requestId))(a)
    }
  }
}

class ZioHandler(withRequestId: WithRequestId) {
  private val log = LoggerFactory.getLogger(getClass)
  def handle(msg: String, requestId: String) = withRequestId.withRequestId(requestId){
    for {
      done1 <- doAThing(msg)
      _ <- ZIO.sleep(100.millis) // yield thread
      done2 <- doAnotherThing(done1)
    } yield {
      log.info(s"Done: $done2")
    }
  }
  private def doAThing(msg: String) = ZIO.attempt(SomeLib.doAThing(msg))
  private def doAnotherThing(msg: String) = ZIO.attempt(SomeLib.doAnotherThing(msg))
}

object ZioHandler {
  def make = WithRequestId.make.map(new ZioHandler(_))
}

object SomeLib {
  private val log = LoggerFactory.getLogger(getClass)
  def doAThing(msg: String): String = {
    log.info(s"Doing a thing: $msg")
    s"doneAThing($msg)"
  }

  def doAnotherThing(msg: String): String = {
    log.info(s"Doing another thing: $msg")
    s"doneAnotherThing($msg)"
  }
}
