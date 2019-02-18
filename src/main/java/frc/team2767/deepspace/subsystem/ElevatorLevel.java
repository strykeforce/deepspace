package frc.team2767.deepspace.subsystem;

public enum ElevatorLevel {
  ONE(1),
  TWO(2),
  THREE(3),
  NOTSET(-1);

  private int id;

  ElevatorLevel(int id) {
    this.id = id;
  }
}
