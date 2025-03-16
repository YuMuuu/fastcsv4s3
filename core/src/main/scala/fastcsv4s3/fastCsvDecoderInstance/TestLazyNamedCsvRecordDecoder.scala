package fastcsv4s3.fastCsvDecoderInstance

import scala.annotation.nowarn
import scala.compiletime.{constValueTuple, erasedValue, summonInline}
import scala.deriving.Mirror

/**
 * test code
 */
final class Lazy[+T](private val thunk: () => T):
  lazy val value: T = thunk()

object Lazy:
  def apply[T](t: => T): Lazy[T] = new Lazy(() => t)

trait MyDecoder[T]:
  def decode(s: String): T

trait DerivedDecoder[Elems <: Tuple]:
  def decode(s: String): Elems

object DerivedDecoder:
  inline given derived[T <: Tuple]: DerivedDecoder[T] =
    inline erasedValue[T] match
      case EmptyTuple => ???
      case (head *: tail) => ???

object TestLazyNamedCsvRecordDecoder {
  given MyDecoder[String] with
    def decode(s: String): String = s

  @nowarn("msg=New anonymous class definition will be duplicated.*")
  inline given deriving[T](using m: Mirror.ProductOf[T], lazyDec: Lazy[DerivedDecoder[m.MirroredElemTypes]]): MyDecoder[T] =
    new MyDecoder[T] {
      def decode(s: String): T =
        m.fromTuple(lazyDec.value.decode(s))
    }

}

