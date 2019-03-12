package frc.team2767.deepspace.health

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import frc.team2767.deepspace.Robot
import frc.team2767.deepspace.health.TestGroup.State.*
import frc.team2767.deepspace.health.tests.TalonPosition
import frc.team2767.deepspace.health.tests.TalonPositionTest
import frc.team2767.deepspace.health.tests.TalonTimedTest
import frc.team2767.deepspace.health.tests.VacuumPressureTest
import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.h2
import kotlinx.html.table
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

abstract class TestGroup(val healthCheck: HealthCheck) : Test {
    override var name = "name not set"

    protected val tests = mutableListOf<Test>()
    private var state = STARTING
    private lateinit var iterator: Iterator<Test>
    private lateinit var currentTest: Test


    override fun execute() = when (state) {
        STARTING -> {
            logger.info { "$name starting" }
            check(tests.isNotEmpty()) { "no tests in test group '$name'" }
            iterator = tests.iterator()
            currentTest = iterator.next()
            state = RUNNING
        }

        RUNNING -> if (!currentTest.isFinished()) {
            currentTest.execute()
        } else if (iterator.hasNext()) {
            currentTest = iterator.next()
        } else {
            logger.info { "$name finished" }
            state = STOPPED
        }

        STOPPED -> throw IllegalStateException()
    }

    override fun isFinished() = state == State.STOPPED

    override fun report(tagConsumer: TagConsumer<Appendable>) {
        tagConsumer.div {
            h2 { +name }
            table {
                val reportable = tests.filterIsInstance<Reportable>()
                if (!reportable.isEmpty()) {
                    reportable.first().apply { reportHeader(tagConsumer) }
                    reportable.forEach { it.reportRows(tagConsumer) }
                }
            }
        }
    }


    override fun toString(): String {
        return "TestGroup(name='$name', tests=$tests)"
    }


    private enum class State {
        STARTING,
        RUNNING,
        STOPPED
    }

}


class TalonGroup(healthCheck: HealthCheck) : TestGroup(healthCheck) {
    var talons = emptyList<TalonSRX>()

    fun timedTest(init: TalonTimedTest.() -> Unit): Test {
        val spinTest = TalonTimedTest(this)
        spinTest.init()
        tests.add(spinTest)
        return spinTest
    }

    fun positionTest(init: TalonPositionTest.() -> Unit): Test {
        val positionTest = TalonPositionTest(this)
        positionTest.init()
        tests.add(positionTest)
        return positionTest
    }

    fun positionTalon(init: TalonPosition.() -> Unit): Test {
        val position = TalonPosition(this)
        position.init()
        tests.add(position)
        return position
    }
}

class VacuumGroup(healthCheck: HealthCheck) : TestGroup(healthCheck) {
    val vacuumSubsystem = Robot.VACUUM

    fun pressureTest(init: VacuumPressureTest.() -> Unit): Test {

        logger.debug { "vacuum group" }
        val pressureTest = VacuumPressureTest(this)
        pressureTest.init()
        tests.add(pressureTest)
        return pressureTest
    }
}
