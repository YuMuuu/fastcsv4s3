package fastcsv4s3.fastCsvDecoderInstance

import de.siegmar.fastcsv.reader.CsvRecord
import fastcsv4s3.{FastCsvDecoder, FieldDecoder}

import scala.annotation.nowarn
import scala.compiletime.{constValueTuple, erasedValue, summonInline}
import scala.deriving.Mirror

trait CsvRecordDecoder[T] extends FastCsvDecoder[CsvRecord, T]

object CsvRecordDecoder:
  def apply[T](implicit decoder: CsvRecordDecoder[T]): CsvRecordDecoder[T] = decoder


  private inline def decodeElems[Elems <: Tuple](
                                                  rec: CsvRecord,
                                                  labels: Tuple,
                                                  columnIndex: Int = 0
                                                ): Either[String, Elems] =
    inline erasedValue[Elems] match
      case _: EmptyTuple => Right(EmptyTuple.asInstanceOf[Elems])
      case _: (head *: tail) =>
        val decoder = summonInline[FieldDecoder[head]]
        val value = decoder.decode(rec.getField(columnIndex))
        value.flatMap(h => decodeElems[tail](rec, FastCsvDecoder.dropFirst(labels), columnIndex + 1).map(t => (h *: t).asInstanceOf[Elems]))

  @nowarn("msg=New anonymous class definition will be duplicated.*")
  inline given derived[T](using m: Mirror.ProductOf[T]): CsvRecordDecoder[T] =
    new CsvRecordDecoder[T]:
      def decode(rec: CsvRecord): Either[String, T] =
        decodeElems[m.MirroredElemTypes](rec, constValueTuple[m.MirroredElemLabels]).map(m.fromTuple)

  def instance[T](f: CsvRecord => Either[String, T]): CsvRecordDecoder[T] = (rec: CsvRecord) => f(rec)