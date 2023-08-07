package solux.baco.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solux.baco.service.RouteService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
//@RequestMapping("/Route")
public class RouteController {

    private final RouteService routeService;

    //생성자 만들어서 의존성 주입
    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }


    @GetMapping("/route")
    public List<List<Double>> getRouteController(@RequestParam double[] start, @RequestParam double[] end) {
        log.info("checkLog:RouteController - getRouteController called with start: {} and end: {}", start, end);

        try {

            //예외처리 => 이어서 진행 예정
            if (start == null || start.length != 2 || end == null || end.length != 2) {
                Map<String, Object> failResponse = new HashMap<>();
                failResponse.put("error", "필수 파라미터가 누락되었거나 잘못되었습니다. (start, end)");
                return null;

            }

            //double[2]가 아닌 경우
            else if (start.length != 2 || end.length != 2) {
                throw new IllegalArgumentException("출발지와 도착지 좌표값이 잘못되었습니다.( 배열의 크기가 2가 아님)");
            }

            //정상적인 로직 가능한 경우에 실행되는 부분
            //RouteService 호출
            return (routeService.passRouteData(start, end));

        } catch (IllegalArgumentException e) {

            return null;
        }


    }

}
