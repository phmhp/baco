package solux.baco.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import solux.baco.domain.Member;
import solux.baco.service.MemberService;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@Getter
@Setter
public class passwordController {

    private final MemberService memberService;
    private String myEmail;

//    @PostMapping("/change-password")
//    public Optional<Member> changePassword(@RequestBody PasswordForm form, HttpSession session){
//        myEmail = (String) session.getAttribute("loginEmail");
//        Optional<Member> presentMember = memberService.findByEmail(myEmail);
//        memberService.memberInfoUpdate(form, presentMember);
//        return presentMember;
//    }

    /**
     *
     * 타임리프 사용하여 임의로 만든 비밀번호 변경 api (리액트에 데이터 넘겨주는 구조X)
     *
    @GetMapping("/update")
    public String getPassword(HttpSession session, Model model) {
        myEmail = (String) session.getAttribute("loginEmail");
        model.addAttribute("passwordForm", new PasswordForm());
        return "update";
    }

    @PostMapping("/update")
    public String updatePassword(@Valid PasswordForm form){
        Optional<Member> presentMember = memberService.findByEmail(myEmail);
        memberService.memberInfoUpdate(form, presentMember);
        return "home";
    }
     */
}
