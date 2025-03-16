package fastcsv4s3.fieldDecoderInstance

import fastcsv4s3.FieldDecoder

trait HiPriorityFieldDecoderInstance {
  given stringFieldDecoder: FieldDecoder[String] with
    def decode(field: String): Either[String, String] = Right(field)

  given longFieldDecoder: FieldDecoder[Long] with
    def decode(field: String): Either[String, Long] =
      try Right(field.toLong)
      catch { case _: NumberFormatException => Left(s"Invalid Long value: '$field'") }

  given intFieldDecoder: FieldDecoder[Int] with
    def decode(field: String): Either[String, Int] =
      try Right(field.trim.toInt)
      catch
        case _: NumberFormatException => Left(s"Invalid Int value: '$field'")

  given doubleFieldDecoder: FieldDecoder[Double] with
    def decode(field: String): Either[String, Double] =
      try Right(field.trim.toDouble)
      catch
        case _: NumberFormatException => Left(s"Invalid Double value: '$field'")
}

object HiPriorityFieldDecoderInstance extends HiPriorityFieldDecoderInstance