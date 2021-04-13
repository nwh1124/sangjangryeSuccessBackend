package com.sbs.deungbulproto.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sbs.deungbulproto.dto.Adm;
import com.sbs.deungbulproto.dto.ResultData;
import com.sbs.deungbulproto.service.AdmMemberService;
import com.sbs.deungbulproto.util.Util;

@Controller
public class AdmMemberController extends BaseController {
	@Autowired
	private AdmMemberService admMemberService;

	@RequestMapping("/adm/member/login")
	public String login() {
		return "adm/member/login";
	}

	@RequestMapping("/adm/member/doLogin")
	@ResponseBody
	public String doLogin(String loginId, String loginPw, String redirectUrl, HttpSession session) {
		if (loginId == null) {
			return Util.msgAndBack("loginId를 입력해주세요.");
		}

		Adm existingMember = admMemberService.getMemberByLoginId(loginId);

		if (existingMember == null) {
			return Util.msgAndBack("존재하지 않는 아이디 입니다.");
		}

		if (loginPw == null) {
			return Util.msgAndBack("비밀번호를 입력해주세요.");
		}

		if (existingMember.getLoginPw().equals(loginPw) == false) {
			return Util.msgAndBack("비밀번호가 일치하지 않습니다.");
		}

		if (admMemberService.isAdmin(existingMember) == false) {
			return Util.msgAndBack("관리자만 접근할 수 있는 페이지 입니다.");
		}

		session.setAttribute("loginedMemberId", existingMember.getId());

		String msg = String.format("%s님 환영합니다.", existingMember.getName());

		redirectUrl = Util.ifEmpty(redirectUrl, "../home/main");

		return Util.msgAndReplace(msg, redirectUrl);
	}

	@RequestMapping("/adm/member/doLogout")
	@ResponseBody
	public String doLogout(HttpSession session) {
		session.removeAttribute("loginedMemberId");

		return Util.msgAndReplace("로그아웃 되었습니다.", "../member/login");
	}

	@RequestMapping("/adm/member/doModify")
	@ResponseBody
	public ResultData doModify(@RequestParam Map<String, Object> param, HttpServletRequest req) {
		if (param.isEmpty()) {
			return new ResultData("F-2", "수정할 정보를 입력해주세요.");
		}

		int loginedMemberId = (int) req.getAttribute("loginedMemberId");
		param.put("id", loginedMemberId);

		return admMemberService.modifyMember(param);
	}

}
