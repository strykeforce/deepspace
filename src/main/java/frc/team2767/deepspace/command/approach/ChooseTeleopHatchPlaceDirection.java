package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.command.states.SetFieldDirectionCommand;
import frc.team2767.deepspace.subsystem.FieldDirection;

public class ChooseTeleopHatchPlaceDirection extends ConditionalCommand {

  public ChooseTeleopHatchPlaceDirection() {
    super(
        new SetFieldDirectionCommand(FieldDirection.LEFT),
        new SetFieldDirectionCommand(FieldDirection.RIGHT));
  }

  @Override
  protected boolean condition() {
    return true;
  }
}
