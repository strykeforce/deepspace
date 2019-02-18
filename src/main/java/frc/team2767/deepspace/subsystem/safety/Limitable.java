package frc.team2767.deepspace.subsystem.safety;

public interface Limitable {

  int getElevatorPosition();

  void setLimits(int forward, int reverse);
}
