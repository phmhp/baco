package solux.baco.service.RouteModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Trafast {
    private Summary summary;
    private List<List<Double>> path;

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public List<List<Double>> getPath() {
        return path;
    }

    public void setPath(List<List<Double>> path) {
        this.path = path;
    }
}
