package fr.triplea.demovote.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import fr.triplea.demovote.model.MyUserDetails;

import org.springframework.util.StringUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class JwtTokenFilter extends OncePerRequestFilter
{
  
  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  private MyUserDetailsService userDetailsService;
  
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException 
  {
    try 
    {
      String token = getTokenFromRequest(request);
      
      if (token != null && jwtTokenUtil.validateJwtToken(token)) 
      {
        String username = jwtTokenUtil.getUserNameFromJwtToken(token);
        
        MyUserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception e) { throw new RuntimeException("Cannot set user authentication" + e.getMessage()); }
    
    filterChain.doFilter(request, response);
  }
  
  private String getTokenFromRequest(HttpServletRequest request) 
  {
    String token = request.getHeader("Authorization");
    
    if (StringUtils.hasText(token) && token.startsWith("Bearer ")) 
    {
      return token.substring(7, token.length()); // remove "Bearer "
    }
    
    return null;
  }
}