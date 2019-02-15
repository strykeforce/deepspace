package frc.team2767.deepspace.subsystem.safety;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VacuumSubsystem extends Subsystem {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final int VACUUM_ID = 60;
  private TalonSRX vacuum = new TalonSRX(VACUUM_ID);

  public VacuumSubsystem() {
    configTalon();
  }

  //        Count	  psi	  in Hg
  //        500	    6.4	  13
  //        600	    7.9	  16
  //        700	    9.3	  19
  //        800	    10.8	22
  //        900	    11.8	24

  @SuppressWarnings("Duplicates")
  private void configTalon() {
    // FIXME: set max fwd/rev voltage

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
  }

  @Override
  protected void initDefaultCommand() {}
}
