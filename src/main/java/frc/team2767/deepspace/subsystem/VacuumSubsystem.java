package frc.team2767.deepspace.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.Robot;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

public class VacuumSubsystem extends Subsystem {

  private static final double COUNTS_PER_INHG = 35.533;
  private static final double COUNTS_OFFSET = 31.98;
  private static final double TEMP_OFFSET = 0.5;
  private static final double VOLTS_PER_CELSIUS = 0.01;
  private static final double TEMP_LIMIT = 93.33;
  private static final String PREFS_NAME = "VacuumSubsystem/Settings/";
  private static final int BACKUP = 2767;
  private static final int VACUUM_ID = 60;
  private static final int TEMPERATURE_ID = 0;
  public static double kBallPressureInHg;
  public static double kHatchPressureInHg;
  public static double kClimbPressureInHg;
  private static int kGoodEnoughGamePiece;
  private static int kGoodEnoughClimb;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final int STABLE_THRESHOLD = 4;
  private final Solenoid tridentSolenoid = new Solenoid(0, Valve.TRIDENT.ID);
  private final Solenoid climbSolenoid = new Solenoid(0, Valve.CLIMB.ID);
  private final TalonSRX vacuum = new TalonSRX(VACUUM_ID);
  private final AnalogInput analogInput = new AnalogInput(TEMPERATURE_ID);
  private int setpoint;
  private int stableCount;
  private int climbStableCounts;;

  public VacuumSubsystem() {
    stableCount = 0;
    climbStableCounts = 0;
    setpoint = 0;

    climbSolenoid.set(false);
    tridentSolenoid.set(false);

    configTalon();
    vacuumPreferences();
  }

  @SuppressWarnings("Duplicates")
  private void configTalon() {
    TalonSRXConfiguration vacuumConfig = new TalonSRXConfiguration();
    vacuumConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.Analog;
    vacuumConfig.continuousCurrentLimit = 25;
    vacuumConfig.peakCurrentDuration = 40;
    vacuumConfig.peakCurrentLimit = 30;
    vacuumConfig.slot0.kP = 16;
    vacuumConfig.slot0.kI = 0;
    vacuumConfig.slot0.kD = 150;
    vacuumConfig.slot0.kF = 0;
    vacuumConfig.slot0.integralZone = 0;
    vacuumConfig.slot0.allowableClosedloopError = 0;
    vacuumConfig.voltageCompSaturation = 12;
    vacuumConfig.voltageMeasurementFilter = 32;
    vacuumConfig.peakOutputForward = 1.0;
    vacuumConfig.peakOutputReverse = 0.0;

    vacuum.configAllSettings(vacuumConfig);
    vacuum.enableCurrentLimit(true);
    vacuum.enableVoltageCompensation(true);
    logger.debug("configured vacuum talon");

    TelemetryService telemetryService = Robot.TELEMETRY;
    telemetryService.stop();
    telemetryService.register(new TalonItem(vacuum, "Vacuum"));
  }

  private void vacuumPreferences() {
    kBallPressureInHg = getPreference("ball_pressure_inHg", 13.17);
    kHatchPressureInHg = getPreference("hatch_pressure_inHg", 16);
    kClimbPressureInHg = getPreference("climb_pressure_inHg", 24.4);
    kGoodEnoughGamePiece = (int) getPreference("gamepiece_good_enough_inTicks", 80);
    kGoodEnoughClimb = (int) getPreference("climb_good_enough_inTicks", 200);
  }

  @SuppressWarnings("Duplicates")
  private double getPreference(String name, double defaultValue) {
    String prefName = PREFS_NAME + name;
    Preferences preferences = Preferences.getInstance();
    if (!preferences.containsKey(prefName)) {
      preferences.putDouble(prefName, defaultValue);
    }
    double pref = preferences.getDouble(prefName, BACKUP);
    logger.info("{}={}", name, pref);
    return pref;
  }

  public List<TalonSRX> getTalons() {
    return List.of(vacuum);
  }

  public Solenoid getTridentSolenoid() {
    return tridentSolenoid;
  }

  public Solenoid getClimbSolenoid() {
    return climbSolenoid;
  }

  public void setSolenoidsState(SolenoidStates state) {
    switch (state) {
      case CLIMB:
        climbSolenoid.set(true);
        tridentSolenoid.set(false);
        break;
      case STOP: // fall through
      case PRESSURE_ACCUMULATE:
        climbSolenoid.set(false);
        tridentSolenoid.set(false);
        break;
      case COOL_DOWN: // fall through
      case GAME_PIECE_PICKUP:
        tridentSolenoid.set(true);
        climbSolenoid.set(false);
        break;
      default:
        logger.warn("could not set to {}", state);
        break;
    }
    logger.info("set state to {}", state);
  }

  public void runOpenLoop(double setpoint) {
    vacuum.set(ControlMode.PercentOutput, setpoint);
    logger.info("set vacuum at {}", setpoint);
  }

  public void dump() {
    logger.info(
        "vacuum pressure in inHg = {} counts = {}",
        getPressure(),
        vacuum.getSelectedSensorPosition());
  }

  public double getPressure() {
    return ((vacuum.getSelectedSensorPosition() - COUNTS_OFFSET) / COUNTS_PER_INHG);
  }

  public void setPressure(double pressure) {
    SmartDashboard.putBoolean("Game/onTarget", false);
    setpoint = (int) (COUNTS_PER_INHG * pressure + COUNTS_OFFSET);
    logger.info("setting pressure to {}", setpoint);
    vacuum.set(ControlMode.Position, setpoint);
  }

  public void stop() {
    logger.info("stop pump");
    SmartDashboard.putBoolean("Game/onTarget", false);
    vacuum.set(ControlMode.Position, 0);
  }

  @Override
  protected void initDefaultCommand() {}

  @Override
  public void periodic() {
    if (!Robot.isEvent()) SmartDashboard.putNumber("Game/Temperature", getPumpTemperature());
    if (getPumpTemperature() > TEMP_LIMIT) {
      logger.error("Vacuum overheating!");
      setPeakOutput(0);
    }
  }

  public boolean onTarget() {
    double error = Math.abs(vacuum.getSelectedSensorPosition() - setpoint);

    if (error > kGoodEnoughGamePiece) {
      stableCount = 0;
    } else {
      stableCount++;
    }
    if (stableCount > STABLE_THRESHOLD) {
      SmartDashboard.putBoolean("Game/onTarget", true);
      return true;
    } else {
      SmartDashboard.putBoolean("Game/onTarget", false);
    }
    return false;
  }

  // FIXME
  public boolean isClimbOnTarget() {
    if (vacuum.getSelectedSensorPosition() >= kGoodEnoughClimb) {
      climbStableCounts++;
    } else {
      climbStableCounts = 0;
    }

    if (climbStableCounts > STABLE_THRESHOLD) {
      SmartDashboard.putBoolean("Game/climbOnTarget", true);
      return true;
    } else {
      SmartDashboard.putBoolean("Game/climbOnTarget", false);
    }

    return false;
  }

  public double getPumpTemperature() {
    return (analogInput.getVoltage() - TEMP_OFFSET) / VOLTS_PER_CELSIUS;
  }

  public void setPeakOutput(double peakOutput) {
    vacuum.configPeakOutputForward(peakOutput, 0);
  }

  public enum Valve {
    TRIDENT(0),
    CLIMB(2);

    public final int ID;

    Valve(int id) {
      this.ID = id;
    }
  }

  public enum SolenoidStates {
    PRESSURE_ACCUMULATE,
    CLIMB,
    GAME_PIECE_PICKUP,
    STOP,
    COOL_DOWN
  }
}
