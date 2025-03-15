package fastcsv4s3

import de.siegmar.fastcsv.reader.NamedCsvRecord

import scala.compiletime.{constValueTuple, erasedValue, summonInline}
import scala.deriving.Mirror

trait FastCsvDecoder[T] {
  def decode(rec: NamedCsvRecord): Either[String, T]
}

object FastCsvDecoder {
  def apply[T](implicit decoder: FastCsvDecoder[T]): FastCsvDecoder[T] = decoder

  def instance[T](f: NamedCsvRecord => Either[String, T]): FastCsvDecoder[T] =
    (rec: NamedCsvRecord) => f(rec)

  private inline def decodeElems[Elems <: Tuple](
                                          rec: NamedCsvRecord,
                                          labels: Tuple
                                        ): Either[String, Elems] =
    inline erasedValue[Elems] match
      case _: EmptyTuple =>
        Right(EmptyTuple.asInstanceOf[Elems])
      case _: (head *: tail) =>
        val fieldName = labels.productElement(0).asInstanceOf[String]
        val headDecoder = summonInline[FieldDecoder[head]]
        val headValue = headDecoder.decode(rec.getField(fieldName))
        headValue.flatMap { h =>
          decodeElems[tail](rec, dropFirst(labels)).map { t =>
            (h *: t).asInstanceOf[Elems]
          }
        }
    
  private inline def dropFirst(t: Tuple): Tuple =
    if t == EmptyTuple then EmptyTuple
    else t.drop(1)
    
  inline given derived[T](using m: Mirror.ProductOf[T]): FastCsvDecoder[T] =
    new FastCsvDecoder[T]:
      def decode(rec: NamedCsvRecord): Either[String, T] =
        decodeElems[m.MirroredElemTypes](rec, constValueTuple[m.MirroredElemLabels]).map(m.fromTuple)
}