package fastcsv4s3

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.{EitherValues, OptionValues}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FieldDecoderSpec extends AnyFunSuite with OptionValues with EitherValues {
  import fastcsv4s3.fieldDecoderInstance.FieldDecoderInstance.given

  case class Id(value: String)
  case class Age(value: Int)
  case class WrapAge(age: Age)

  given FieldDecoder[LocalDateTime] with
    def decode(field: String): Either[String, LocalDateTime] =
      try Right(LocalDateTime.parse(field.trim, DateTimeFormatter.ISO_LOCAL_DATE_TIME))
      catch
        case _: Exception => Left(s"Invalid LocalDateTime value: '$field'")

  test("FieldDecoder[String] should trim input") {
    val input = "  hello world  "
    val result = summon[FieldDecoder[String]].decode(input)
    assert(result == Right("  hello world  "))
  }

  test("FieldDecoder[Long] should valid long") {
    val input = "12345"
    val result = summon[FieldDecoder[Long]].decode(input)
    assert(result === Right(12345.toLong))
  }

  test("FieldDecoder[Long] should invalid long") {
    val input = " 12345 "
    val result = summon[FieldDecoder[Long]].decode(input)
    assert(result.isLeft)
    assert(result.left.value === "Invalid Long value: ' 12345 '")
  }

  test("FieldDecoder[Long] should fail for invalid long") {
    val input = "abc"
    val result = summon[FieldDecoder[Long]].decode(input)
    assert(result.isLeft)
  }

  test("FieldDecoder[Int] should decode valid int") {
    val input = " 42 "
    val result = summon[FieldDecoder[Int]].decode(input)
    assert(result == Right(42))
  }

  test("FieldDecoder[Int] should fail for invalid int") {
    val input = "4.2"
    val result = summon[FieldDecoder[Int]].decode(input)
    assert(result.isLeft)
    assert(result.left.value === "Invalid Int value: '4.2'")
  }

  test("FieldDecoder[Double] should decode valid double") {
    val input = " 3.14 "
    val result = summon[FieldDecoder[Double]].decode(input)
    assert(result == Right(3.14))
  }

  test("FieldDecoder[Double] should fail for invalid double") {
    val input = "three.point.one.four"
    val result = summon[FieldDecoder[Double]].decode(input)
    assert(result.isLeft)
    assert(result.left.value === "Invalid Double value: 'three.point.one.four'")
  }

  test("FieldDecoder[Option[String]] should return Some for non-empty input") {
    val input = " data "
    val result = summon[FieldDecoder[Option[String]]].decode(input)
    assert(result == Right(Some(" data ")))
  }

  test("FieldDecoder[Option[String]] should return None for empty input") {
    val input = "    "
    val result = summon[FieldDecoder[Option[String]]].decode(input)
    assert(result == Right(None))
  }

  test("Derived FieldDecoder for wrap class Id should decode correctly") {
    val input = "  uniqueId123  "
    val result = summon[FieldDecoder[Id]].decode(input)
    assert(result == Right(Id("  uniqueId123  ")))
  }

  test("Derived FieldDecoder for wrap class Age should decode correctly") {
    val input = "  30  "
    val result = summon[FieldDecoder[Age]].decode(input)
    assert(result == Right(Age(30)))
  }

  test("Derived FieldDecoder for wrap class Age should fail for invalid input") {
    val input = "thirty"
    val result = summon[FieldDecoder[Age]].decode(input)
    assert(result.isLeft)
    assert(result.left.value === "Invalid Age value: 'thirty'")
  }

  test("Derived FieldDecoder for wrap class WrapAge should decode correctly") {
    val input = "30"
    val result = summon[FieldDecoder[WrapAge]].decode(input)
    assert(result == Right(WrapAge(Age(30))))
  }

  test("Derived FieldDecoder for wrap class WrapAge should fail for empty input") {
    val input = "lie_age"
    val result = summon[FieldDecoder[WrapAge]].decode(input)
    assert(result.isLeft)
    assert(result.left.value === "Invalid WrapAge value: 'lie_age'")
  }

  // simulate user define decoder
  test("FieldDecoder[LocalDateTime] should decode valid LocalDateTime") {
    val input = "2025-03-15T10:15:30"
    val result = summon[FieldDecoder[LocalDateTime]].decode(input)
    val expected = LocalDateTime.of(2025, 3, 15, 10, 15, 30)
    assert(result == Right(expected))
  }

  test("FieldDecoder[LocalDateTime] should fail for invalid LocalDateTime") {
    val input = "15-03-2025 10:15:30"
    val result = summon[FieldDecoder[LocalDateTime]].decode(input)
    assert(result.isLeft)
    assert(result.left.value === s"Invalid LocalDateTime value: '$input'")
  }
}
