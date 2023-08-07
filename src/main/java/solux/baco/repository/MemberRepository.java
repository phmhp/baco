package solux.baco.repository;

import solux.baco.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Member member){
        if(member.getMember_id() == null){
            em.persist(member);
        } else { //이미 DB에 등록되어 있을 경우
            em.merge(member);
        }
    }

    //id => member_id로 변경(7/26)
    public Member findOne(Long member_id){
        return em.find(Member.class, member_id);
    }

    //이메일로 회원 조회
    public Optional<Member> findByEmail(String email){
        List<Member> members = em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email",email)
                .getResultList();
        if (!members.isEmpty()){
            return Optional.of(members.get(0));
        }
        else{
            return Optional.empty();
        }
//        return em.createQuery("select m from Member m where m.email = :email", Member.class)
//                .setParameter("email", email)
//                .getResultList().stream().findAny();
    }

}
