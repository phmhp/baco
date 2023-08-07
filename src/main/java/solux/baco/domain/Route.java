package solux.baco.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long route_id;

    private String startPoint;

    private String endPont;

    private String routePoint;

    @OneToOne
    @JoinColumn(name = "review_id", unique = true)
    private Review review; //FK

}
