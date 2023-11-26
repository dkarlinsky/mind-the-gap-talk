package mindthegap

import mindthegap.RunZio.runZioThrow
import zio._

object ForkDaemonExample extends scala.App {
  trait Heartbeat {
    def stop(): Unit
  }

  val heartbeat: Heartbeat = runZioThrow {
    for {
      fiber <- ZIO.debug("heartbeat")
        .repeat(Schedule.spaced(10.seconds))
        .forkDaemon
    } yield (() => runZioThrow(fiber.interrupt)): Heartbeat
  }
  Thread.sleep(1000)
  heartbeat.stop()
}
