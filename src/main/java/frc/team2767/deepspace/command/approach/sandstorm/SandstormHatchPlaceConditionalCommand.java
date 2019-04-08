package frc.team2767.deepspace.command.approach.sandstorm;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.command.approach.sequences.AutoHatchPlaceCommandGroup;
import frc.team2767.deepspace.command.teleop.DriverPlaceAssistCommand;

public class SandstormHatchPlaceConditionalCommand extends ConditionalCommand {

  public SandstormHatchPlaceConditionalCommand() {
    super(new AutoHatchPlaceCommandGroup(), new DriverPlaceAssistCommand());
  }

  @Override
  protected boolean condition() {
    return DriverStation.getInstance().isAutonomous();
  }
}
