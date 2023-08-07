package solux.baco.service;

import solux.baco.controller.LoginForm;
import solux.baco.domain.Member;
import solux.baco.exception.NotCorresspondingEmailException;
import solux.baco.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;
    public Member login(LoginForm loginForm){
        Optional<Member> findMember = memberRepository.findByEmail(loginForm.getEmail());
        if(!findMember.orElseThrow(()->new NotCorresspondingEmailException("해당 이메일이 존재하지 않습니다."))
                .checkPassword(loginForm.getPassword())){
            throw new IllegalStateException("이메일과 비밀번호가 일치하지 않습니다.");
        }
        return findMember.get();
    }

}
