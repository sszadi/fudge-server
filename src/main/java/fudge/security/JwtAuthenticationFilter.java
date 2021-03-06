package fudge.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			String jwt = getTokenFromRequest(request);

			if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
				String email = tokenProvider.getUsernameFromToken(jwt);
				log.info("email: {}", email);
				UserDetails userDetails = userDetailsService.loadUserByUsername(email);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken
						(userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception ex) {
			log.error("Could not set user authentication in security context", ex);
		}

		filterChain.doFilter(request, response);
	}

	private static final String AUTH_HEADER = "Authorization";

	private static final String BEARER_HEADER = "Bearer ";

	private String getTokenFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTH_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_HEADER)) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtTokenProvider tokenProvider;
}
