package mindthegap
import mindthegap.RunZio.runZioThrow
import zio._

import java.util.concurrent.CompletableFuture
import scala.concurrent.{ExecutionContext, Future}

object ZioInterop {

  object attempt {
    // Task[T] = ZIO[Any, Throwable, T]
    def max(seq: Seq[Int]): Task[Int] = ZIO.attempt(
      // with throw if seq is empty
      seq.max
    )
  }

  object succeed {
    // UIO[T] = ZIO[Any, Nothing, T]
    def max(seq: Seq[Int]): UIO[Option[Int]] = ZIO.succeed(
      seq.sorted.lastOption
    )
  }

  object attemptBlocking {
    def readLine: Task[String] = ZIO.attemptBlocking {
      scala.io.StdIn.readLine()
    }
  }

  object succeedBlocking {
    def readLine: Task[String] = ZIO.succeedBlocking {
      scala.io.StdIn.readLine()
    }
  }

  object FromCompeltableFuture {
    def fetchLines(maxLines: Int): CompletableFuture[Seq[String]] = ???
    // Task[T] = ZIO[Any, Throwable, T]
    def fetchLinesZio(maxLines: Int): Task[Seq[String]] =
      ZIO.fromCompletableFuture(fetchLines(maxLines))
  }

  object FromFuture {
    def fetchLines(maxLines: Int)
                  (implicit ec: ExecutionContext): Future[Seq[String]] = ???
    // Task[T] = ZIO[Any, Throwable, T]
    def fetchLinesZio(maxLines: Int): Task[Seq[String]] =
      ZIO.fromFutureInterrupt(implicit ec => fetchLines(maxLines))
  }

  object FromCafkaFuture {
    def fromKafkaFuture[T](kf: KafkaFuture[T]): Task[T] = {
      ZIO.async(cb => kf.whenComplete((t: T, e: Throwable) => cb(
         if (e == null) ZIO.succeed(t)
         else ZIO.fail(e)))
      )
    }
  }

  object FromKafkaFutureWithCancellation {
    def fromKafkaFuture[T](kf: KafkaFuture[T]): Task[T] = {
      ZIO.asyncInterrupt { cb =>
        kf.whenComplete { (a, e) => cb(
          if (e == null) ZIO.succeed(a)
          else ZIO.fail(e))
        }
        Left(ZIO.attemptBlocking(kf.cancel(true)).ignore)
      }
    }
  }

  object ForkDaemonExample {
    trait Heartbeat { def stop(): Unit }
    def main(): Unit = {
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
  }

  trait KafkaFuture[T] {
    def whenComplete(cb: (T, Throwable) => Unit): Unit
    def cancel(mayInterruptIfRunning: Boolean): Boolean
  }
}
