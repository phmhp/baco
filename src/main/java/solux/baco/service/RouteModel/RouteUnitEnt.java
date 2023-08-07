package solux.baco.service.RouteModel;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RouteUnitEnt {
    private List<Trafast> trafast;

    public List<Trafast> getTrafast() {
        return trafast;
    }
}