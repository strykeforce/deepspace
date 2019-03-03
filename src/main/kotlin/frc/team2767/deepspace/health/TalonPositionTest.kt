package frc.team2767.deepspace.health

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import frc.team2767.deepspace.health.TalonPositionTest.State.*
import kotlinx.html.TagConsumer
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr
import mu.KotlinLogging
import kotlin.math.absoluteValue

private val logger = KotlinLogging.logger {}

@Suppress("MemberVisibilityCanBePrivate")
class TalonPositionTest(private val group: TalonGroup) : Test, Reportable {
    override var name = "position test"
    var percentOutput = 0.0
    var peakVoltage = 12.0
    var zeroGoodEnough = 5
    var zeroTimeOutCount = 10
    var encoderTarget = 0
    var encoderTimeOutCount = 0
    var controlMode = ControlMode.MotionMagic

    private var state = STARTING
    private var iteration = 0
    private var countingUp = true
    private var passed = false

    private lateinit var talon: TalonSRX
    private var currents = mutableListOf<Double>()
    private var speeds = mutableListOf<Int>()


    override fun execute() {
        when (state) {
            STARTING -> {
                name = "position test at ${percentOutput * peakVoltage} volts"
                if (group.talons.size != 1) {
                    logger.error { "position test valid for one talon, has ${group.talons.size}, skipping" }
                    state = STOPPED
                    return
                }
                logger.info { "$name starting" }
                talon = group.talons.first()
                countingUp = encoderTarget > 0
                talon.selectedSensorPosition = 0
                state = ZEROING
            }

            ZEROING -> {
                if (talon.selectedSensorPosition.absoluteValue < zeroGoodEnough) {
                    logger.info { "successfully zeroed encoder" }
                    talon.set(PercentOutput, percentOutput)
                    iteration = 0
                    state = RUNNING
                    return
                }
                if (iteration++ > zeroTimeOutCount) {
                    logger.warn { "timed out waiting for encoder zero, skipping test" }
                    state = STOPPED
                }
            }

            RUNNING -> {
                currents.add(talon.outputCurrent)
                speeds.add(talon.selectedSensorVelocity)

                if (talon.selectedSensorPosition.absoluteValue > encoderTarget) {
                    logger.info { "reached encoder target $encoderTarget" }
                    talon.set(PercentOutput, 0.0)
                    state = PASSED
                    return
                }

                if (iteration++ > encoderTimeOutCount) {
                    logger.warn { "timed out waiting for encoder count, failing test" }
                    talon.set(PercentOutput, 0.0)
                    state = STOPPED
                }
            }

            PASSED -> {
                passed = true
                talon.set(controlMode, 0.0)
                state = RESETTING
            }

            RESETTING -> {
                if (talon.selectedSensorPosition.absoluteValue < zeroGoodEnough)
                    state = STOPPED
            }

            STOPPED -> logger.info { "position test stopped" }

        }
    }

    override fun isFinished() = state == STOPPED

    override fun report(tagConsumer: TagConsumer<Appendable>) = reportTable(tagConsumer)

    override fun reportHeader(tagConsumer: TagConsumer<Appendable>) {
        tagConsumer.tr {
            th { +"talon ID" }
            th { +"Setpoint (volts)" }
            th { +"Position (ticks)" }
            th { +"Current (amps)" }
            th { +"Speed (ticks/100ms)" }
        }
    }

    override fun reportRows(tagConsumer: TagConsumer<Appendable>) {
        tagConsumer.tr {
            td { +"${talon.deviceID}" }
            td { +"%.1f".format(percentOutput * peakVoltage) }
            td { +"$encoderTarget" }
            td { +"%.2f".format(currents.average()) }
            td { +"%.2f".format(speeds.average()) }
        }
    }

    @Suppress("unused")
    private enum class State {
        STARTING,
        ZEROING,
        RUNNING,
        PASSED,
        RESETTING,
        STOPPED
    }
}