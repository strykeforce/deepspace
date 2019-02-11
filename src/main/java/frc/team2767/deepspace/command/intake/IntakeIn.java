package frc.team2767.deepspace.command.intake;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.IntakeSubsystem;

public class IntakeIn extends InstantCommand {
  IntakeSubsystem intakeSubsystem = Robot.IntakeSubsystem;

  IntakeIn() {
    requires(intakeSubsystem);
  }

  @Override
  protected void initialize() {
    intakeSubsystem.setRollerOutput(ControlMode.PercentOutput, 0.2);
  }
}
