package fastcsv4s3.reader

import cats.effect.{IO, Resource}
import de.siegmar.fastcsv.reader.CsvReader.CsvReaderBuilder
import de.siegmar.fastcsv.reader.{CsvReader, NamedCsvRecord}
import types.{ChunkSize, LineCount, MaxLines}
import fs2.io.toInputStream
import de.siegmar.fastcsv.reader.CsvRecord
import fastcsv4s3.reader.types.BaseTypes.NonNegInt

import scala.jdk.CollectionConverters.*
import java.util.{Spliterator, stream}
import java.util.function.Predicate

class CsvReader4S3[R <: CsvRecord](private val underlying: CsvReader[R]) extends AnyVal {
  //MEMO: iterator は streamで代替できるため提供しない
  def stream(chunkSize: ChunkSize = ChunkSize(NonNegInt.unsafeApply(128))): fs2.Stream[IO, R] =
    fs2.Stream.resource(Resource.fromAutoCloseable(IO.delay(underlying.iterator()))).flatMap{ it =>
      fs2.Stream.fromBlockingIterator[IO](it.asScala, chunkSize.value.unwrap)
    }

  def skipLines(lineCount: LineCount): IO[Unit] =
      IO(underlying.skipLines(lineCount.value.unwrap))

  def skipLines(predicate: Predicate[String], maxLines: MaxLines): IO[Int] =
    IO(underlying.skipLines(predicate, maxLines.value.unwrap))

  //MEMO: 本当にパフォーマンスを出したい時はParallelStreamを作成して利用した方が早いかも？
  def spliterator: Spliterator[R] = underlying.spliterator

  def close(): Unit = underlying.close()
}

object CsvReader4S3 {
  def resource[R <: CsvRecord](csvReader: CsvReader[R]): Resource[IO, CsvReader4S3[R]] =
    Resource.make(IO(CsvReader4S3[R](csvReader)))(cr => IO(cr.close()))
}
