package solux.baco.service.ReviewModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@JsonIgnoreProperties(ignoreUnknown = true)
public class SavedReviewDataDTO {
    //후기 저장하고 다시 반환할 때

    public void setStartPlace(String startPlace) {
        this.startPlace = startPlace;
    }

    public void setEndPlace(String endPlace) {
        this.endPlace = endPlace;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String startPlace;

    private String endPlace;

    private String content;


    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    //7/30 추가
    private String mapUrl;


}
