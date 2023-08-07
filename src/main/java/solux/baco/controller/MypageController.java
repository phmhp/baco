package solux.baco.controller;

import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import solux.baco.domain.Member;
import solux.baco.domain.Review;
import solux.baco.service.LoginService;
import solux.baco.service.MemberService;
import solux.baco.service.ReviewModel.ReviewListDTO;
import solux.baco.service.ReviewService;

import javax.swing.text.html.Option;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@Getter
@Setter
@RequestMapping("/Mypage")
public class MypageController {

    private final MemberService memberService;
    private final ReviewService reviewService;
    private final LoginService loginService;
    private Optional<Member> presentMember;


    //memberId 사용하지 않고 회원정보 수정
    /**
     * @PostMapping("/MemberInfo-change")
    public Optional<Member> updateMemberInfo1(@RequestBody PasswordForm form, HttpSession session){
        String myEmail = (String) session.getAttribute("loginEmail");
        Optional<Member> presentMember = memberService.findByEmail(myEmail);
        memberService.memberInfoUpdate(form, presentMember);
        return memberService.findByEmail(myEmail);
    }
    */

    //memberId 사용하여 회원정보 수정
    @PostMapping("/MemberInfo-change/{member_id}")
    public Member updateMemberInfo2(@PathVariable("member_id")Long member_id, @RequestBody PasswordForm form){
        Member member1 = memberService.findOne(member_id);
        memberService.memberInfoUpdate(form, member1);
        return memberService.findOne(member_id);
    }

    //memberId 사용하지 않고 나의 작성목록 조회
    @GetMapping("/My-reviews")
    public List<ReviewListDTO> showReviews1(HttpSession session){
        String myEmail = (String) session.getAttribute("loginEmail");
        presentMember = memberService.findByEmail(myEmail);
        if(presentMember.isPresent()){
            Long memberId = presentMember.get().getMember_id();
            return reviewService.findReviews(memberId);
        }
        else{
            return Collections.emptyList();
        }

    }

    //memeberId 사용하여 나의 작성목록 조회
    @GetMapping("/My-reviews/{member_id}")
    public List<ReviewListDTO> showReviews2(@PathVariable("member_id")Long member_id){
        return reviewService.findReviews(member_id);
    }

    //나의 작성 게시글 상세 조회
    @GetMapping("/My-reviews/Review/{review_id}")
    public Review showReview(@PathVariable("review_id") Long review_id){
        return reviewService.findReview(review_id);
    }

    /**
    @PostMapping("/join")
    public Member create_api(@RequestBody MemberForm form, BindingResult result){
        if(result.hasErrors()){
            return "join";
        }

        Member member = new Member();
        member.setEmail(form.getEmail());
        member.setPassword(form.getPassword());
        member.setPassword2(form.getPassword2());
        member.setNickname(form.getNickname());

        memberService.join(member);
        return member;
    }

    @PostMapping("/login")
    public Object loginId_api(@RequestBody LoginForm form, BindingResult bindingResult
            , HttpSession session){

        if(bindingResult.hasErrors()){
            return "login";
        }

        presentMember = Optional.ofNullable(loginService.login(form));

        //현재 로그인한 회원의 이메일 정보를 세션에 담아줌
        session.setAttribute("loginEmail", presentMember.get().getEmail());
        return presentMember;
    }
    */

}
