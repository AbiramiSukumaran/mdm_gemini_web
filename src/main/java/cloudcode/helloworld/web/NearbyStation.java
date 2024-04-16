package cloudcode.helloworld.web;

import java.util.List;


// NearbyStation class represents a nearby station with its name and address.

public class NearbyStation {

  private String stationName;
  private String stationAddress;

  public NearbyStation() {}

  public NearbyStation(String stationName, String stationAddress) {
    this.stationName = stationName;
    this.stationAddress = stationAddress;
  }

  public String getStationName() {
    return stationName;
  }

  public void setStationName(String stationName) {
    this.stationName = stationName;
  }

  public String getStationAddress() {
    return stationAddress;
  }

  public void setStationAddress(String stationAddress) {
    this.stationAddress = stationAddress;
  }

}
