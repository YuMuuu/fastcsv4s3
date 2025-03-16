package fastcsv4s3.fastCsvDecoderInstance

import de.siegmar.fastcsv.reader.NamedCsvRecord
import fastcsv4s3.{FastCsvDecoder, FieldDecoder}

import scala.annotation.nowarn
import scala.annotation.nowarn
import scala.compiletime.{constValueTuple, erasedValue, summonInline}
import scala.deriving.Mirror

trait NamedCsvRecordDecoder[T] extends FastCsvDecoder[NamedCsvRecord, T]

object NamedCsvRecordDecoder:
  def apply[T](implicit decoder: NamedCsvRecordDecoder[T]): NamedCsvRecordDecoder[T] = decoder

  private inline def decodeElems[Elems <: Tuple](
                                                  rec: NamedCsvRecord,
                                                  labels: Tuple
                                                ): Either[String, Elems] =
    inline erasedValue[Elems] match
      case _: EmptyTuple => Right(EmptyTuple.asInstanceOf[Elems])
      case _: (head *: tail) =>
        val fieldName = labels.productElement(0).asInstanceOf[String]
        val headDecoder = summonInline[FieldDecoder[head]]
        val headValue = headDecoder.decode(rec.getField(fieldName))
        headValue.flatMap(h => decodeElems[tail](rec, FastCsvDecoder.dropFirst(labels)).map(t => (h *: t).asInstanceOf[Elems]))

  //TODO: inlineの再帰が深くなるのをどうにかできないか検討する tuple1 ~ tupleNに対応するderivedを全部手で定義すれば解決する？
  // もしくはMSPでうまいこと行かないか...?
  // shaplessのLazy的なやつを作っても良い
  @nowarn("msg=New anonymous class definition will be duplicated.*")
  inline given derived[T](using m: Mirror.ProductOf[T]): NamedCsvRecordDecoder[T] =
    new NamedCsvRecordDecoder[T]:
      def decode(rec: NamedCsvRecord): Either[String, T] =
        decodeElems[m.MirroredElemTypes](rec, constValueTuple[m.MirroredElemLabels]).map(m.fromTuple)

  def instance[T](f: NamedCsvRecord => Either[String, T]): NamedCsvRecordDecoder[T] = (rec: NamedCsvRecord) => f(rec)



