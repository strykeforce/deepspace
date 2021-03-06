package frc.team2767.deepspace.command.intake;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class WaitForIntakeBeamCommand extends Command {
  private static final double SLOWER_WAIT_TIME_MS = 500;
  private static final IntakeSubsystem INTAKE = Robot.INTAKE;
  private static final double SLOW_SPEED = 0.5;
  private static final double SLOWER_SPEED = 0.3;
  private double breakTime;
  private boolean hasBroken;
  private State state;

  @Override
  protected void initialize() {
    state = State.WAIT_FOR_BEAM;
    hasBroken = false;
  }

  @Override
  protected void execute() {
    switch (state) {
      case WAIT_FOR_BEAM:
        if (INTAKE.isIntakeSlowBeamBroken()) {
          INTAKE.rollerOpenLoop(SLOW_SPEED);
          INTAKE.setPosition(105);
          state = State.SLOW;
          hasBroken = true;
        }
        break;
      case SLOW:
        if (hasBroken && !INTAKE.isIntakeSlowBeamBroken()) {
          INTAKE.rollerOpenLoop(SLOWER_SPEED);
          state = State.SLOWER;
          breakTime = System.currentTimeMillis();
        }
        break;
      case SLOWER:
        if (System.currentTimeMillis() - breakTime > SLOWER_WAIT_TIME_MS) {
          state = State.DONE;
          INTAKE.rollerOpenLoop(0.15);
        }
        break;
      case DONE:
        break;
    }
  }

  @Override
  protected boolean isFinished() {
    return state == State.DONE;
  }

  @Override
  protected void end() {}

  private enum State {
    WAIT_FOR_BEAM,
    SLOW,
    SLOWER,
    DONE,
  }
}
