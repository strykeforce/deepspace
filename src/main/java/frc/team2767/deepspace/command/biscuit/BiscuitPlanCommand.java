package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiscuitPlanCommand extends InstantCommand {

  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private BiscuitSubsystem.FieldDirection direction;

  public BiscuitPlanCommand(BiscuitSubsystem.FieldDirection direction) {
    this.direction = direction;
    requires(BISCUIT);
  }

  @Override
  protected void initialize() {
    logger.info("BiscuitPlan = {}", direction);
    BISCUIT.setPlan(BiscuitSubsystem.FieldDirection.PLACE_L, BiscuitSubsystem.Position.LEFT);
  }
}
