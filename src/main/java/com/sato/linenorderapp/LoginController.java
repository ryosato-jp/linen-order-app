package com.sato.linenorderapp;

import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sato.linenorderapp.entity.User;
import com.sato.linenorderapp.repository.UserRepository;

@Controller
public class LoginController {

	private final UserRepository userRepository;

	public LoginController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@GetMapping("/login")
	public String loginPage() {
	    return "login";
	}

	@PostMapping("/login")
	public String doLogin(@RequestParam String loginId,
			              @RequestParam String password,
			              HttpSession session) {
		
	Optional<User> userOpt = userRepository.findByLoginId(loginId);
		
		if(userOpt.isEmpty()) {
			return "redirect:/login";
		}
		
		User user = userOpt.get();
		
		if(!user.getPassword().equals(password)){
				return "redirect:/login";
	}

	session.setAttribute("facilityId",user.getFacility().getId());
	session.setAttribute("facilityName",user.getFacility().getName());

	return"redirect:/dashboard";
	}
}