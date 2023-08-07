package solux.baco.service.RouteModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LngLatPosition {
    private List<Double> location; //[double,double]형태

    public List<Double> getLocation() {
        return location;
    }

    public void setLocation(List<Double> location) {
        this.location = location;
    }


    //public Double Lng = location.get(0);
    public Double getLng() {
        return location.get(0);
    }

    //public Double Lat = location.get(1);
    public Double getLat() {
        return location.get(1);
    }
}