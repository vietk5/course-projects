package com.electromart.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;

public class CharacterEncodingFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // Không cần cấu hình gì thêm
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    // Ép toàn bộ request/response dùng UTF-8
    if (req.getCharacterEncoding() == null) {
      req.setCharacterEncoding("UTF-8");
    }
    res.setCharacterEncoding("UTF-8");

    chain.doFilter(req, res);
  }

  @Override
  public void destroy() {
    // Không cần giải phóng gì thêm
  }
}
