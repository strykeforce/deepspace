package frc.team2767.deepspace.control;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.command.log.LogCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XboxControls {

  private final Joystick xbox;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public XboxControls(int port) {
    xbox = new Joystick(port);

    // Buttons
    new JoystickButton(xbox, Button.A.id).whenPressed(log(Button.A));
    new JoystickButton(xbox, Button.B.id).whenPressed(log(Button.B));
    new JoystickButton(xbox, Button.X.id).whenPressed(log(Button.X));
    new JoystickButton(xbox, Button.Y.id).whenPressed(log(Button.Y));
    new JoystickButton(xbox, Button.START.id).whenPressed(log(Button.START));
    new JoystickButton(xbox, Button.BACK.id).whenPressed(log(Button.BACK));
    new JoystickButton(xbox, Button.LEFT_STICK.id).whenPressed(log(Button.LEFT_STICK));
    new JoystickButton(xbox, Button.RIGHT_STICK.id).whenPressed(log(Button.RIGHT_STICK));

    // Shoulders
    new JoystickButton(xbox, Shoulder.LEFT.id).whenPressed(log(Shoulder.LEFT));
    new JoystickButton(xbox, Shoulder.RIGHT.id).whenPressed(log(Shoulder.RIGHT));

    // Triggers
    new LeftTrigger(this).whenActive(log(Axis.LEFT_TRIGGER));
    new RightTrigger(this).whenActive(log(Axis.RIGHT_TRIGGER));

    // Dpad
    new DpadLeft(this).whenActive(new LogCommand(logger, "DPAD kLeft"));
    new DpadRight(this).whenActive(new LogCommand(logger, "DPAD kRight"));
    new DpadUp(this).whenActive(new LogCommand(logger, "DPAD kUp"));
    new DpadDown(this).whenActive(new LogCommand(logger, "DPAD kDown"));
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

  private class DpadLeft extends Trigger {
    private XboxControls xbox;

    public DpadLeft(XboxControls xbox) {
      this.xbox = xbox;
    }

    @Override
    public boolean get() {
      return xbox.getDpad() == 270;
    }
  }

  private class DpadRight extends Trigger {
    private XboxControls xbox;

    public DpadRight(XboxControls xbox) {
      this.xbox = xbox;
    }

    @Override
    public boolean get() {
      return xbox.getDpad() == 90;
    }
  }

  private class DpadUp extends Trigger {
    private XboxControls xbox;

    public DpadUp(XboxControls xbox) {
      this.xbox = xbox;
    }

    @Override
    public boolean get() {
      return xbox.getDpad() == 0;
    }
  }

  private class DpadDown extends Trigger {
    private XboxControls xbox;

    public DpadDown(XboxControls xbox) {
      this.xbox = xbox;
    }

    @Override
    public boolean get() {
      return xbox.getDpad() == 180;
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
