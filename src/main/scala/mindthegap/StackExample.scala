package mindthegap
import RunZioTrace.runZioThrow
import zio._

/**
 * ZIO based library with non-zio API
 */
object ZioLib {
  def calculate(): Int = runZioThrow(step1)
  private def step1: Task[Int] = step2.delay(1.millis).map(_ + 1)
  private def step2: Task[Int] = kaboom.delay(1.millis).map(_ + 1)
  private def kaboom: Task[Int] = ZIO.fail(new RuntimeException("boom"))
}

/**
 * A Plain Scala app that uses the ZIO based library
 */
object ScalaApp extends App {
  private def calculateInternal() = ZioLib.calculate()
  def calculate(): Int = calculateInternal() + 1
  calculate()
}

object RunZioTrace {
  def runZioThrow[E <: Throwable, A](effect: ZIO[Any, E, A])
                                    (implicit trace: Trace): A = {
    // capture the stack before executing the effect
    val stack = StackTrace(FiberId.None, Chunk.fromArray(
      Trace("--> zio.Runtime.unsafe.run", "",  -1) +:
        Thread.currentThread.getStackTrace.drop(1).map(Trace.fromJava)
    ))
    // instrument the effect to append the captured stack to errors
    val traced = effect.mapErrorCause { _.traced(stack) }
    Unsafe.unsafe { implicit u =>
      Runtime.default.unsafe.run(traced).getOrThrow()
    }
  }
}


