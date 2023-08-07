package solux.baco.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import solux.baco.domain.Member;
import solux.baco.domain.Review;
import solux.baco.service.MemberService;
import solux.baco.service.ReviewModel.*;
import solux.baco.service.ReviewService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import solux.baco.service.ReviewModel.ReviewDTO;
import solux.baco.service.ReviewService;
import solux.baco.service.RouteModel.JsonDataEntity;
import solux.baco.service.RouteService;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/Review")

public class ReviewController {

    private final ReviewService reviewService;
    private final returnReviewDataDTO returnReviewDataDTO;
    private final ReviewDetailDTO reviewDetailDTO;
    private final JsonDataEntity jsonDataEntity;

    @Autowired
    public ReviewController(ReviewService reviewService, returnReviewDataDTO returnReviewDataDTO, ReviewDetailDTO reviewDetailDTO, JsonDataEntity jsonDataEntity) {
        this.reviewService = reviewService;
        this.returnReviewDataDTO = returnReviewDataDTO;
        this.reviewDetailDTO = reviewDetailDTO;
        this.jsonDataEntity = jsonDataEntity;

    }


    /**후기 및 경로 저장(후기작성)*/
    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<SavedReviewDataDTO> saveReviewController(@RequestBody ReviewDTO reviewData) { //@RequestBody : 요청바디와 데이터 매핑. //HttpSession session,
        double[] startPointArray;
        double[] endPointArray;

        double startLat = 0.0;
        double startLng = 0.0;

        double endLat = 0.0;
        double endLng = 0.0;

        //String email = (String) session.getAttribute("loginEmail");
        //log.info("session->email : {}",email);
        log.info("requestBody data-content : {}", reviewData.getContent());
        log.info("requestBody data-startPlace : {}", reviewData.getStartPlace());
        log.info("requestBody data-endPlace : {}", reviewData.getEndPlace());
        log.info("requestBody data-email : {}", reviewData.getEmail());

        try {
            String mapUrl;
            //log.info("checklog: email:{}, reviewData:{}",email,reviewData);
            //예외처리

            //1. ReviewDTO형태의 reviewData를 통해 startPlace,endPlace,content 추출.
            String startPlace = reviewData.getStartPlace();
            String endPlace = reviewData.getEndPlace();
            String content = reviewData.getContent();
            String email = reviewData.getEmail();
            log.info("checklog: ReviewController_saveReviewController-startPlace:{},endPlace:{},content:{}", startPlace, endPlace, content);


            //출발지 도착지 장소명과 좌표 매핑하기
            Optional<double[]> startPoint = reviewService.findPoint(startPlace);
            if (startPoint.isPresent()) {
                startPointArray = startPoint.get();
                log.info("startPointArray={}", startPointArray);

                startLat = startPointArray[0];
                startLng = startPointArray[1];
                log.info("startPointArray[0]={}", startPointArray[0]);
                log.info("startLat={}", startLat);

                Optional<double[]> endPoint = reviewService.findPoint(endPlace);
                if (endPoint.isPresent()) {
                    endPointArray = endPoint.get();
                    log.info("endPointArray={}", endPointArray);

                    endLat = endPointArray[0];
                    endLng = endPointArray[1];
                    log.info("endPointArray[0]={}", endPointArray[0]);
                    log.info("endLat={}", endLat);


                    //(7/30) 1. 경로좌표전달 api호출로 경로데이터 얻기
                    WebClient webClient = WebClient.create();

                    String apiUrl = "https://port-0-baco-server-eg4e2alkhufq9d.sel4.cloudtype.app/route"; //경로좌표전달 url => 서버 배포 시 url 변경 예정
                    double[] startParameter = {startLat, startLng}; //ex-"127.12345, 37.12345"
                    double[] endParameter = {endLat, endLng}; //ex-"128.12345,38.12345"

                    //요청 파라미터 설정 => url 쿼리 스트링 파라미터
                    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(apiUrl)
                            .queryParam("start", startLat + "," + startLng)
                            .queryParam("end", endLat + "," + endLng);

                    //api 호출 후 응답받은 내용을 string 형태로 routePoint에 저장.
                    String routePointString = webClient.get()
                            .uri(uriBuilder.toUriString())
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();
                    log.info("checklog: ReviewController_saveReviewController-routePoint:{}", routePointString);


                    //(7/30)2. 다른 데이터들 저장과 함께 경로좌표데이터도 저장 .
                    //ReviewService 호출
                    //저장하고, 출발지장소명/도착지장소명/후기내용/review_id 반환.
                    returnReviewDataDTO returnReviewDataDTO = reviewService.saveReview(email, startPlace, endPlace, content, routePointString); //

                    //review_id 구하기
                    //MapTestController와 MapConfirm은 똑같이 동작하고, html의 지도api 크기만 다름!
                    //String savedRoutePoint = reviewService.getRoutePointString(review_id);
                    Long review_id = returnReviewDataDTO.getReview_id();


                    //Html에 동적으로 내용을 전달하기 위해 MapTestController(변경 예정)API를 호출
                    WebClient webClient_map = WebClient.create();

                    String apiUrl_map = "https://port-0-baco-server-eg4e2alkhufq9d.sel4.cloudtype.app/map"; //서버 배포 시 url 변경 예정

                    UriComponentsBuilder uriBuilder_map = UriComponentsBuilder.fromUriString(apiUrl_map)
                            .queryParam("review_id", review_id);

                    String mapTest = webClient.get()
                            .uri(uriBuilder.toUriString())
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();
                    log.info("checklog: ReviewController_reviewDetailController-mapTest:{}", mapTest);

                    //html에 경로 표시하기 성공하면
                    mapUrl = "https://port-0-baco-server-eg4e2alkhufq9d.sel4.cloudtype.app/map?review_id=" + review_id;
                    log.info("checklog: ReviewController_reviewDetailController-mapUrl:{}", mapUrl);


                    //클라이언트 측에 반환할 데이터들
                    SavedReviewDataDTO savedReviewDataDTO = new SavedReviewDataDTO();
                    savedReviewDataDTO.setContent(returnReviewDataDTO.getContent());
                    savedReviewDataDTO.setStartPlace(returnReviewDataDTO.getStartPlace());
                    savedReviewDataDTO.setEndPlace(returnReviewDataDTO.getEndPlace());
                    savedReviewDataDTO.setMapUrl(mapUrl);


                    return ResponseEntity.ok(savedReviewDataDTO);
                }
            } else {
                //예외처리
            }


        } catch (Exception e) {
            //db저장 실패 시
            return null; //예외처리 예정

        }
        return null; //예외처리 예정
    }


    /**후기 게시글 상세 조회*/
    @GetMapping("/detail/{review_id}")
    public ReviewDetailDTO reviewDetailContriller(@PathVariable Long review_id, Model model) {
        log.info("checklog: ReviewController_reviewDetailController");
        try {
            String mapUrl;
            //예외처리 구현 예정

            //Html에 동적으로 내용을 전달하기 위해 MapTestController(변경 예정)API를 호출
            WebClient webClient = WebClient.create();
            WebClient webClient_map = WebClient.create();

            String apiUrl = "https://port-0-baco-server-eg4e2alkhufq9d.sel4.cloudtype.app/map"; //서버 배포 시 url 변경 예정

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(apiUrl)
                    .queryParam("review_id", review_id);

            String mapTest = webClient.get()
                    .uri(uriBuilder.toUriString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("checklog: ReviewController_reviewDetailController-mapTest:{}", mapTest);

            //html에 경로 표시하기 성공하면
            mapUrl = "https://port-0-baco-server-eg4e2alkhufq9d.sel4.cloudtype.app/map?review_id=" + review_id;
            log.info("checklog: ReviewController_reviewDetailController-mapUrl:{}", mapUrl);


            ReviewDetailDTO reviewDetail = reviewService.reviewDetail(review_id, mapUrl);
            log.info("checklog: ReviewController_reviewDetailController-content:{},mapUrl:{}", reviewDetail.getContent(), mapUrl);

            return reviewDetail;

        } catch (
                Exception e) {
            log.info("checklog: ReviewController_reviewDetailController-catch");

            //html 경로 표시 실패하면
            //예외처리 구현 예정
            return null;
        }


    }

    /**해시태그 필터링 목록 조회*/
    @GetMapping("/reviews")
    public List<ReviewListDTO> showReviews_hashtag(@RequestParam String hashtag) {
        return reviewService.findHashtagReviews(hashtag);
    }
}