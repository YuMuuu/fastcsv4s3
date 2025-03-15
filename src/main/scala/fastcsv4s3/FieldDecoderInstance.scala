package fastcsv4s3

object FieldDecoderInstance {
  given FieldDecoder[String] with
    def decode(field: String): Either[String, String] = Right(field)

  given FieldDecoder[Long] with
    def decode(field: String): Either[String, Long] =
      try Right(field.toLong)
      catch { case _: NumberFormatException => Left(s"Invalid Long: $field") }
}