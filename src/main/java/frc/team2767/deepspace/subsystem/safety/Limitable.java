package frc.team2767.deepspace.subsystem.safety;

public interface Limitable {

  int getTicks();

  void setLimits(int forward, int reverse);
}
