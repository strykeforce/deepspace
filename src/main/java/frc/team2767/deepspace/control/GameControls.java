package frc.team2767.deepspace.control;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.command.ZeroAxisCommand;
import frc.team2767.deepspace.command.elevator.ElevatorOpenLoopDownCommand;
import frc.team2767.deepspace.command.elevator.ElevatorOpenLoopUpCommand;
import frc.team2767.deepspace.command.elevator.ElevatorStopCommand;
import frc.team2767.deepspace.command.intake.RollerOutCommand;
import frc.team2767.deepspace.command.intake.RollerStopCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.sequences.CoconutPickupCommandGroup;
import frc.team2767.deepspace.command.sequences.PlayerCargoCommandGroup;
import frc.team2767.deepspace.command.sequences.PlayerHatchCommandGroup;
import frc.team2767.deepspace.command.sequences.StowAllCommandGroup;
import frc.team2767.deepspace.command.states.SetFieldDirectionCommand;
import frc.team2767.deepspace.command.states.SetLevelCommand;
import frc.team2767.deepspace.command.vacuum.ActivateValveCommand;
import frc.team2767.deepspace.command.vacuum.DeactivateValveCommand;
import frc.team2767.deepspace.command.vision.SelectCameraCommand;
import frc.team2767.deepspace.control.trigger.*;
import frc.team2767.deepspace.subsystem.ElevatorLevel;
import frc.team2767.deepspace.subsystem.FieldDirection;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This assumes the use of a Logitech F310 controller. */
@SuppressWarnings("unused")
public class GameControls {

  private final Joystick joystick;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public GameControls(int port) {
    joystick = new Joystick(port);

    new JoystickButton(joystick, Button.START.id).whenPressed(new ZeroAxisCommand());

    DirectionPadAny directionPadAny = new DirectionPadAny(this);

    RightStickDown rightStickDown = new RightStickDown(this);
    RightStickUp rightStickUp = new RightStickUp(this);

    LeftStickLeft leftStickLeft = new LeftStickLeft(this);
    LeftStickRight leftStickRight = new LeftStickRight(this);

    directionPadAny.whenActive(new RollerOutCommand());
    directionPadAny.whenInactive(new RollerStopCommand());

    new JoystickButton(joystick, Trigger.LEFT.id)
        .whenPressed(new DeactivateValveCommand(VacuumSubsystem.Valve.TRIDENT));
    //
    // COMP CONFIG DO NOT CHANGE
    //

    // CLIMB
    new JoystickButton(joystick, GameControls.Button.BACK.id)
        .whenPressed(new SelectCameraCommand());
    //    new JoystickButton(joystick, GameControls.Button.BACK.id)
    //            .whenPressed(new DeployClimberCommand());
    //    new JoystickButton(joystick, Button.START.id).whenPressed(new RunClimb());

    // ELEVATOR

    new JoystickButton(joystick, GameControls.Button.X.id).whenPressed(new StowAllCommandGroup());
    new JoystickButton(joystick, GameControls.Button.Y.id)
        .whenPressed(new SetLevelCommand(ElevatorLevel.THREE));
    new JoystickButton(joystick, GameControls.Button.B.id)
        .whenPressed(new SetLevelCommand(ElevatorLevel.TWO));
    new JoystickButton(joystick, GameControls.Button.A.id)
        .whenPressed(new SetLevelCommand(ElevatorLevel.ONE));

    rightStickUp.whenActive(new ElevatorOpenLoopUpCommand());
    rightStickUp.whenInactive(new ElevatorStopCommand());
    rightStickDown.whenInactive(new ElevatorStopCommand());
    rightStickDown.whenActive(new ElevatorOpenLoopDownCommand());

    // FIELD DIRECTION STATE
    leftStickLeft.whenActive(new SetFieldDirectionCommand(FieldDirection.LEFT));
    leftStickRight.whenActive(new SetFieldDirectionCommand(FieldDirection.RIGHT));
    //
    //    // LOADING
    new JoystickButton(joystick, GameControls.Shoulder.RIGHT.id)
        .whenPressed(new PlayerCargoCommandGroup());
    new JoystickButton(joystick, GameControls.Shoulder.RIGHT.id)
        .whenReleased(new CoconutPickupCommandGroup());
    new JoystickButton(joystick, GameControls.Shoulder.LEFT.id)
        .whenPressed(new PlayerHatchCommandGroup());
    new JoystickButton(joystick, Shoulder.LEFT.id)
        .whenReleased(new ActivateValveCommand(VacuumSubsystem.Valve.TRIDENT));

    //
    // END COMP CONFIG
    //
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
