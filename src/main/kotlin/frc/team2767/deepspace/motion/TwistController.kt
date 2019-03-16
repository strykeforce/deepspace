package frc.team2767.deepspace.motion

import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.Preferences
import frc.team2767.deepspace.motion.TwistState.*
import mu.KotlinLogging
import org.strykeforce.thirdcoast.swerve.SwerveDrive
import org.strykeforce.thirdcoast.swerve.Wheel
import org.strykeforce.thirdcoast.trapper.Action
import org.strykeforce.thirdcoast.trapper.post
import kotlin.math.absoluteValue
import kotlin.math.log

private enum class TwistState { STARTING, RUNNING, STOPPING, STOPPED }

class TwistController(
    private val drive: SwerveDrive,
    heading: Double,
    private val distance: Int,
    private val targetYaw: Double = 0.0
) {
    private val logger = KotlinLogging.logger {}
    private val prefs = Preferences.getInstance()

    private val kDtMs = prefs.getInt(DT_MS, 20)
    private val kT1Ms = prefs.getInt(T1_MS, 200)
    private val kT2Ms = prefs.getInt(T2_MS, 100)
    private var kVProg = prefs.getInt(V_PROFILE, 120_000)
    private var kPDistance = prefs.getDouble(K_P_DISTANCE, 3.7)
    //    private val kGoodEnoughDistance = prefs.getInt(GOOD_ENOUGH_DISTANCE, 5500)
    private var kPYaw = prefs.getDouble(K_P_YAW, 0.018)
    private val kMaxYaw = prefs.getDouble(MAX_YAW, 0.01)
    private val kYawTpd = prefs.getDouble(YAW_TPD, 0.0)
    private val kExtraTime = prefs.getLong(EXTRA_TIME, 1000L)
    private val kTrace = prefs.getBoolean(TRACE, false)

    private val motionProfile = MotionProfile(kDtMs, kT1Ms, kT2Ms, kVProg, distance)
    private val notifier = Notifier(this::process)

    private val ticksPerSecMax = drive.wheels[0].driveSetpointMax * 10.0
    private val forwardComponent = Math.cos(Math.toRadians(heading)) / ticksPerSecMax
    private val strafeComponent = Math.sin(Math.toRadians(heading)) / ticksPerSecMax

    private val start = IntArray(4)

    private var action = Action(name = "Skippy Twist", traceMeasures = TRACE_MEASURES)
    private var extraTimeStart = 0L
    @Volatile
    private var state = STARTING
    private var lastYawAngle = 0.0
    private var yawDistanceCorrection = 0.0


    init {
        logger.info {
            "INIT motion, heading = $heading, distance = $distance,\n" +
                    "ticks/sec max = $ticksPerSecMax\n" +
                    "forward = $forwardComponent, strafe = $strafeComponent\n"
        }

        if (kTrace) with(action) {
            meta["dt"] = kDtMs
            meta["t1"] = kT1Ms
            meta["t2"] = kT2Ms
            meta["v_prog"] = kVProg
            meta["direction"] = heading
            meta["yaw"] = targetYaw
            meta["tags"] = listOf("skippy", "twist")
            meta["type"] = "twist"
            meta["k_p"] = kPDistance
//            meta["good_enough"] = kGoodEnoughDistance
            meta["profile_ticks"] = distance
        }
        savePreferences()
    }

    fun getPIDPrefs() {
        kPDistance = prefs.getDouble(K_P_DISTANCE, 3.7)
        kVProg = prefs.getInt(V_PROFILE, 120_000)
        kPYaw = prefs.getDouble(K_P_YAW, 0.018)
        logger.debug { "kPDistance = $kPDistance kVProg = $kVProg" }
    }

    fun start() {
        logger.info { "twist controller start" }
        notifier.startPeriodic(DT_MS_DEFAULT / 1000.0)
    }

    fun interrupt() {
        logger.warn("interrupted in $state state")
        state = STOPPED
    }

    val isFinished: Boolean
        get() = state == STOPPED


    private val actualDistance
        get() = drive.wheels.foldIndexed(0.0) { index, sum, wheel ->
            sum + (wheel.driveTalon.getSelectedSensorPosition(0) - start[index]).absoluteValue
        } / 4.0


    private val actualVelocity: Int
        get() = drive.wheels[0].driveTalon.getSelectedSensorVelocity(0)


    private val actualDriveCurrent: Double
        get() = drive.wheels[0].driveTalon.outputCurrent


    private val actualDriveVoltage: Double
        get() = drive.wheels[0].driveTalon.motorOutputVoltage


    private val positionError
        get() = motionProfile.currPos + yawDistanceCorrection - actualDistance


    private val yawError
        get() = targetYaw - drive.gyro.angle

    private fun isWheelYawInPhase(wheel: Wheel, index: Int, yawDelta: Double): Boolean {
        if (yawDelta > 0)
            return if (wheel.isInverted) index == 1 || index == 3 else index == 0 || index == 2
        return if (wheel.isInverted) index == 0 || index == 2 else return index == 1 || index == 3
    }


    private fun updateYawDistanceCorrection() {
        val yawAngle     = drive.gyro.angle
        val yawDelta = yawAngle - lastYawAngle
        val wheels = drive.wheels
        wheels.forEachIndexed { index, wheel ->
            if (isWheelYawInPhase(wheel, index, yawDelta)) yawDistanceCorrection += yawDelta.absoluteValue * kYawTpd
            else yawDistanceCorrection -= yawDelta.absoluteValue * kYawTpd
        }
        lastYawAngle = yawAngle
    }


    private fun process() {
        var velocity = 0.0
        var forward = 0.0
        var strafe = 0.0
        var yaw = 0.0

        motionProfile.calculate()

        when (state) {
            STARTING -> {
                drive.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP)
                drive.wheels.forEachIndexed { i, wheel -> start[i] = wheel.driveTalon.getSelectedSensorPosition(0) }
                lastYawAngle = drive.gyro.angle
                state = RUNNING
                logState()
                logger.debug { "current profile velocity = ${motionProfile.currVel}" }
            }
            RUNNING -> {
                velocity = motionProfile.currVel + kPDistance * positionError
                forward = forwardComponent * velocity
                strafe = strafeComponent * velocity

                logger.debug { "forward = $forward strafe = $strafe" }
                yaw = (kPYaw * yawError).coerceIn(-kMaxYaw, kMaxYaw)
                updateYawDistanceCorrection()

                drive.drive(forward, strafe, yaw)

                if (motionProfile.isFinished) {
                    extraTimeStart = System.currentTimeMillis()
                    drive.stop()
                    state = STOPPING
                    logState()
                }
            }
            STOPPING -> {
                if (System.currentTimeMillis() - extraTimeStart > kExtraTime) state = STOPPED
            }
            STOPPED -> {
                drive.stop()
                logger.debug { "ticks driven = $actualDistance" }
                if (kTrace) action.post()
                logState()
                notifier.close()
            }
        }

        if (kTrace && state != STOPPED) action.traceData.add(
            listOf(
                (motionProfile.iteration * DT_MS_DEFAULT).toDouble(), // millis
                motionProfile.currAcc,     // profile_acc
                motionProfile.currVel,     // profile_vel
                velocity,                  // setpoint_vel
                actualVelocity.toDouble(), // actual_vel
                motionProfile.currPos,     // profile_ticks
                actualDistance,            // actual_ticks
                forward,                   // forward
                strafe,                    // strafe
                yaw,                       // yaw
                drive.gyro.angle,          // gyro_angle
                actualDriveCurrent,        // drive_current
                actualDriveVoltage,        // drive_voltage
                yawDistanceCorrection                // yaw_distance
            )
        )
    }

    private fun logState() = logger.info("$state")


    private fun savePreferences() = with(Preferences.getInstance()) {
        if (!getBoolean(PREFS_SAVE, true)) return
        putInt(DT_MS, kDtMs)
        putInt(T1_MS, kT1Ms)
        putInt(T2_MS, kT2Ms)
        putInt(V_PROFILE, kVProg)
        putDouble(K_P_DISTANCE, kPDistance)
//        putInt(GOOD_ENOUGH_DISTANCE, kGoodEnoughDistance)
        putDouble(K_P_YAW, kPYaw)
        putDouble(MAX_YAW, kMaxYaw)
        putDouble(YAW_TPD, kYawTpd)
        putLong(EXTRA_TIME, kExtraTime)
        putBoolean(TRACE, kTrace)
        putBoolean(PREFS_SAVE, false)
    }

}

private val TRACE_MEASURES = listOf(
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
    "gyro_angle",
    "drive_current",
    "drive_voltage",
    "yaw_distance"
)

private const val PREFS_NAME = "TwistController"
private const val PREFS_SAVE = "$PREFS_NAME/save_prefs"
private const val TRACE = "$PREFS_NAME/save_trace"
private const val MOTION_PROFILE = "MotionProfile"
private const val DISTANCE_LOOP = "DistanceLoop"
private const val YAW_LOOP = "YawLoop"
private const val K_P_DISTANCE = "$PREFS_NAME/$DISTANCE_LOOP/k_p"
private const val GOOD_ENOUGH_DISTANCE = "$PREFS_NAME/$DISTANCE_LOOP/good_enough"
private const val K_P_YAW = "$PREFS_NAME/$YAW_LOOP/k_p"
private const val MAX_YAW = "$PREFS_NAME/$YAW_LOOP/max_yaw"
private const val YAW_TPD = "$PREFS_NAME/$DISTANCE_LOOP/yaw_tpd"
private const val EXTRA_TIME = "$PREFS_NAME/$MOTION_PROFILE/extra_time"
private const val DT_MS = "$PREFS_NAME/$MOTION_PROFILE/dt"
private const val DT_MS_DEFAULT = 20
private const val T1_MS = "$PREFS_NAME/$MOTION_PROFILE/t1"
private const val T2_MS = "$PREFS_NAME/$MOTION_PROFILE/t2"
private const val V_PROFILE = "$PREFS_NAME/$MOTION_PROFILE/v_profile"
