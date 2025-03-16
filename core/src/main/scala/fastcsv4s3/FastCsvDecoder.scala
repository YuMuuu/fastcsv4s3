package fastcsv4s3

import de.siegmar.fastcsv.reader.CsvRecord

trait FastCsvDecoder[R <: CsvRecord, T]:
  def decode(rec: R): Either[String, T]

object FastCsvDecoder:
  //TODO: ここに定義する意味はもうないのでutil classなどに移動させる
  inline def dropFirst(t: Tuple): Tuple =
    if t == EmptyTuple then EmptyTuple
    else t.drop(1)
