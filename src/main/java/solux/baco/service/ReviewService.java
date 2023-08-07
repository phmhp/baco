//origin/develop merge 테스트
package solux.baco.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import solux.baco.domain.Member;
import solux.baco.domain.Review;
import solux.baco.repository.ReviewRepository;
import solux.baco.service.ReviewModel.ReviewDetailDTO;
import solux.baco.service.ReviewModel.ReviewListDTO;
import solux.baco.service.ReviewModel.returnReviewDataDTO;
import solux.baco.service.RouteModel.JsonDataEntity;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import java.util.List;

@Service
@Slf4j
//@RequiredArgsConstructor
public class ReviewService {


    private final ReviewRepository reviewRepository;
    private final MemberService memberService;
    private final ReviewDetailDTO reviewDetailDTO;
    private final Map<String, double[]> placeCoordinate = new HashMap<>(); //장소 좌표 매칭하기 위해 미리 저장


    public ReviewService(ReviewRepository reviewRepository, MemberService memberService, ReviewDetailDTO reviewDetailDTO) {

        this.reviewRepository = reviewRepository;
        this.memberService = memberService;
        this.reviewDetailDTO = reviewDetailDTO;

        /**미리 준비한 장소와 좌표 */
        placeCoordinate.put("숙명여대", new double[]{37.54167, 126.964930});
        placeCoordinate.put("숙명 여대", new double[]{37.54167, 126.964930});
        placeCoordinate.put("숙대", new double[]{37.54167, 126.964930});
        placeCoordinate.put("숙명여자대학교", new double[]{37.54167, 126.964930});
        placeCoordinate.put("숙명 여자대학교", new double[]{37.54167, 126.964930});
        placeCoordinate.put("숙명여자 대학교", new double[]{37.54167, 126.964930});

        placeCoordinate.put("남산타워", new double[]{37.549180, 126.989704});
        placeCoordinate.put("남산 타워", new double[]{37.549180, 126.989704});
        placeCoordinate.put("N서울타워", new double[]{37.549180, 126.989704});
        placeCoordinate.put("N 서울 타워", new double[]{37.549180, 126.989704});
        placeCoordinate.put("N서울 타워", new double[]{37.549180, 126.989704});

        placeCoordinate.put("청계천", new double[]{37.569225, 126.978628});

        placeCoordinate.put("경복궁", new double[]{37.57621220811897, 126.97672509786915});

        placeCoordinate.put("어린이대공원", new double[]{37.549785, 127.081546});
        placeCoordinate.put("어린이 대공원", new double[]{37.549785, 127.081546});
        placeCoordinate.put("서울어린이대공원", new double[]{37.549785, 127.081546});

        placeCoordinate.put("홍대", new double[]{37.550768, 126.925639});
        placeCoordinate.put("홍익대학교", new double[]{37.550768, 126.925639});
        placeCoordinate.put("홍익 대학교", new double[]{37.550768, 126.925639});

        placeCoordinate.put("서울시청", new double[]{37.56640973022008, 126.97857314414601});
        placeCoordinate.put("서울 시청", new double[]{37.56640973022008, 126.97857314414601});
        placeCoordinate.put("서울 특별 시청", new double[]{37.56640973022008, 126.97857314414601});
        placeCoordinate.put("서울특별시청", new double[]{37.56640973022008, 126.97857314414601});

        placeCoordinate.put("여의도 한강공원", new double[]{37.528409, 126.933089});
        placeCoordinate.put("여의도한강 공원", new double[]{37.528409, 126.933089});
        placeCoordinate.put("여의도 한강 공원", new double[]{37.528409, 126.933089});
        placeCoordinate.put("한강공원", new double[]{37.528409, 126.933089});
        placeCoordinate.put("한강 공원", new double[]{37.528409, 126.933089});
        placeCoordinate.put("한강", new double[]{37.528409, 126.933089});

        placeCoordinate.put("서울숲", new double[]{37.545020, 127.040982});
        placeCoordinate.put("서울 숲", new double[]{37.545020, 127.040982});

        placeCoordinate.put("월드컵경기장", new double[]{37.568403, 126.896931});
        placeCoordinate.put("월드컵 경기장", new double[]{37.568403, 126.896931});

        placeCoordinate.put("키에리", new double[]{37.533381, 126.993136});

        placeCoordinate.put("석촌호수", new double[]{37.511676, 127.103444});
        placeCoordinate.put("석촌 호수", new double[]{37.511676, 127.103444});
        placeCoordinate.put("석촌 호수 공원 ", new double[]{37.511676, 127.103444});
        placeCoordinate.put("석촌호수공원 ", new double[]{37.511676, 127.103444});
    }


    /**
     * 후기 저장 메서드 (findPoint -> saveReview)
     */


    /**
     * 미리 준비한 장소와 좌표를 매핑하는 과정
     */
    public Optional<double[]> findPoint(String placeName) {
        log.info("placeName = {}", placeName);
        String lowercasePlaceName = placeName.toLowerCase();

        Optional<double[]> coordinate = null;
        for (Map.Entry<String, double[]> entry : placeCoordinate.entrySet()) {
            //lowercase 상태에서 키 값을 먼저 비교한 다음
            if (entry.getKey().toLowerCase().equals(lowercasePlaceName)) {
                //같은 키값일 때의 좌표를 반환
                coordinate = Optional.of(entry.getValue());
                log.info("coordinate = {}", coordinate);

                if (coordinate != null && coordinate.isPresent()) {
                    double[] coordinateArray = coordinate.get();
                    log.info("coordinateArray = {}", coordinateArray);
                    //return coordinateArray;
                }

            } else {
                //예외처리
            }
        }
        return coordinate; //예외처리
    }

    /**후기 저장 메서드*/
    public returnReviewDataDTO saveReview(String email, String startPlace, String endPlace, String content, String routePoint) { //
        log.info("checklog: ReviewService_saveReview-review.setRoutePoint(routePoint): {}", startPlace);
        log.info("checklog: ReviewService_saveReview-review.setRoutePoint(routePoint): {}", endPlace);
        log.info("checklog: ReviewService_saveReview-review.setRoutePoint(routePoint): {}", content);
        log.info("checklog: ReviewService_saveReview-review.setRoutePoint(routePoint): {}", routePoint);


        Review review = new Review();
        returnReviewDataDTO returnReviewDataDTO = new returnReviewDataDTO();
/**
 //1. 세션에서 이메일 추출하기
 String email = (String) session.getAttribute("loginEmail");
 log.info("checklog: ReviewService_saveReview-loginEmail : {}", email);
 //전달받은 데이터 예외처리
 log.info("checklog: ReviewService_saveReview-ReviewService");
 */

        //2. 이메일을 통해서 작성자의 Member객체 받아오기
        Optional<Member> writerInfo = memberService.findByEmail(email);
        log.info("checklog: ReviewService_saveReview-writerInfo: {}", writerInfo);

        //3. 받아온 Member객체를 통해서 member_id 추출하기
        if (writerInfo.isPresent()) {
            //Optional 객체 속의 요소인 Member객체를 가져오기 위해 .get() //(null이 아닐 때만 get으로 가져올 수 있음.)
            Member member = writerInfo.get();
            //Long member_id = member.getMember_id();
            log.info("checklog: ReviewService_saveReview-review.routePoint: {}", routePoint);


            review.setMember(member); //작성자의 member테이블 레코드 저장
            review.setContent(content);
            review.setStartPlace(startPlace);
            review.setEndPlace(endPlace);
            review.setDate(LocalDate.now());
            review.setRoute_point(routePoint); //(7/30)수정-route테이블은 삭제, 후기테이블에 routePoint 컬럼 추가.

            log.info("checklog: ReviewService_saveReview-review.setRoutePoint(routePoint): {}", review.getRoute_point());

            Long review_id = reviewRepository.save(review);

            returnReviewDataDTO.setStartPlace(review.getStartPlace());
            returnReviewDataDTO.setEndPlace(review.getEndPlace());
            returnReviewDataDTO.setContent(review.getContent());
            returnReviewDataDTO.setReview_id(review_id);

        } else {
            return null;
        }
        return returnReviewDataDTO;
    }




    /**
     * 상세조회 관련 메서드
     */

    /**
     * review_id에 해당하는 경로 데이터만 빼오는 메서드(html에 경로를 넘기기 위한 과정임)
     */
    public String getJsonData(Long review_id) {
        log.info("checklog: ReviewService_getJsonData");

        try {
            String routePointString = reviewRepository.routeData(review_id); //review_id로 route데이터 빼오는 과정
            log.info("checklog: ReviewService_getJsonData-routePointString: {}", routePointString);

            return routePointString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 마이페이지_나의 후기 보기 목록 중 작성자 후기 상세조회
     */
    public Review findReview(Long reviewId) {
        return reviewRepository.findOne(reviewId);
    }

    /**
     * 후기 공유 게시판 기본 목록 or 해시태그 필터링 목록 중 후기 상세조회
     */
    public ReviewDetailDTO reviewDetail(Long review_id, String mapUrl) {
        ReviewDetailDTO reviewDetailDTO = new ReviewDetailDTO();
        log.info("checklog: ReviewService_getJsonData-review_id: {}", review_id);

        //review_id에 해당되는 저장값 가져오도록 repository 호출
        try {
            Optional<Review> reviewEntity = reviewRepository.detailReview(review_id);
            log.info("checklog: ReviewService_getJsonData-reviewEntity: {}", reviewEntity);

            // Optional이기 때문에 null일 수도 있음.
            if (reviewEntity.isPresent()) {
                Review review = reviewEntity.get();
                //String startPlace = review.getStartPlace();
                //String endPlace = review.getEndPlace();
                String content = review.getContent();
                //Long member_id = review.getMember().getMember_id();


                reviewDetailDTO.setContent(content);
                reviewDetailDTO.setMapUrl(mapUrl);
                //log.info("checklog: ReviewService_reviewDetail-startPlace: {}", startPlace);
                //log.info("checklog: ReviewService_reviewDetail-endPlace: {}", endPlace);
                log.info("checklog: ReviewService_getJsonData-content: {}", content);
                log.info("checklog: ReviewService_getJsonData-mapUrl: {}", mapUrl);


            }

        } catch (Exception e) {

            return null; //예외처리 구현예정
        }
        return reviewDetailDTO;
    }


    /**
     * html동적 렌더링 (db에서 string 값을 빼온 후 객체형태로 변경)
     */
    public Double[][] makeArray(String jsonData) {
        log.info("checklog: ReviewService_makeArray");

        JSONArray jsonArray = new JSONArray(jsonData);
        Double[][] jsonDataArray = new Double[jsonArray.length()][2];

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray innerArray = jsonArray.getJSONArray(i);
            jsonDataArray[i][0] = innerArray.getDouble(0);
            jsonDataArray[i][1] = innerArray.getDouble(1);
        }
        log.info("checklog: ReviewService_makeArray-jsonDataArray:{}", jsonDataArray);
        return jsonDataArray;
    }


    /**
     * 전체 후기 목록 조회
     */
    public List<ReviewListDTO> allReviews() {
        //Join된 테이블 요소를 List형태로 받아옴
        List<Object[]> allReviewList = reviewRepository.findJoinEntity();
        //(반환할) List형태의 ReviewListDTO를 생성
        List<ReviewListDTO> reviewListDTOs = new ArrayList<>();
        //allReviewList에 담아온 값들을 한 레코드씩 돌면서 하나의 객체를 완성
        for (Object[] data : allReviewList) {
            ReviewListDTO oneReviewListDTO = new ReviewListDTO();
            oneReviewListDTO.setReview_id((Long) data[0]);
            oneReviewListDTO.setStartPlace((String) data[1]);
            oneReviewListDTO.setEndPlace((String) data[2]);
            oneReviewListDTO.setDate((LocalDate) data[3]);
            oneReviewListDTO.setHashtag((String) data[4]);
            oneReviewListDTO.setNickname((String) data[5]);

            reviewListDTOs.add(oneReviewListDTO);
        }
        return reviewListDTOs;
    }


    //반환값 수정한 부분

    /**
     * 작성자 후기 목록 조회
     */

    public List<ReviewListDTO> findReviews(Long memberId) {

        List<Object[]> memberIdReviewList = reviewRepository.findMemberReviews(memberId);
        //(반환할) List형태의 memberIdReviewListDTOs 를 생성
        List<ReviewListDTO> memberIdReviewListDTOs = new ArrayList<>();
        //memberIdReviewList에 담아온 값들을 한 레코드씩 돌면서 하나의 객체를 완성
        for (Object[] data : memberIdReviewList) {
            ReviewListDTO oneMemberIdReviewListDTO = new ReviewListDTO();
            oneMemberIdReviewListDTO.setReview_id((Long) data[0]);
            oneMemberIdReviewListDTO.setStartPlace((String) data[1]);
            oneMemberIdReviewListDTO.setEndPlace((String) data[2]);
            oneMemberIdReviewListDTO.setDate((LocalDate) data[3]);
            oneMemberIdReviewListDTO.setHashtag((String) data[4]);
            oneMemberIdReviewListDTO.setNickname((String) data[5]);

            memberIdReviewListDTOs.add(oneMemberIdReviewListDTO);
        }
        return memberIdReviewListDTOs;
    }


    /**
     * 해시태그 필터링 조회
     */
    public List<ReviewListDTO> findHashtagReviews(String hashtag) {
        List<Object[]> hashtagReviewList = reviewRepository.findHashtagReviews(hashtag);
        //(반환할) List형태의 hashtagReviewListDTOs 를 생성
        List<ReviewListDTO> hashtagReviewListDTOs = new ArrayList<>();
        //hashtagReviewList에 담아온 값들을 한 레코드씩 돌면서 하나의 객체를 완성
        for (Object[] data : hashtagReviewList) {
            ReviewListDTO oneHashtagReviewListDTO = new ReviewListDTO();
            oneHashtagReviewListDTO.setReview_id((Long) data[0]);
            oneHashtagReviewListDTO.setStartPlace((String) data[1]);
            oneHashtagReviewListDTO.setEndPlace((String) data[2]);
            oneHashtagReviewListDTO.setDate((LocalDate) data[3]);
            oneHashtagReviewListDTO.setHashtag((String) data[4]);
            oneHashtagReviewListDTO.setNickname((String) data[5]);

            hashtagReviewListDTOs.add(oneHashtagReviewListDTO);
        }
        return hashtagReviewListDTOs;
    }


}
