package solux.baco.service.ReviewModel;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class ReviewDTO {
//후기작성 데이터 받아올 때

    private String startPlace;

    private String endPlace;

    private String content;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // 8/2 추가
    private String email;

    public void setStartPlace(String startPlace) {
        this.startPlace = startPlace;
    }

    public void setEndPlace(String endPlace) {
        this.endPlace = endPlace;
    }

    public void setContent(String content) {
        this.content = content;
    }



    public String getStartPlace() {
        return startPlace;
    }

    public String getEndPlace() {
        return endPlace;
    }

    public String getContent() {
        return content;
    }





}
