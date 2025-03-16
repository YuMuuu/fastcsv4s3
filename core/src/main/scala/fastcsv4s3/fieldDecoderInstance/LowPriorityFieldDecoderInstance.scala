package fastcsv4s3.fieldDecoderInstance

import fastcsv4s3.FieldDecoder

import scala.annotation.nowarn
import scala.compiletime.constValue
import scala.deriving.Mirror

trait LowPriorityFieldDecoderInstance {
  given [T](using dec: FieldDecoder[T]): FieldDecoder[Option[T]] with
    def decode(field: String): Either[String, Option[T]] =
      if field.trim.isEmpty then Right(None)
      else dec.decode(field).map(Some(_))

  @nowarn("msg=New anonymous class definition will be duplicated.*")
  inline given derivedValueWrapper[A, T](using
                                         m: Mirror.ProductOf[A] { type MirroredElemTypes = Tuple1[T] },
                                         fd: FieldDecoder[T]
                                        ): FieldDecoder[A] =
    new FieldDecoder[A]:
      def decode(field: String): Either[String, A] =
        fd.decode(field) match
          case Right(t) => Right(m.fromTuple(Tuple1(t)))
          case Left(_)  => Left(s"Invalid ${constValue[m.MirroredLabel]} value: '$field'")
}
