package cloudcode.helloworld.web;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.google.cloud.bigquery.FieldValueList;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import cloudcode.helloworld.web.NearbyStation;
import cloudcode.helloworld.web.NearbyStationsResponse;

/**
 * A simple Spring Boot controller for handling web requests.
 */

/*
* The methods read from BigQuery. 
* Remember to replace the table name with your table where you have stored the distance data.
*/

@RestController
public final class HelloWorldController {

      
/*
 * This method is invoked as the app loads with the signin/signup fields.
 * 
*/
@GetMapping("/")
public ModelAndView home(ModelMap map) throws Exception {
  // Connect to BigQuery and write a select SQL to fetch the station names from the table `mdm_gemini.CITIBIKE_STATIONS_EMBEDDINGS` and column "Station_Name"
  String query = "SELECT distinct Station_Name FROM `mdm_gemini.CITIBIKE_STATIONS_DISTANCE`";

  BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
  QueryJobConfiguration queryConfig =
      QueryJobConfiguration.newBuilder(query)
          .setUseLegacySql(false)
          .build();

  // Create a job ID so that we can safely retry.
  JobId jobId = JobId.of(UUID.randomUUID().toString());
  Job queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

  // Wait for the query to complete.
  queryJob = queryJob.waitFor();

  // Check for errors
  if (queryJob == null) {
    throw new RuntimeException("Job no longer exists");
  } else if (queryJob.getStatus().getError() != null) {
    // You can also look at queryJob.getStatus().getExecutionErrors() for all
    // errors, not just the latest one.
    throw new RuntimeException(queryJob.getStatus().getError().toString());
  }

  // Get the results.
  TableResult result = queryJob.getQueryResults();
  List<String> stationNames = new ArrayList<>();
  // Iterate over the results and add the station names to the list
  for (FieldValueList row : result.iterateAll()) {
    stationNames.add(row.get("Station_Name").getStringValue());
  }

  // Add the station names to the dropdown using JavaScript
  String script =
      "var stationNameDropdown = document.getElementById('station-name');"
          + "stationNames.forEach(function(stationName) {"
          + "  var option = document.createElement('option');"
          + "  option.value = stationName;"
          + "  option.innerText = stationName;"
          + "  stationNameDropdown.appendChild(option);"
          + "});";

  map.addAttribute("script", script);
  map.addAttribute("stationNames", stationNames);

  return new ModelAndView("index", map);
}


@GetMapping("/station-details")
public @ResponseBody NearbyStation stationDetails(@RequestParam String stationName) throws Exception {
  // Validate the input
  if (stationName == null || stationName.isEmpty()) {
    throw new IllegalArgumentException("Station name cannot be null or empty");
  }
  System.out.println("STation Name: " + stationName + "\n");
  // Create a response object to return to the client
  NearbyStation response = new NearbyStation();
  // Connect to BigQuery and write a select SQL to fetch the nearby stations from the table `mdm_gemini.CITIBIKE_STATIONS_EMBEDDINGS` and column "Station_Name"
  String query = String.format("SELECT distinct Station_Name, replace(replace(Station_Address,'```json',''),'```','') as Station_Address FROM `mdm_gemini.CITIBIKE_STATIONS_DISTANCE` WHERE Station_Name = '" + stationName + "' ");

  BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
  QueryJobConfiguration queryConfig =
      QueryJobConfiguration.newBuilder(query)
          .setUseLegacySql(false)
          .build();

  // Create a job ID so that we can safely retry.
  JobId jobId = JobId.of(UUID.randomUUID().toString());
  Job queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

  // Wait for the query to complete.
  queryJob = queryJob.waitFor();

  // Check for errors
  if (queryJob == null) {
    throw new RuntimeException("Job no longer exists");
  } else if (queryJob.getStatus().getError() != null) {
    // You can also look at queryJob.getStatus().getExecutionErrors() for all
    // errors, not just the latest one.
    throw new RuntimeException(queryJob.getStatus().getError().toString());
  }

  // Get the results.
  TableResult result = queryJob.getQueryResults();
  List<NearbyStation> nearbyStations = new ArrayList<>();
  // Iterate over the results and add the nearby stations to the list
  for (FieldValueList row : result.iterateAll()) {
    nearbyStations.add(new NearbyStation(row.get("Station_Name").getStringValue(), row.get("Station_Address").getStringValue()));
    response.setStationName(row.get("Station_Name").getStringValue());
    response.setStationAddress(row.get("Station_Address").getStringValue());
    System.out.println("STation Name: " + row.get("Station_Name").getStringValue() + "\n");
    System.out.print("STation Address: " + row.get("Station_Address").getStringValue() + "\n");
  }
 // map.addAttribute("responseBody", response);

 return response;
}



@GetMapping("/nearby-stations")
public @ResponseBody NearbyStationsResponse nearbyStations(@RequestParam String stationName) throws Exception {
  // Validate the input
  if (stationName == null || stationName.isEmpty()) {
    throw new IllegalArgumentException("Station name cannot be null or empty");
  }
  System.out.println("STation Name: " + stationName + "\n");
  // Create a response object to return to the client
  NearbyStationsResponse response = new NearbyStationsResponse();
  // Connect to BigQuery and write a select SQL to fetch the nearby stations from the table `mdm_gemini.CITIBIKE_STATIONS_EMBEDDINGS` and column "Station_Name"
  String query = String.format("SELECT distinct Station_Name original_Station_Name, Station_Nearby Station_Name, Nearby_Station_Address Station_Address, distance FROM `mdm_gemini.CITIBIKE_STATIONS_DISTANCE` WHERE Station_Name = '" + stationName + "' ORDER BY distance desc limit 5");

  BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
  QueryJobConfiguration queryConfig =
      QueryJobConfiguration.newBuilder(query)
          .setUseLegacySql(false)
          .build();

  // Create a job ID so that we can safely retry.
  JobId jobId = JobId.of(UUID.randomUUID().toString());
  Job queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

  // Wait for the query to complete.
  queryJob = queryJob.waitFor();

  // Check for errors
  if (queryJob == null) {
    throw new RuntimeException("Job no longer exists");
  } else if (queryJob.getStatus().getError() != null) {
    // You can also look at queryJob.getStatus().getExecutionErrors() for all
    // errors, not just the latest one.
    throw new RuntimeException(queryJob.getStatus().getError().toString());
  }

  // Get the results.
  TableResult result = queryJob.getQueryResults();
  List<NearbyStation> nearbyStations = new ArrayList<>();
  // Iterate over the results and add the nearby stations to the list
  for (FieldValueList row : result.iterateAll()) {
    nearbyStations.add(new NearbyStation(row.get("Station_Name").getStringValue(), row.get("Station_Address").getStringValue()));
    System.out.println("STATIONNAME: " + row.get("Station_Name").getStringValue() + "\n");
    System.out.print("STATIONADDRESS: " + row.get("Station_Address").getStringValue() + "\n");
    System.out.print("DISTANCE: " + row.get("distance").getStringValue() + "\n"); 
  }
 // map.addAttribute("responseBody", response);
 
 return new NearbyStationsResponse(nearbyStations);
}

}
