package solux.baco.service.ReviewModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@JsonIgnoreProperties(ignoreUnknown = true)
public class returnReviewDataDTO {
    //게시글 상세보기로 반환할 값 모음

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



    public Long getReview_id() {
        return review_id;
    }

    public void setReview_id(Long review_id) {
        this.review_id = review_id;
    }

    public Long review_id;


}
