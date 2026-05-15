package com.demo.controller;

import com.demo.persistence.SanPhamDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * API endpoint cho autocomplete search
 * Trả về JSON array với các gợi ý
 */
@WebServlet(name = "AutocompleteServlet", urlPatterns = {"/api/autocomplete"})
public class AutocompleteServlet extends HttpServlet {
    
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String query = req.getParameter("q");
        if (query == null || query.trim().isEmpty() || query.length() < 2) {
            resp.getWriter().write("[]");
            return;
        }
        
        try {
            List<String> suggestions = sanPhamDAO.getSuggestions(query.trim(), 10);
            
            // Tạo JSON response
            PrintWriter out = resp.getWriter();
            out.write("[");
            for (int i = 0; i < suggestions.size(); i++) {
                if (i > 0) out.write(",");
                out.write("\"" + escapeJson(suggestions.get(i)) + "\"");
            }
            out.write("]");
            
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }
    
    /**
     * Escape special characters for JSON
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}

