package frc.team2767.deepspace.command;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;

import java.util.ArrayList;
import java.util.List;

public class AzimuthZeroPositionCommand extends InstantCommand {
  private final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final List<Integer> ZEROS = new ArrayList<>() {{
    add(0);
    add(0);
    add(0);
    add(0);
  }};

  public AzimuthZeroPositionCommand () {
    requires(DRIVE);
  }

  @Override
  protected void initialize() {
    DRIVE.setWheelAzimuthPosition(ZEROS);
  }
}
