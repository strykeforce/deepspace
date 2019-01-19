package frc.team2767.deepspace.motion

import edu.wpi.first.wpilibj.Notifier
import mu.KotlinLogging
import org.strykeforce.thirdcoast.swerve.SwerveDrive
import org.strykeforce.thirdcoast.trapper.Action
import org.strykeforce.thirdcoast.trapper.post

const val K_P = 3.7
const val GOOD_ENOUGH = 5500
const val EXTRA_TIME = 1000L

private const val DT_MS = 20
private const val T1_MS = 200
private const val T2_MS = 100
private const val V_PROG = (12000 * 10).toDouble() // ticks/sec

class TwistController(private val drive: SwerveDrive, heading: Double, val distance: Int, targetYaw: Double = 0.0) {
    private val logger = KotlinLogging.logger {}

    private val motionProfile = MotionProfile(DT_MS, T1_MS, T2_MS, V_PROG, distance)
    private val notifier = Notifier(this::updateDrive)

    private val ticksPerSecMax = drive.wheels[0].driveSetpointMax * 10.0
    private val forwardComponent = Math.cos(Math.toRadians(heading)) / ticksPerSecMax
    private val strafeComponent = Math.sin(Math.toRadians(heading)) / ticksPerSecMax

    private val start = IntArray(4)

    private var action =
        Action(
            name = "Skippy Motion Profile",
            traceMeasures = listOf(
                // "millis", are added as first data element but are not a measure
                "profile_acc",
                "profile_vel",
                "setpoint_vel",
                "actual_vel",
                "profile_ticks",
                "actual_ticks",
                "forward",
                "strafe",
                "yaw",
                "gyro_angle"
            )
        )


    init {
        drive.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP)
        logger.debug {
            "INIT motion, heading = $heading, distance = $distance,\n" +
                    "ticks/sec max = $ticksPerSecMax\n" +
                    "forward = $forwardComponent, strafe = $strafeComponent\n"
        }

        action.meta["dt"] = DT_MS
        action.meta["t1"] = T1_MS
        action.meta["t2"] = T2_MS
        action.meta["v_prog"] = V_PROG.toInt()
        action.meta["direction"] = heading
        action.meta["yaw"] = targetYaw
        action.meta["tags"] = listOf("skippy", "twist")
        action.meta["type"] = "twist"
        action.meta["k_p"] = K_P
        action.meta["good_enough"] = GOOD_ENOUGH
        action.meta["profile_ticks"] = distance
    }

    var isFinished = false
    var extraTimeStart = 0L

    private val actualDistance: Double
        get() {
            var distance = 0.0
            for (i in 0..3) distance += Math.abs(drive.wheels[i].driveTalon.getSelectedSensorPosition(0) - start[i])
            return distance / 4.0
        }

    private val actualVelocity: Double
        get() {
            val wheel = drive.wheels[0]
            val sign = if (wheel.isInverted) -1.0 else 1.0
            return sign * wheel.driveTalon.getSelectedSensorVelocity(0).toDouble()
        }

    fun start() {
        notifier.startPeriodic(DT_MS / 1000.0)
        logger.info("START motion, gyro angle = {}", drive.gyro.angle)
        action.meta["gyro_start"] = drive.gyro.angle
        for (i in 0..3) start[i] = drive.wheels[i].driveTalon.getSelectedSensorPosition(0)
    }

    fun stop() {
        notifier.close()
        drive.drive(0.0, 0.0, 0.0)
        logger.info("FINISH motion position = {}", motionProfile.currPos)
        action.meta["gyro_end"] = drive.gyro.angle
        action.meta["actual_ticks"] = actualDistance
        action.post()
    }

    var iteration = 0

    private fun updateDrive() {
        var setpointVelocity = 0.0
        var forward = 0.0
        var strafe = 0.0
        var yaw = 0.0
        if (motionProfile.isFinished) {
            if (extraTimeStart == 0L) extraTimeStart = System.currentTimeMillis()
            iteration++
            drive.drive(0.0, 0.0, 0.0)
            if (System.currentTimeMillis() - extraTimeStart > EXTRA_TIME) {
                isFinished = true
                return
            }
        } else {
            motionProfile.calculate()
            iteration = motionProfile.iteration
            setpointVelocity = motionProfile.currVel + K_P * positionError()
            forward = forwardComponent * setpointVelocity
            strafe = strafeComponent * setpointVelocity
            yaw = 0.0
            drive.drive(forward, strafe, yaw)
        }
        action.traceData.add(
            listOf(
                (iteration * DT_MS).toDouble(), // millis
                motionProfile.currAcc,     // profile_acc
                motionProfile.currVel,     // profile_vel
                setpointVelocity,          // setpoint_vel
                actualVelocity,            // actual_vel
                motionProfile.currPos,     // profile_ticks
                actualDistance,            // actual_ticks
                forward,  // forward
                strafe,   // strafe
                yaw,   // yaw
                drive.gyro.angle
            )
        )
    }

    private fun positionError() = motionProfile.currPos - actualDistance
}
