package cloudcode.helloworld.web;
import java.util.List;
/**
 * Response object for the NearbyStations endpoint.
 */
public class NearbyStationsResponse {

    private List<NearbyStation> nearbyStations;
  
    public NearbyStationsResponse() {}
  
    public NearbyStationsResponse(List<NearbyStation> nearbyStations) {
      this.nearbyStations = nearbyStations;
    }
  
    public List<NearbyStation> getNearbyStations() {
      return nearbyStations;
    }
  
    public void setNearbyStations(List<NearbyStation> nearbyStations) {
      this.nearbyStations = nearbyStations;
    }
  
  }