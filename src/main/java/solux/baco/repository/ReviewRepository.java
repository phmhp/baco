package solux.baco.repository;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import solux.baco.domain.Member;
import solux.baco.domain.Review;
import solux.baco.domain.Route;
import solux.baco.service.RouteModel.JsonDataEntity;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Transactional
public class ReviewRepository {

    private final JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    public ReviewRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    /**
     * 후기 저장 메서드
     * //저장 결과를 다시 클라이언트 측에 나타내기 위해서 다시 반환.
     */

    public Long save(Review review) {

        log.info("checklog: ReviewRepository_save-review.getEndPlace: {}", review.getEndPlace());
        log.info("checklog: ReviewRepository_save-review.getStartPlace: {}", review.getStartPlace());
        log.info("checklog: ReviewRepository_save-review.getContent: {}", review.getContent());
        log.info("checklog: ReviewRepository_save-review.getMember: {}", review.getMember());
        log.info("checklog: ReviewRepository_save-review.getRoutePoint: {}", review.getRoute_point());


        //String sql = "INSERT INTO review (member_id,content,start_place,end_place, date,route_point) VALUES (?, ?, ?, ? , ?, ?)";
        //jdbcTemplate.update(sql, review.getMember().getMember_id(), review.getContent(), review.getStartPlace(), review.getEndPlace(), review.getDate(), review.getRoute_point());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("review").usingGeneratedKeyColumns("review_id");

        Long review_id = simpleJdbcInsert.executeAndReturnKey(new MapSqlParameterSource()
                .addValue("member_id", review.getMember().getMember_id())
                .addValue("content",review.getContent())
                .addValue("start_place",review.getStartPlace())
                .addValue("end_place",review.getEndPlace())
                .addValue("date",review.getDate())
                .addValue("route_point",review.getRoute_point()))
                .longValue();

        return review_id;
    }


    /**
     * r후기 상세 조회 관련 메서드
     */
    public String routeData(Long review_id) {
        Review review = entityManager.find(Review.class, review_id);
        String routeData = review.getRoute_point();
        return routeData;
    }


    //getReviewByReview_id (게시글 상세조회)_1.후기테이블정보
    public Optional<Review> detailReview(Long review_id) {
        Review review = entityManager.find(Review.class, review_id);
        return Optional.ofNullable(review);

    }

    //getReviewByReview_id (게시글 상세조회)_2.회원테이블정보
    public Optional<Member> detailMember(Long member_id) {
        Member member = entityManager.find(Member.class, member_id);
        return Optional.ofNullable(member);
    }


    /**작성자 후기 상세조회 */

    public Review findOne(Long reviewId) {
        return entityManager.find(Review.class, reviewId);
    }

    /**작성자 후기 목록 조회 */
    public List<Object[]> findMemberReviews(Long memberId) {
        String jpql = String.format("SELECT r.review_id, r.startPlace, r.endPlace, r.date, r.hashtag, m.nickname FROM Review r Join r.member m WHERE r.member.member_id=%d",memberId);
        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        return query.getResultList();

    }

    /**해시태그 필터링 목록 조회*/
    public List<Object[]>  findHashtagReviews(String hashtag) {
        String jpql = "SELECT r.review_id, r.startPlace, r.endPlace, r.date, r.hashtag, m.nickname FROM Review r JOIN r.member m WHERE r.hashtag = :hashtag";
        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        query.setParameter("hashtag", hashtag);
        return query.getResultList();

    }

    /**전체 후기 목록 조회*/
    /*
    public List<Review> getAllReviews(){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Review> cq = cb.createQuery(Review.class);
        Root<Review> root = cq.from(Review.class);
        cq.select(root);
        return entityManager.createQuery(cq).getResultList();
    }

    public List<Member> findNicknameAllReviews(){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Member> cq = cb.createQuery(Member.class);
        Root<Member> root = cq.from(Member.class);
        cq.select(root);
        return entityManager.createQuery(cq).getResultList();
    }
*/
    public List<Object[]> findJoinEntity(){
        String jpql = "SELECT r.review_id, r.startPlace, r.endPlace, r.date, r.hashtag, m.nickname FROM Review r Join r.member m";
        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        return query.getResultList();
        }

}

