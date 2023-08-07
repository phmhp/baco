package solux.baco.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Review {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long review_id;

    public Long getReview_id() {
        return review_id;
    }

    public void setReview_id(Long review_id) {
        this.review_id = review_id;
    }


    private String startPlace;

    public String getStartPlace() {
        return startPlace;
    }

    public void setStartPlace(String startPlace) {
        this.startPlace = startPlace;
    }


    private String endPlace;

    public String getEndPlace() {
        return endPlace;
    }

    public void setEndPlace(String endPlace) {
        this.endPlace = endPlace;
    }

    @Column(columnDefinition = "TEXT")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    private java.time.LocalDate date;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }


    private String hashtag;

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }


    @Column(length=10000,columnDefinition = "TEXT")
    private String route_point;
    public String getRoute_point() {
        return route_point;
    }
    public void setRoute_point(String route_point) {
        this.route_point = route_point;
    }


    public Integer getAnalyzed() {
        return analyzed;
    }

    public void setAnalyzed(Integer analyzed) {
        this.analyzed = analyzed;
    }

    //int -> integer
    private Integer analyzed;


    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "member_id")
    private Member member; //FK

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }


}



