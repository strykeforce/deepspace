package frc.team2767.deepspace.command.health;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.health.HealthCheckSubsystem;

abstract class DeepspaceHealthCheck extends Command {

  final HealthCheckSubsystem HEALTHCHECK = Robot.HEALTHCHECK;

  DeepspaceHealthCheck() {
    requires(HEALTHCHECK);
    requires(Robot.DRIVE);
    requires(Robot.INTAKE);
    requires(Robot.ELEVATOR);
    requires(Robot.BISCUIT);
  }

  @Override
  protected void interrupted() {
    HEALTHCHECK.cancel();
  }
}
