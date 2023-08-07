package solux.baco.service.RouteModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@JsonIgnoreProperties(ignoreUnknown = true) //선택적으로 필요한 값만 선택하기 위해 정의되지않은 내용은 무시.
public class JsonDataEntity {


    public List<List<Double>> getRoutePoint() {
        return routePoint;
    }

    public void setRoutePoint(List<List<Double>> routePoint) {
        this.routePoint = routePoint;
    }

    private List<List<Double>> routePoint;



    }

