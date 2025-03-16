package fastcsv4s3

import scala.compiletime.constValue

trait FieldDecoder[T] {
  def decode(field: String): Either[String, T]
}

object FieldDecoder {
  def apply[T](using fd: FieldDecoder[T]): FieldDecoder[T] = fd
}
