package frc.team2767.deepspace.motion

import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.Preferences
import mu.KotlinLogging
import org.strykeforce.thirdcoast.swerve.SwerveDrive
import org.strykeforce.thirdcoast.trapper.Action
import org.strykeforce.thirdcoast.trapper.post
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.sign

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
    private val kVProg = prefs.getInt(V_PROFILE, 12000 * 10)
    private val kPDistance = prefs.getDouble(K_P_DISTANCE, 3.7)
    private val kGoodEnoughDistance = prefs.getInt(GOOD_ENOUGH_DISTANCE, 5500)
    private val kPYaw = prefs.getDouble(K_P_YAW, 0.0)
    private val kMaxYaw = prefs.getDouble(MAX_YAW, 0.01)
    private val kExtraTime = prefs.getLong(EXTRA_TIME, 1000L)
    private val kTrace = prefs.getBoolean(TRACE, false)

    private val motionProfile = MotionProfile(kDtMs, kT1Ms, kT2Ms, kVProg, distance)
    private val notifier = Notifier(this::updateDrive)

    private val ticksPerSecMax = drive.wheels[0].driveSetpointMax * 10.0
    private val forwardComponent = Math.cos(Math.toRadians(heading)) / ticksPerSecMax
    private val strafeComponent = Math.sin(Math.toRadians(heading)) / ticksPerSecMax

    private val start = IntArray(4)

    private var action = Action(name = "Skippy Motion Profile", traceMeasures = TRACE_MEASURES)


    init {
        drive.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP)
        logger.debug {
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
            meta["good_enough"] = kGoodEnoughDistance
            meta["profile_ticks"] = distance
        }
        initializePreferences()
    }

    var isFinished = false
    var extraTimeStart = 0L

    private val actualDistance
        get() = drive.wheels.foldIndexed(0.0) { index, sum, wheel ->
            sum + (wheel.driveTalon.getSelectedSensorPosition(0) - start[index]).absoluteValue
        } / 4.0


    private val actualVelocity: Int
        get() = drive.wheels[0].driveTalon.getSelectedSensorVelocity(0)

    private val actualDriveCurrent: Double
        get() {
            val wheel = drive.wheels[0]
            return wheel.driveTalon.outputCurrent
        }

    fun start() {
        notifier.startPeriodic(DT_MS_DEFAULT / 1000.0)
        logger.info("START motion, gyro angle = {}", drive.gyro.angle)
        for (i in 0..3) start[i] = drive.wheels[i].driveTalon.getSelectedSensorPosition(0)
        if (kTrace) action.meta["gyro_start"] = drive.gyro.angle
    }

    fun stop() {
        notifier.close()
        drive.drive(0.0, 0.0, 0.0)
        logger.info("FINISH motion position = {}", motionProfile.currPos)
        if (kTrace) with(action) {
            meta["gyro_end"] = drive.gyro.angle
            meta["actual_ticks"] = actualDistance
            post()
        }
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
            if (System.currentTimeMillis() - extraTimeStart > kExtraTime) {
                isFinished = true
                return
            }
        } else {
            motionProfile.calculate()
            iteration = motionProfile.iteration
            setpointVelocity = motionProfile.currVel + kPDistance * positionError
            forward = forwardComponent * setpointVelocity
            strafe = strafeComponent * setpointVelocity
            yaw = yawError.sign * min((kPYaw * yawError).absoluteValue, kMaxYaw)
            drive.drive(forward, strafe, yaw)
        }
        if (kTrace) action.traceData.add(
            listOf(
                (iteration * DT_MS_DEFAULT).toDouble(), // millis
                motionProfile.currAcc,     // profile_acc
                motionProfile.currVel,     // profile_vel
                setpointVelocity,          // setpoint_vel
                actualVelocity.toDouble(), // actual_vel
                motionProfile.currPos,     // profile_ticks
                actualDistance,            // actual_ticks
                forward,                   // forward
                strafe,                    // strafe
                yaw,                       // yaw
                drive.gyro.angle,
                actualDriveCurrent
            )
        )
    }

    private val positionError
        get() = motionProfile.currPos - actualDistance

    private val yawError
        get() = targetYaw - drive.gyro.angle

    private

    fun initializePreferences() {
        val prefs = Preferences.getInstance()
        if (prefs.getBoolean(PREFS_INITIALIZED, false)) return
        with(prefs) {
            putInt(DT_MS, kDtMs)
            putInt(T1_MS, kT1Ms)
            putInt(T2_MS, kT2Ms)
            putInt(V_PROFILE, kVProg)
            putDouble(K_P_DISTANCE, kPDistance)
            putInt(GOOD_ENOUGH_DISTANCE, kGoodEnoughDistance)
            putDouble(K_P_YAW, kPYaw)
            putDouble(MAX_YAW, kMaxYaw)
            putLong(EXTRA_TIME, kExtraTime)
            putBoolean(TRACE, kTrace)
            putBoolean(PREFS_INITIALIZED, true)
        }
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
    "drive_current"
)

private const val PREFS_NAME = "TwistController"
private const val PREFS_INITIALIZED = "$PREFS_NAME/initialized"
private const val MOTION_PROFILE = "MotionProfile"
private const val DISTANCE_LOOP = "DistanceLoop"
private const val YAW_LOOP = "YawLoop"
private const val TRACE = "save_trace"
private const val K_P_DISTANCE = "$PREFS_NAME/$DISTANCE_LOOP/k_p"
private const val GOOD_ENOUGH_DISTANCE = "$PREFS_NAME/$DISTANCE_LOOP/good_enough"
private const val K_P_YAW = "$PREFS_NAME/$YAW_LOOP/k_p"
private const val MAX_YAW = "$PREFS_NAME/$YAW_LOOP/max_yaw"
private const val EXTRA_TIME = "$PREFS_NAME/$MOTION_PROFILE/extra_time"
private const val DT_MS = "$PREFS_NAME/$MOTION_PROFILE/dt"
private const val DT_MS_DEFAULT = 20
private const val T1_MS = "$PREFS_NAME/$MOTION_PROFILE/t1"
private const val T2_MS = "$PREFS_NAME/$MOTION_PROFILE/t2"
private const val V_PROFILE = "$PREFS_NAME/$MOTION_PROFILE/v_profile"
