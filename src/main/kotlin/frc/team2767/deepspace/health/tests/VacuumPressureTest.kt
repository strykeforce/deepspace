package frc.team2767.deepspace.health.tests

import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.Timer
import frc.team2767.deepspace.health.Reportable
import frc.team2767.deepspace.health.Test
import frc.team2767.deepspace.health.VacuumGroup
import frc.team2767.deepspace.health.tests.VacuumPressureTest.State.*
import kotlinx.html.TagConsumer
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr
import mu.KotlinLogging


private val logger = KotlinLogging.logger {}

class VacuumPressureTest(private val group: VacuumGroup) : Test, Reportable {
    override var name = "vacuum pressure test"
    var pressure = 0.0
    var encoderTimeOutCount = 50
    var sealedWaitCount = 10_000
    var maxAcceptablePressureDrop = 0
    var valveCloseCount = 5
    var bufferSize = 5


    private var movingAverage = mutableListOf<Int>()
    private var pressures = mutableListOf<Double>()
    private var state = STARTING
    private var iteration = 0
    private var startingPosition = 0.0
    private var startTime = 0.0

    override fun execute() {
        when (state) {
            STARTING -> {
                name = "pressure test to $pressure"

                logger.info { "$name starting, setting to $pressure" }

                startingPosition = group.vacuumSubsystem.pressure
                group.vacuumSubsystem.pressure = pressure

                startTime = Timer.getFPGATimestamp()

                group.vacuumSubsystem.climbSolenoid.set(false)
                group.vacuumSubsystem.tridentSolenoid.set(false)

                state = RUNNING
            }


            RUNNING -> {
                if (group.vacuumSubsystem.onTarget()) {
                    iteration = 0
                    logger.debug { "vacuum on target" }
                    state = SEALING
                }

                if (iteration++ > encoderTimeOutCount) {
                    logger.warn { "time out waiting for pressure" }
                    group.vacuumSubsystem.talons.first().set(ControlMode.PercentOutput, 0.0)
                    state = STOPPED
                }
            }

            SEALING -> {
                if (iteration++ > valveCloseCount) {
                    group.vacuumSubsystem.talons.first().set(ControlMode.PercentOutput, 0.0)
                    state = MEASURING
                }
            }

            MEASURING -> {

                val currentPressure = group.vacuumSubsystem.talons.first().selectedSensorPosition
                movingAverage[iteration % bufferSize] = currentPressure

                if (iteration > bufferSize) {
                    pressures.add(movingAverage.average())
                }

                if (iteration++ > sealedWaitCount) {
                    logger.debug { "stopping" }
                    state = STOPPED
                }
            }

            STOPPED -> {
                logger.info { "vacuum test stopped" }
            }
        }
    }

    override fun isFinished() = state == State.STOPPED

    override fun report(tagConsumer: TagConsumer<Appendable>) = reportTable(tagConsumer)

    override fun reportHeader(tagConsumer: TagConsumer<Appendable>) {
        tagConsumer.tr {
            th { +"Setpoint (in Hg)" }
            th { +"Max Acceptable Pressure Drop" }
            th { +"Time sealed" }
            th { +"Pressure drop" }
        }
    }

    override fun reportRows(tagConsumer: TagConsumer<Appendable>) {
        val pressureDrop = pressure - pressures.last()
        tagConsumer.tr {
            td { +"$pressure" }
            td { +"$maxAcceptablePressureDrop" }
            td { +"$sealedWaitCount" }
            td { +"$pressureDrop" }
        }
    }

    @Suppress("unused")
    private enum class State {
        STARTING,
        RUNNING,
        SEALING,
        MEASURING,
        STOPPED
    }
}