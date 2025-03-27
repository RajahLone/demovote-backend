package fr.triplea.demovote.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/*")
@Order(2)
public class CorsFilter implements Filter 
{
  
  private final Logger LOG = LoggerFactory.getLogger(CorsFilter.class);

  public CorsFilter() { LOG.info("CorsFilter Init"); }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
  {

    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    response.setHeader("Access-Control-Allow-Origin", "https://localhost:4200");
    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS, DELETE");
    response.setHeader("Access-Control-Max-Age", "3600");
    response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

    if (request.getMethod().equals("OPTIONS")) { response.setStatus(HttpServletResponse.SC_ACCEPTED); return; }

    chain.doFilter(req, res);
  }

  @Override
  public void init(FilterConfig filterConfig) {}

  @Override
  public void destroy() {}
  
}