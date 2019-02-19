package frc.team2767.deepspace.subsystem;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Subsystem;

public class CoconutSubsystem extends Subsystem {

  private static final double OPEN_LEFT = 0.52;
  private static final double OPEN_RIGHT = 0.74;
  private static final double CLOSE_LEFT = 0.6;
  private static final double CLOSE_RIGHT = 0.62;

  private final Servo leftServo = new Servo(0);
  private final Servo rightServo = new Servo(1);

  public CoconutSubsystem() {
    open();
  }

  public void open() {
    leftServo.set(OPEN_LEFT);
    rightServo.set(OPEN_RIGHT);
  }

  public void close() {
    leftServo.set(CLOSE_LEFT);
    rightServo.set(CLOSE_RIGHT);
  }

  @Override
  protected void initDefaultCommand() {}
}
