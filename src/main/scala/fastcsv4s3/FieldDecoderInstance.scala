package fastcsv4s3

object FieldDecoderInstance {
  given FieldDecoder[String] with
    def decode(field: String): Either[String, String] = Right(field)

  given FieldDecoder[Long] with
    def decode(field: String): Either[String, Long] =
      try Right(field.toLong)
      catch { case _: NumberFormatException => Left(s"Invalid Long value: '$field'") }

  given FieldDecoder[Int] with
    def decode(field: String): Either[String, Int] =
      try Right(field.trim.toInt)
      catch
        case _: NumberFormatException => Left(s"Invalid Int value: '$field'")

  given FieldDecoder[Double] with
    def decode(field: String): Either[String, Double] =
      try Right(field.trim.toDouble)
      catch
        case _: NumberFormatException => Left(s"Invalid Double value: '$field'")

  given [T](using dec: FieldDecoder[T]): FieldDecoder[Option[T]] with
    def decode(field: String): Either[String, Option[T]] =
      if field.trim.isEmpty then Right(None)
      else dec.decode(field).map(Some(_))
}