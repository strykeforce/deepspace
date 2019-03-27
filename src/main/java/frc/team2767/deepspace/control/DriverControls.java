package frc.team2767.deepspace.control;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.command.ZeroGyroCommand;
import frc.team2767.deepspace.command.approach.sequences.AutoHatchPickupCommandGroup;
import frc.team2767.deepspace.command.approach.sequences.HatchPlaceCommandGroup;
import frc.team2767.deepspace.command.biscuit.BiscuitNegativeCommand;
import frc.team2767.deepspace.command.biscuit.BiscuitPositiveCommand;
import frc.team2767.deepspace.command.biscuit.BiscuitStopCommand;
import frc.team2767.deepspace.command.climb.ClimbJogCommand;
import frc.team2767.deepspace.command.climb.StopClimbCommand;
import frc.team2767.deepspace.command.intake.IntakeDownCommand;
import frc.team2767.deepspace.command.intake.IntakeUpCommand;
import frc.team2767.deepspace.command.intake.ShoulderStopCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.log.SafetyLogDumpCommand;
import frc.team2767.deepspace.command.sequences.StowAllCommandGroup;
import frc.team2767.deepspace.command.sequences.pickup.CargoGroundPickupCommandGroup;
import frc.team2767.deepspace.command.sequences.pickup.CoconutPickupCommandGroup;
import frc.team2767.deepspace.command.teleop.InterruptCommand;
import frc.team2767.deepspace.command.vacuum.SetSolenoidStatesCommand;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This assumes the use of an Interlink X Flight Simulator controller. */
@SuppressWarnings("unused")
public class DriverControls {

  private final Joystick joystick;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  DriverControls(int port) {
    joystick = new Joystick(port);

    // intake pickup
    new JoystickButton(joystick, Shoulder.RIGHT_DOWN.id)
        .whenPressed(new CargoGroundPickupCommandGroup());
    new JoystickButton(joystick, Shoulder.RIGHT_DOWN.id)
        .whenReleased(new CoconutPickupCommandGroup());

    new JoystickButton(joystick, Button.X.id).whenPressed(new StowAllCommandGroup());

    // Climb Commands
    new JoystickButton(joystick, Button.UP.id)
        .whenPressed(new ClimbJogCommand(ClimbSubsystem.kJogUpVelocity));
    new JoystickButton(joystick, Button.UP.id).whenReleased(new StopClimbCommand());
    new JoystickButton(joystick, Button.DOWN.id)
        .whenPressed(new ClimbJogCommand(ClimbSubsystem.kJogDownVelocity));
    new JoystickButton(joystick, Button.DOWN.id).whenReleased(new StopClimbCommand());

    // biscuit
    new JoystickButton(joystick, Trim.RIGHT_Y_POS.id)
        .whenPressed(new AutoHatchPickupCommandGroup());
    new JoystickButton(joystick, Trim.RIGHT_Y_NEG.id)
        .whenPressed(new AutoHatchPickupCommandGroup());

    // interrupt
    new JoystickButton(joystick, Trim.LEFT_Y_NEG.id).whenPressed(new InterruptCommand());
    new JoystickButton(joystick, Trim.LEFT_Y_POS.id).whenPressed(new InterruptCommand());

    // biscuit
    new JoystickButton(joystick, Trim.LEFT_X_POS.id).whenPressed(new BiscuitPositiveCommand());
    new JoystickButton(joystick, Trim.LEFT_X_POS.id).whenReleased(new BiscuitStopCommand());
    new JoystickButton(joystick, Trim.LEFT_X_NEG.id).whenPressed(new BiscuitNegativeCommand());
    new JoystickButton(joystick, Trim.LEFT_X_NEG.id).whenReleased(new BiscuitStopCommand());

    // intake
    new JoystickButton(joystick, Trim.RIGHT_X_POS.id).whenPressed(new IntakeUpCommand());
    new JoystickButton(joystick, Trim.RIGHT_X_NEG.id).whenPressed(new IntakeDownCommand());
    new JoystickButton(joystick, Trim.RIGHT_X_POS.id).whenReleased(new ShoulderStopCommand());
    new JoystickButton(joystick, Trim.RIGHT_X_NEG.id).whenReleased(new ShoulderStopCommand());

    // ZEROS / LOG DUMPS
    new JoystickButton(joystick, Button.RESET.id).whenPressed(new ZeroGyroCommand());
    new JoystickButton(joystick, Button.HAMBURGER.id).whenPressed(new SafetyLogDumpCommand());

    // shoulder
    new JoystickButton(joystick, Shoulder.LEFT_DOWN.id)
        .whenPressed(
            new SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates.PRESSURE_ACCUMULATE));
    new JoystickButton(joystick, Shoulder.LEFT_UP.id).whenPressed(new HatchPlaceCommandGroup());
  }

  private <E extends Enum<E>> Command log(E control) {
    return new LogCommand(logger, control.toString());
  }

  /** Left stick X (up-down) axis. */
  public double getForward() {
    return -joystick.getRawAxis(Axis.LEFT_X.id);
  }

  /** Left stick Y (left-right) axis. */
  public double getStrafe() {
    return joystick.getRawAxis(Axis.LEFT_Y.id);
  }

  /** Right stick Y (left-right) axis. */
  public double getYaw() {
    return joystick.getRawAxis(Axis.RIGHT_Y.id);
  }

  /** Tuner knob. */
  public double getTuner() {
    return joystick.getRawAxis(Axis.TUNER.id);
  }

  /** Left slider on back of controller. */
  public double getLeftBackAxis() {
    return joystick.getRawAxis(Axis.LEFT_BACK.id);
  }

  /** Right slider on back of controller. */
  public double getRightBackAxis() {
    return joystick.getRawAxis(Axis.RIGHT_BACK.id);
  }

  public boolean getToggle() {
    return joystick.getRawButtonPressed(Toggle.LEFT_TOGGLE.id);
  }

  public enum Axis {
    RIGHT_X(1),
    RIGHT_Y(0),
    LEFT_X(2),
    LEFT_Y(5),
    TUNER(6),
    LEFT_BACK(4),
    RIGHT_BACK(3);

    private final int id;

    Axis(int id) {
      this.id = id;
    }
  }

  public enum Shoulder {
    RIGHT_DOWN(2),
    LEFT_DOWN(4),
    LEFT_UP(5);

    private final int id;

    Shoulder(int id) {
      this.id = id;
    }
  }

  public enum Toggle {
    LEFT_TOGGLE(1);

    private final int id;

    Toggle(int id) {
      this.id = id;
    }
  }

  public enum Button {
    RESET(3),
    HAMBURGER(14),
    X(15),
    UP(16),
    DOWN(17);

    private final int id;

    Button(int id) {
      this.id = id;
    }
  }

  public enum Trim {
    LEFT_Y_POS(7),
    LEFT_Y_NEG(6),
    LEFT_X_POS(8),
    LEFT_X_NEG(9),
    RIGHT_X_POS(10),
    RIGHT_X_NEG(11),
    RIGHT_Y_POS(12),
    RIGHT_Y_NEG(13);

    private final int id;

    Trim(int id) {
      this.id = id;
    }
  }
}
