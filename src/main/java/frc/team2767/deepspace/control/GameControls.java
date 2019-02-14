package frc.team2767.deepspace.control;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.command.biscuit.BiscuitZeroCommand;
import frc.team2767.deepspace.command.elevator.*;
import frc.team2767.deepspace.command.intake.IntakeZeroCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This assumes the use of a Logitech F310 controller. */
@SuppressWarnings("unused")
public class GameControls {

  private final Joystick joystick;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public GameControls(int port) {
    joystick = new Joystick(port);

    new JoystickButton(joystick, GameControls.Button.X.id).whenPressed(new ElevatorZeroCommand());
    new JoystickButton(joystick, GameControls.Button.Y.id).whenPressed(new BiscuitZeroCommand());
    new JoystickButton(joystick, GameControls.Button.B.id).whenPressed(new IntakeZeroCommand());

//    // Shoulder
//    new JoystickButton(joystick, GameControls.Shoulder.LEFT.id)
//        .whenPressed(log(GameControls.Shoulder.LEFT));
//    new JoystickButton(joystick, GameControls.Shoulder.RIGHT.id)
//        .whenPressed(new ElevatorStopCommand());
//
//    // Triggers
//    new JoystickButton(joystick, GameControls.Trigger.LEFT.id)
//        .whenPressed(log(GameControls.Trigger.LEFT));
//    new JoystickButton(joystick, GameControls.Trigger.RIGHT.id)
//        .whenPressed(log(GameControls.Trigger.RIGHT));
//
//    // Push-buttons
//    new JoystickButton(joystick, GameControls.Button.A.id)
//        .whenPressed(new ElevatorOpenLoopDownCommand());
//    new JoystickButton(joystick, GameControls.Button.B.id)
//        .whenPressed(new ElevatorPositionCommand(ElevatorSubsystem.Position.STOW));
//
//    new JoystickButton(joystick, GameControls.Button.START.id)
//        .whenPressed(log(GameControls.Button.START));
//    new JoystickButton(joystick, GameControls.Button.BACK.id)
//        .whenPressed(log(GameControls.Button.BACK));
//    new JoystickButton(joystick, GameControls.Button.LEFT.id)
//        .whenPressed(log(GameControls.Button.LEFT));
//    new JoystickButton(joystick, GameControls.Button.RIGHT.id)
//        .whenPressed(log(GameControls.Button.RIGHT));
  }

  private <E extends Enum<E>> Command log(E control) {
    return new LogCommand(logger, control.toString());
  }

  /** Left stick Y (up-down) axis. */
  public double getLY() {
    return -joystick.getRawAxis(Axis.LEFT_Y.id);
  }

  /** Left stick X (left-right) axis. */
  public double getLX() {
    return joystick.getRawAxis(Axis.LEFT_X.id);
  }

  /** Right stick Y (up-down) axis. */
  public double getRY() {
    return -joystick.getRawAxis(Axis.RIGHT_Y.id);
  }

  /** Right stick X (left-right) axis. */
  public double getRX() {
    return joystick.getRawAxis(Axis.RIGHT_X.id);
  }

  /** D-pad axis. */
  public int getDPad() {
    return joystick.getPOV();
  }

  public enum Axis {
    LEFT_X(0),
    LEFT_Y(1),
    RIGHT_X(2),
    RIGHT_Y(3);

    private final int id;

    Axis(int id) {
      this.id = id;
    }
  }

  public enum Shoulder {
    LEFT(5),
    RIGHT(6);

    private final int id;

    Shoulder(int id) {
      this.id = id;
    }
  }

  public enum Trigger {
    LEFT(7),
    RIGHT(8);

    private final int id;

    Trigger(int id) {
      this.id = id;
    }
  }

  public enum Button {
    A(2),
    B(3),
    X(1),
    Y(4),
    START(10),
    BACK(9),
    LEFT(11),
    RIGHT(12);

    private final int id;

    Button(int id) {
      this.id = id;
    }
  }
}
