package solux.baco.service;

import solux.baco.controller.PasswordForm;
import solux.baco.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solux.baco.domain.Member;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Long join(Member member){
        password_check(member.getPassword(), member.getPassword2());
        memberRepository.save(member);
        return member.getMember_id();
    }

    private void password_check(String pwd, String pwd2) {
        if( !pwd.equals(pwd2)){
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }
    }

    //id => member_id로 변경(7/26)
    public Member findOne(Long member_id){
        return memberRepository.findOne(member_id);
    }

    public Optional<Member> findByEmail(String email){
        return memberRepository.findByEmail(email);}

    public void memberInfoUpdate(PasswordForm form, Member presentMember) {
        //예외 처리 필요함...
        password_check(form.getNewPassword(), form.getNewPasswordConfirm());
        Member updateMember = new Member();
        updateMember.setMember_id(presentMember.getMember_id());
        updateMember.setEmail(presentMember.getEmail());
        updateMember.setNickname(form.getNewNickname());
        updateMember.setPassword(form.getNewPassword());
        updateMember.setPassword2(form.getNewPasswordConfirm());
        memberRepository.save(updateMember);
    }
}
