package frc.team2767.deepspace.control;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.command.LogCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This assumes the use of a Logitech F310 controller. */
@SuppressWarnings("unused")
public class GameControls {

  private final Joystick joystick;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public GameControls(int port) {
    joystick = new Joystick(port);
  }

  private <E extends Enum<E>> Command log(E control) {
    return new LogCommand(logger, control.toString());
  }

  public enum Axis {
    LEFT_X(0),
    LEFT_Y(0),
    RIGHT_X(0),
    RIGHT_Y(0),
    TUNER(0),
    LEFT_TRIGGER(0),
    RIGHT_TRIGGER(0);

    private final int id;

    Axis(int id) {
      this.id = id;
    }
  }

  public enum Shoulder {
    LEFT(0),
    RIGHT(0);

    private final int id;

    Shoulder(int id) {
      this.id = id;
    }
  }

  public enum Button {
    A(0),
    B(0),
    X(0),
    Y(0),
    START(0),
    BACK(0);

    private final int id;

    Button(int id) {
      this.id = id;
    }
  }

  public enum DPad {
    UP(0),
    DOWN(0),
    LEFT(0),
    RIGHT(0);

    private final int id;

    DPad(int id) {
      this.id = id;
    }
  }
}
