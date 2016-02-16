package actors;

public class Snapshot {
  
  int    time;
  Object value;

  public Snapshot(int time, Object value) {
    super();
    this.time = time;
    this.value = value;
  }

  public String toString() {
    return value + "@" + time;
  }

}
