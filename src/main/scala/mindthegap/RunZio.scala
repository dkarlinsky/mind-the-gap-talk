package mindthegap

import zio.{Exit, Runtime, Unsafe, ZIO}

import java.util.concurrent.CompletableFuture
import scala.concurrent.Future

object RunZio {
  def runZio[E, A](effect: ZIO[Any, E, A]): Exit[E, A] = {
    Unsafe.unsafe { implicit u =>
      Runtime.default.unsafe.run(effect)
    }
  }

  def runZioThrow[E <: Throwable, A](effect: ZIO[Any, E, A]): A = {
    Unsafe.unsafe { implicit u =>
      Runtime.default.unsafe.run(effect)
        .getOrThrow()
    }
  }

  def runToFuture[E <: Throwable, A](effect: ZIO[Any, E, A]): Future[A] = {
    Unsafe.unsafe { implicit u =>
      Runtime.default.unsafe.runToFuture(effect)
    }
  }

  def runToCompletableFuture[E <: Throwable, A](effect: ZIO[Any, E, A]): CompletableFuture[A] = {
    Unsafe.unsafe { implicit u =>
      import scala.compat.java8.FutureConverters._
      Runtime.default.unsafe.runToFuture(effect).toJava.toCompletableFuture
    }
  }
}
