package fastcsv4s3

import scala.compiletime.constValue
import scala.deriving.Mirror
import scala.util.{Failure, Success, Try}

trait FieldDecoder[T] {
  def decode(field: String): Either[String, T]
}

object FieldDecoder {
  def apply[T](using fd: FieldDecoder[T]): FieldDecoder[T] = fd

  inline given derivedValueWrapper[A, T](using
                                         m: Mirror.ProductOf[A] {type MirroredElemTypes = Tuple1[T]},
                                         fd: FieldDecoder[T]
                                        ): FieldDecoder[A] =
    new FieldDecoder[A]:
      def decode(field: String): Either[String, A] =
        fd.decode(field) match {

          case Right(t)
          => Right(m.fromTuple(Tuple1(t)))
          case Left(_)
          => Left(s"Invalid ${constValue[m.MirroredLabel]} value: '$field'")
        }
}

