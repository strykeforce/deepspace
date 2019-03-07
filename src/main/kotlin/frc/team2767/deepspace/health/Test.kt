package frc.team2767.deepspace.health

import kotlinx.html.TagConsumer

fun <T : Comparable<T>> ClosedRange<T>.statusOf(value: T): String = if (this.contains(value)) "pass" else "fail"

interface Test {
    var name: String
    fun execute()
    fun isFinished(): Boolean
    fun report(tagConsumer: TagConsumer<Appendable>)
}
