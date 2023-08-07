package solux.baco.controller;

import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;
import solux.baco.domain.Member;
import solux.baco.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@Getter @Setter
public class LoginController {

    private final LoginService loginService;
    private Member presentMember;

    @PostMapping("/login")
    public Object loginId_api(@RequestBody LoginForm form, BindingResult bindingResult
            , HttpSession session){

//        if(bindingResult.hasErrors()){
//            return "login";
//        }

        presentMember = loginService.login(form);

        //현재 로그인한 회원의 이메일 정보를 세션에 담아줌
        session.setAttribute("loginEmail", presentMember.getEmail());
        return presentMember;
    }

    /**
     *
     * 타임리프 사용하여 임의로 만든 로그인api (리액트에 데이터 넘겨주는 구조X)
     *

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String loginId(@ModelAttribute("form") LoginForm form, BindingResult bindingResult
                          , HttpSession session){

        if(bindingResult.hasErrors()){
            return "login";
        }

        presentMember = loginService.login(form);

        //현재 로그인한 회원의 이메일 정보를 세션에 담아줌
        session.setAttribute("loginEmail", presentMember.getEmail());
        return "sessionValueCheck";
    }
     */
}
