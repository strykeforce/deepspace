package frc.team2767.deepspace.control;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.command.ZeroGyroCommand;
import frc.team2767.deepspace.command.biscuit.BiscuitNegativeCommand;
import frc.team2767.deepspace.command.biscuit.BiscuitPositionCommand;
import frc.team2767.deepspace.command.biscuit.BiscuitPositiveCommand;
import frc.team2767.deepspace.command.biscuit.BiscuitStopCommand;
import frc.team2767.deepspace.command.deliver.DeliverCommandGroup;
import frc.team2767.deepspace.command.elevator.ElevatorOpenLoopDownCommand;
import frc.team2767.deepspace.command.elevator.ElevatorOpenLoopUpCommand;
import frc.team2767.deepspace.command.elevator.ElevatorStopCommand;
import frc.team2767.deepspace.command.intake.IntakeDownCommand;
import frc.team2767.deepspace.command.intake.IntakeStopCommand;
import frc.team2767.deepspace.command.intake.IntakeUpCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.log.SafetyLogDumpCommand;
import frc.team2767.deepspace.command.vision.LightsOffCommand;
import frc.team2767.deepspace.command.vision.LightsOnCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This assumes the use of an Interlink X Flight Simulator controller. */
@SuppressWarnings("unused")
public class DriverControls {

  private final Joystick joystick;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  DriverControls(int port) {
    joystick = new Joystick(port);

    // biscuit
    new JoystickButton(joystick, Trim.RIGHT_Y_POS.id).whenActive(new BiscuitPositiveCommand());
    new JoystickButton(joystick, Trim.RIGHT_Y_POS.id).whenInactive(new BiscuitStopCommand());
    new JoystickButton(joystick, Trim.RIGHT_Y_NEG.id).whenActive(new BiscuitNegativeCommand());
    new JoystickButton(joystick, Trim.RIGHT_Y_NEG.id).whenInactive(new BiscuitStopCommand());

        new JoystickButton(joystick, Shoulder.RIGHT_DOWN.id).whenPressed(new DeliverCommandGroup());

    //     elevator
    new JoystickButton(joystick, Trim.LEFT_Y_POS.id).whenActive(new ElevatorOpenLoopUpCommand());
    new JoystickButton(joystick, Trim.LEFT_Y_POS.id).whenInactive(new ElevatorStopCommand());
    new JoystickButton(joystick, Trim.LEFT_Y_NEG.id).whenInactive(new ElevatorStopCommand());
    new JoystickButton(joystick, Trim.LEFT_Y_NEG.id).whenActive(new ElevatorOpenLoopDownCommand());

    // ZEROS / LOG DUMPS
    new JoystickButton(joystick, Button.RESET.id).whenPressed(new ZeroGyroCommand());
    new JoystickButton(joystick, Button.HAMBURGER.id).whenPressed(new SafetyLogDumpCommand());
    new JoystickButton(joystick, Button.X.id).whenPressed(new BiscuitPositionCommand());

    // LIGHTS
    new JoystickButton(joystick, Trim.LEFT_X_POS.id).whenPressed(new LightsOnCommand());
    new JoystickButton(joystick, Trim.LEFT_X_NEG.id).whenPressed(new LightsOffCommand());

    // shoulder
    new JoystickButton(joystick, Shoulder.LEFT_DOWN.id).whenPressed(new IntakeDownCommand());
    new JoystickButton(joystick, Shoulder.LEFT_UP.id).whenPressed(new IntakeUpCommand());
    new JoystickButton(joystick, Shoulder.LEFT_DOWN.id).whenReleased(new IntakeStopCommand());
    new JoystickButton(joystick, Shoulder.LEFT_UP.id).whenReleased(new IntakeStopCommand());
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
