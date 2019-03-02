package frc.team2767.deepspace.control;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.command.Command;
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
import frc.team2767.deepspace.control.trigger.*;
import frc.team2767.deepspace.subsystem.ElevatorLevel;
import frc.team2767.deepspace.subsystem.FieldDirection;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XboxControls {

  private final Joystick xbox;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public XboxControls(int port) {
    xbox = new Joystick(port);

    Trigger directionPadAny =
        new Trigger() {
          @Override
          public boolean get() {
            return xbox.getPOV(Dpad.Dpad.id) != -1;
          }
        };

    Trigger RightStickUp =
        new Trigger() {
          @Override
          public boolean get() {
            return xbox.getRawAxis(Axis.RIGHT_Y.id) < -0.1;
          }
        };

    Trigger RightStickDown =
        new Trigger() {
          @Override
          public boolean get() {
            return xbox.getRawAxis(Axis.RIGHT_Y.id) > 0.1;
          }
        };

    Trigger LeftStickRight =
        new Trigger() {
          @Override
          public boolean get() {
            return xbox.getRawAxis(Axis.LEFT_X.id) > 0.1;
          }
        };

    Trigger LeftStickLeft =
        new Trigger() {
          @Override
          public boolean get() {
            return xbox.getRawAxis(Axis.LEFT_X.id) < -0.1;
          }
        };

    // ELEVATOR
    new JoystickButton(xbox, Button.A.id).whenPressed(new SetLevelCommand(ElevatorLevel.ONE));
    new JoystickButton(xbox, Button.B.id).whenPressed(new SetLevelCommand(ElevatorLevel.TWO));
    new JoystickButton(xbox, Button.X.id).whenPressed(new StowAllCommandGroup());
    new JoystickButton(xbox, Button.Y.id).whenPressed(new SetLevelCommand(ElevatorLevel.THREE));

    RightStickUp.whenActive(new ElevatorOpenLoopUpCommand());
    RightStickUp.whenInactive(new ElevatorStopCommand());
    RightStickDown.whenActive(new ElevatorOpenLoopDownCommand());
    RightStickDown.whenInactive(new ElevatorStopCommand());

    // FIELD DIRECTION STATE
    LeftStickLeft.whenActive(new SetFieldDirectionCommand(FieldDirection.LEFT));
    LeftStickRight.whenActive(new SetFieldDirectionCommand(FieldDirection.RIGHT));

    // Shoulders
    new JoystickButton(xbox, XboxControls.Shoulder.RIGHT.id)
        .whenPressed(new PlayerCargoCommandGroup());
    new JoystickButton(xbox, XboxControls.Shoulder.RIGHT.id)
        .whenReleased(new CoconutPickupCommandGroup());
    new JoystickButton(xbox, XboxControls.Shoulder.LEFT.id)
        .whenPressed(new PlayerHatchCommandGroup());
    new JoystickButton(xbox, XboxControls.Shoulder.LEFT.id)
        .whenReleased(new ActivateValveCommand(VacuumSubsystem.Valve.TRIDENT));

    // Dpad
    directionPadAny.whenActive(new RollerOutCommand());
    directionPadAny.whenInactive(new RollerStopCommand());
  }

  public double getLX() {
    return xbox.getRawAxis(Axis.LEFT_X.id);
  }

  public double getLY() {
    return xbox.getRawAxis(Axis.LEFT_Y.id);
  }

  public double getRX() {
    return xbox.getRawAxis(Axis.RIGHT_X.id);
  }

  public double getRY() {
    return xbox.getRawAxis(Axis.RIGHT_Y.id);
  }

  public double getLTrig() {
    return xbox.getRawAxis(Axis.LEFT_TRIGGER.id);
  }

  public double getRTrig() {
    return xbox.getRawAxis(Axis.RIGHT_TRIGGER.id);
  }

  public double getDpad() {
    return xbox.getPOV(Dpad.Dpad.id);
  }

  private <E extends Enum<E>> Command log(E control) {
    return new LogCommand(logger, control.toString());
  }

  public enum Button {
    A(1),
    B(2),
    X(3),
    Y(4),
    BACK(7),
    START(8),
    LEFT_STICK(9),
    RIGHT_STICK(10);

    private final int id;

    Button(int id) {
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

  public enum Axis {
    LEFT_X(0),
    LEFT_Y(1),
    LEFT_TRIGGER(2),
    RIGHT_TRIGGER(3),
    RIGHT_X(4),
    RIGHT_Y(5);

    private final int id;

    Axis(int id) {
      this.id = id;
    }
  }

  public enum Dpad {
    Dpad(0);

    private final int id;

    Dpad(int id) {
      this.id = id;
    }
  }

  private class LeftTrigger extends Trigger {
    private XboxControls xbox;

    public LeftTrigger(XboxControls xbox) {
      this.xbox = xbox;
    }

    @Override
    public boolean get() {
      return xbox.getLTrig() > 0;
    }
  }

  private class RightTrigger extends Trigger {
    private XboxControls xbox;

    public RightTrigger(XboxControls xbox) {
      this.xbox = xbox;
    }

    @Override
    public boolean get() {
      return xbox.getRTrig() > 0;
    }
  }
}
