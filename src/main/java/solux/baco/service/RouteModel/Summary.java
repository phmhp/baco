package solux.baco.service.RouteModel;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Summary {
    private LngLatPosition start;
    private LngLatPosition goal;
    private int distance;

    public LngLatPosition getStart() {
        return start;
    }

    public void setStart(LngLatPosition start) {
        this.start = start;
    }

    public LngLatPosition getGoal() {
        return goal;
    }

    public void setGoal(LngLatPosition goal) {
        this.goal = goal;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}