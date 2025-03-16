package fastcsv4s3.reader.types

import io.github.iltotore.iron.{:|, refineOption, refineUnsafe}
import io.github.iltotore.iron.constraint.numeric.Greater

object BaseTypes {
  opaque type NonNegInt = Int :| Greater[0]

  object NonNegInt:
    def apply(i: Int): Option[NonNegInt] = i.refineOption
    def unsafeApply(i: Int): NonNegInt = i.refineUnsafe

  extension (n: NonNegInt)
    def unwrap: Int = n
}
