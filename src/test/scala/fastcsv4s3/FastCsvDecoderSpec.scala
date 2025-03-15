package fastcsv4s3

import de.siegmar.fastcsv.reader.{CsvReader, NamedCsvRecord}
import fastcsv4s3.FieldDecoderInstance.given
import org.scalatest.{EitherValues, OptionValues}
import org.scalatest.funsuite.AnyFunSuite

import scala.deriving.Mirror
import scala.jdk.CollectionConverters.*
import scala.jdk.StreamConverters.*

class FastCsvDecoderSpec  extends AnyFunSuite with OptionValues with EitherValues {

  case class Person(id: Long, firstName: String, lastName: String)

  test("FastCsvDecoder should decode Person correctly") {
    val data =
      """id,firstName,lastName
        |1,John,Doe
        |2,Jane,Smith""".stripMargin

    val csv = CsvReader.builder().ofNamedCsvRecord(data)
    val results = csv.stream()
      .toScala(LazyList)
      .map { rec =>  FastCsvDecoder[Person].decode(rec) }
      .toList

    assert(results == List(
      Right(Person(1, "John", "Doe")),
      Right(Person(2, "Jane", "Smith"))
    ))
  }

  test("should return error for invalid numeric field") {
    val data =
      """id,firstName,lastName
        |not_a_number,John,Doe""".stripMargin

    val csv = CsvReader.builder().ofNamedCsvRecord(data)
    val rec = csv.stream().toScala(LazyList).headOption.value
    val result = FastCsvDecoder[Person].decode(rec)
    assert(result.isLeft)
    assert(result.left.value === "Invalid Long value: 'not_a_number'")
  }

  test("should return error when numeric field is missing") {
    val data =
      """id,firstName,lastName
        |,John,Doe""".stripMargin

    val csv = CsvReader.builder().ofNamedCsvRecord(data)
    val rec = csv.stream().toScala(LazyList).headOption.value
    val result = FastCsvDecoder[Person].decode(rec)
    assert(result.isLeft)
    assert(result.left.value === "Invalid Long value: ''")
  }

  test("should decode CSV with extra records") {
    val data =
      """id,firstName,lastName
        |1,John,Doe
        |2,Jane,Smith
        |3,Bob,Brown""".stripMargin

    val csv = CsvReader.builder().ofNamedCsvRecord(data)
    val results = csv.stream()
      .toScala(LazyList)
      .map(FastCsvDecoder[Person].decode)
      .toList

    assert(results == List(
      Right(Person(1, "John", "Doe")),
      Right(Person(2, "Jane", "Smith")),
      Right(Person(3, "Bob", "Brown"))
    ))
  }

  test("should return error if numeric field contains extra whitespace") {
    // Long.parseLong は余分な空白を許容しないため、エラーになるケースを想定
    val data =
      """id,firstName,lastName
        | 1 ,John,Doe""".stripMargin

    val csv = CsvReader.builder().ofNamedCsvRecord(data)
    val rec = csv.stream().toScala(LazyList).headOption.value
    val result = FastCsvDecoder[Person].decode(rec)
    assert(result.isLeft)
    assert(result.left.value === "Invalid Long value: ' 1 '")
  }
}
