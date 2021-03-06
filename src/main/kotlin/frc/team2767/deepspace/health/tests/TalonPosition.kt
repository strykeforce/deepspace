package frc.team2767.deepspace.health.tests

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import frc.team2767.deepspace.health.TalonGroup
import frc.team2767.deepspace.health.Test
import frc.team2767.deepspace.health.tests.TalonPosition.State.RUNNING
import frc.team2767.deepspace.health.tests.TalonPosition.State.STOPPED
import kotlinx.html.TagConsumer
import mu.KotlinLogging
import kotlin.math.absoluteValue

private val logger = KotlinLogging.logger {}

@Suppress("MemberVisibilityCanBePrivate")
class TalonPosition(private val group: TalonGroup) : Test {
    override var name = "position talon"
    var controlMode = ControlMode.MotionMagic
    var encoderTarget = 0
    var encoderGoodEnough = 10

    private var state = State.STARTING
    private lateinit var talon: TalonSRX

    override fun execute() {
        when (state) {
            State.STARTING -> {
                if (group.talons.size != 1) {
                    logger.error { "position test valid for one talon, has ${group.talons.size}, skipping" }
                    state = STOPPED
                    return
                }
                logger.info { "$name starting" }
                talon = group.talons.first()
                talon.set(controlMode, encoderTarget.toDouble())
                state = RUNNING
            }
            RUNNING -> {
                if ((encoderTarget - talon.selectedSensorPosition).absoluteValue < encoderGoodEnough) {
                    logger.info { "repositioned to $encoderTarget, finishing" }
                    state = STOPPED
                }
            }
            STOPPED -> logger.info { "position talon stopped" }
        }
    }

    override fun isFinished() = state == STOPPED

    override fun report(tagConsumer: TagConsumer<Appendable>) {}

    @Suppress("unused")
    private enum class State {
        STARTING,
        RUNNING,
        STOPPED
    }

}