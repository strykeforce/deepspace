package frc.team2767.deepspace.health

import kotlinx.html.TagConsumer

interface Test {
    var name: String
    fun execute()
    fun isFinished(): Boolean
    fun report(tagConsumer: TagConsumer<Appendable>)
}


private fun <T : Comparable<T>> ClosedRange<T>.withUnits(units: String) =
    "${this.start} - ${this.endInclusive} $units"
