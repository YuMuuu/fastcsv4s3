package fastcsv4s3

import scala.util.{Failure, Success, Try}

trait FieldDecoder[T] {
  def decode(field: String): Either[String, T]
}

object FieldDecoder {
  def apply[T](using fd: FieldDecoder[T]): FieldDecoder[T] = fd
}

