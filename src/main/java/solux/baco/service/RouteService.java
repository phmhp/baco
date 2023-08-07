package solux.baco.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import solux.baco.service.RouteModel.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RouteService {

    //여러 메서드에서 사용할 예정인 변수들이라 멤버변수로 선언
    List<List<Double>> path = null; //경로좌표 List의 List
    List<Double> startList = new ArrayList<>(); //(요청파라미터)시작좌표 List
    List<Double> endList = new ArrayList<>(); //(요청파라미터)도착좌표 List
    List<Double> startResponseList = new ArrayList<>(); //(네이버api응답)시작좌표 List
    List<Double> endResponseList = new ArrayList<>(); //(네이버api응답)도착좌표 List
    double[] startNaver = new double[2]; //네이버 호출 시 사용할 시작좌표 double배열
    double[] endNaver = new double[2]; //네이버 호출 시 사용할 도착좌표 double배열


    //전체 메서드 실행 순서을 담고있는 메서드
    public List<List<Double>> passRouteData(double[] startKakao, double[] endKakao) {
        log.info("checkLog:RouteService_passRouteData- startKakao: {} and endKakao: {}", startKakao, endKakao);

        //준비과정(startList/endList를 모두 비어있는 상태로 설정하고, double[]형태인 startKakao/endKakao의 데이터를 List<>형태인 startList/endList에 복사함.

        //메서드 호출 시마다 startKakao,endKakao를 startList,endList에 매번 복사하면서 매번 배열에 같은 값이 추가되는 문제 발생
        //그래서 초기화해주는 코드 추가함.
        startList.clear();
        endList.clear();
        log.info("checkLog:RouteService_passRouteData- startList(after clear): {} and endList(after clear): {}", startList, endList);

        //startKakao,endKakao의 요소들을 하나씩 startList/endList에 각각 추가함.
        for (double startCoordinate : startKakao) {
            startList.add(startCoordinate);
        }
        for (double endCoordinate : endKakao) {
            endList.add(endCoordinate);
        }
        log.info("checkLog:RouteService_passRouteData- startList(after loop ): {} and endList(after loop ): {}", startList, endList);


        //1.출발,도착좌표 [위도,경도] 순에서 [경도,위도]순으로 변경
        getLngLat(startKakao, endKakao);

        //2.네이버 지도 api 호출 후 응답받고 필요한 데이터만 변수에 담는 메서드 호출
        Boolean getRouteResult = getRoute(startNaver, endNaver);
        log.info("checkLog:RouteService_passRouteData- getRouteResult: {}", getRouteResult);
        //예외처리 예정
        log.info("checkLog:RouteService_passRouteData- path(before swap): {}", path);

        //3. 경로 좌표 [경,위]에서 [위,경] 으로 변환
        swapPath();
        log.info("checkLog:RouteService_passRouteData- path(after  swap): {}", path);

        //4.필요한 데이터들을 묶어서 객체형태로 만들기 위한 메서드 호출
        List<List<Double>> processRouteMap = processRoute();
        log.info("checkLog:RouteService_passRouteData- processRouteMap: {}", processRouteMap);

        //5.경로좌표, 출발좌표, 도착좌표 담아서 json형태로 반환.
        return processRouteMap;
    }


    //1. 좌표데이터 순서 바꾸는 메서드
    public void getLngLat(double[] startKakao, double[] endKakao) {
        //[위도,경도]에서 [경도,위도]순으로 변경하는 과정.
        startNaver[0] = startKakao[1];
        startNaver[1] = startKakao[0];

        endNaver[0] = endKakao[1];
        endNaver[1] = endKakao[0];
        log.info("checkLog:RouteService_getLngLat- startNaver: {} and endNaver: {}", startNaver, endNaver);

    }


    //2. 네이버 지도 API(Direction 5 driving) 활용해서 길찾기 기능 수행 후 결과 얻는 메서드
    //좌표 형태의 출발지,도착지를 얻은 상태로 가정.
    public Boolean getRoute(double[] startCoordinate, double[] endCoordinate) {
        Boolean result = true;
        //(1)네이버 지도 API 호출하는 과정
        //WebClient 인스턴스 생성
        WebClient webClient = WebClient.create();

        String apiUrl = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving"; //네이버 api url
        String clientId = "73qkoqmj6s"; //네이버 지도 api 키 발급 id
        String clientSecret = "XlBY8sv7BYz2GKkzZ9kFfI4Ry4yV1TqfDPVIAd64"; //네이버 지도 api 키 발급 pw
        double[] startParameter = startCoordinate; //ex-"127.12345, 37.12345"
        double[] endParameter = endCoordinate; //ex-"128.12345,38.12345"

        //요청 파라미터 설정 => url 쿼리 스트링 파라미터
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("start", startParameter[0] + "," + startParameter[1])
                .queryParam("goal", endParameter[0] + "," + endParameter[1])
                .queryParam("option", "trafast");

        //api 호출 후 응답받은 내용을 string 형태로 response에 저장.
        String response = webClient.get()
                .uri(uriBuilder.toUriString())
                .header("X-NCP-APIGW-API-KEY-ID", clientId) //요청헤더로 키 발급 id 전송
                .header("X-NCP-APIGW-API-KEY", clientSecret) //요청헤더로 키 발급 pw 전송
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.info("checkLog:RouteService_getRoute- response: {}", response);


        //(2)응답 내용 중 필요한 정보 저장
        // Jackson ObjectMapper 인스턴스 생성
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // readValue()메서드를 통해 json형식으로 구성된 string(response)을 java객체(RouteDTO 클래스)에 매핑 후 RouteDTO변수 RouteDTO에 저장.
            RouteDTO RouteDTO = objectMapper.readValue(response, RouteDTO.class);

            //RouteDTO에 담긴 내용에서 getter를 통해 저장.
            int code = RouteDTO.getCode(); //code

            //code가 0이면 길찾기 성공, code가 1~6 사이의 값이면 실패
            if (code == 0) {
                String message = RouteDTO.getMessage(); //message
                String currentDateTime = RouteDTO.getCurrentDateTime(); //currentDateTime

                //중첩구조로 표현된 응답내용을 얻기 위해 인스턴스 생성(응답바디 구조 참고)
                RouteUnitEnt routeUnitEnt = RouteDTO.getRoute(); //route

                //trafast속성은 배열형태이기 때문에 먼저 배열형태의 trafastList를 저장한 다음,
                //trafastList에서 0번째 요소(summary(start_location,goal_location,distance 등),path 등이 포함됨.)에 해당하는 내용을 trafast에 저장해야함.
                List<Trafast> trafastList = routeUnitEnt.getTrafast(); //trafastList
                Trafast trafast = trafastList.get(0);//trafast(trafastList의 0번째 요소)
                Summary summary = trafast.getSummary(); //summary

                startResponseList.add(0, summary.getStart().getLat());
                startResponseList.add(1, summary.getStart().getLng());

                endResponseList.add(0, summary.getGoal().getLat());
                endResponseList.add(1, summary.getGoal().getLng());

                log.info("checkLog:RouteService_getRoute-startResponseList: {} and endResponseList : {} ", startResponseList, endResponseList);
                //경로 좌표 배열
                path = trafast.getPath();
            }
            //code가 0이 아닌 경우
            else {
                result = false;
                return result;
            }

        } catch (JsonProcessingException e) {
            // 에러가 날 경우 로그 기록
            log.error("response_parsing error", e);
        }
        return result;
    }


    //3. 응답으로 받은 경로좌표List를 [경도,위도] -> [위도,경도] 순으로 변경
    public void swapPath() {
        for (List<Double> coordinate : path) {
            double lng = coordinate.get(0);
            double lat = coordinate.get(1);

            coordinate.set(0, lat);
            coordinate.set(1, lng);
        }
    }


    //4.필요한 데이터 추출한 멤버변수를 객체형태로 만드는 단계의 메서드
    public List<List<Double>> processRoute() {
        Map<String, Object> processRouteData = new HashMap<>();

        processRouteData.put("start", startResponseList); //출발좌표
        processRouteData.put("path", path); //도착좌표
        processRouteData.put("end", endResponseList); //도착좌표
        log.info("checkLog:RouteService_processRoute-startList: {} and endList: {}", startList, endList);
        log.info("checkLog:RouteService_processRoute-path: {}", path);

        return path; //내부호출이기 때문에 바로 path를 전달하기로 변경.
    }

}