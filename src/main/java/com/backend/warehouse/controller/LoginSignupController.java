package com.backend.warehouse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.backend.warehouse.entity.User;
import com.backend.warehouse.entity.UserRole;
import com.backend.warehouse.payload.request.LoginRequest;
import com.backend.warehouse.payload.request.SignupRequest;
import com.backend.warehouse.payload.response.JwtResponse;
import com.backend.warehouse.payload.response.MessageResponse;
import com.backend.warehouse.repository.UserRepository;
import com.backend.warehouse.security.jwt.JwtUtils;
import com.backend.warehouse.service.UserDetailsImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class LoginSignupController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		if (!userRepository.findByUsername(loginRequest.getUsername()).isPresent()) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Tài khoản không tồn tại!"));
		}

		User user = userRepository.findByUsername(loginRequest.getUsername()).get();
		if (!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Mật khẩu không đúng!"));
		}

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		// Lấy role từ DB (UserDetailsImpl.getAuthorities() trả về GrantedAuthority)
		String role = userDetails.getAuthorities().stream()
				.findFirst()
				.map(a -> a.getAuthority())
				.orElse("ROLE_STAFF");

		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUserid(), userDetails.getUsername(),
				userDetails.getProfile_name(), userDetails.getEmail(), role));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Tài khoản này đã tồn tại!"));
		}

		UserRole role = UserRole.ROLE_STAFF;
		if ("ADMIN".equalsIgnoreCase(signUpRequest.getRole())) {
			role = UserRole.ROLE_ADMIN;
		}

		User user = new User(
			signUpRequest.getUsername(),
			encoder.encode(signUpRequest.getPassword()),
			signUpRequest.getProfileName(),
			signUpRequest.getEmail(),
			role
		);
		userRepository.save(user);
		return ResponseEntity.ok(new MessageResponse("Đăng ký thành công!"));
	}

	@GetMapping("/countUsers")
	public ResponseEntity<Long> countUsers() {
		long userCount = userRepository.count();
		return ResponseEntity.ok(userCount);
	}
}

