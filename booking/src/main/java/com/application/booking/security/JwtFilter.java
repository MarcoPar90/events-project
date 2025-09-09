package com.application.booking.security;

import com.application.booking.exception.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Log
@Component
public class JwtFilter extends OncePerRequestFilter {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.header}")
	private String header;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getServletPath();
		if (path.startsWith("/public/") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
			filterChain.doFilter(request, response);
			return;
		}

		String authHeader = request.getHeader(header);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			unauthorizedTokenError(response, "Token not valid");
			return;
		}

		String jwt = authHeader.substring(7);

		try {
			byte[] keyBytes = Decoders.BASE64.decode(secret);
			SecretKey key = Keys.hmacShaKeyFor(keyBytes);

			Claims claims = Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(jwt)
					.getBody();

			String username = claims.getSubject();
			log.info("JWT valido, user: " + username);

			List<String> roles = claims.get("authorities", List.class);
			List<SimpleGrantedAuthority> authorities = roles.stream()
					.map(SimpleGrantedAuthority::new)
					.toList();

			UsernamePasswordAuthenticationToken authToken =
					new UsernamePasswordAuthenticationToken(username, null, authorities);
			SecurityContextHolder.getContext().setAuthentication(authToken);

		} catch (ExpiredJwtException e) {
			unauthorizedTokenError(response, "Token expired");
			return;

		} catch (JwtException e) {
			unauthorizedTokenError(response, "Token not valid");
			return;
		}

		filterChain.doFilter(request, response);
	}

	private void unauthorizedTokenError(HttpServletResponse response, String message) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		ErrorMessage error = new ErrorMessage(
				HttpStatus.UNAUTHORIZED.name(),
				message,
				LocalDateTime.now(),
				HttpServletResponse.SC_UNAUTHORIZED
		);

		response.getWriter().write(mapper.writeValueAsString(error));
	}
}
