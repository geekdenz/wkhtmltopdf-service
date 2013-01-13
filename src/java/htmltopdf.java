/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

/**
 *
 * @author student
 */
@WebServlet(name = "htmltopdf", urlPatterns = {"/render.pdf"})
public class htmltopdf extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Runtime rt = Runtime.getRuntime();
        String url = request.getParameter("url");
        String outputFileName = request.getParameter("filename");
        Double zoom = 1.0;
        try{
            zoom = Double.parseDouble(request.getParameter("zoom"));
        }catch (Exception ex){}
        
        Process p;
        
        if (outputFileName != null) {
            response.addHeader("Content-Disposition", "attachment; filename=\""+
                    outputFileName +"\"");
        }
        if(url != null){
            //hopefully this stops the program accessing local files.
            if(! (url.startsWith("http://") || url.startsWith("https://"))  ){
                url = "http://" + url;
            }
            p = rt.exec("wkhtmltopdf --disallow-local-file-access --zoom " + zoom + " "+ url +" -");
            
        }else{
            String html = request.getParameter("html");
            if(html == null){
                response.sendError(404);
                return;
            }
            p = rt.exec("wkhtmltopdf --disallow-local-file-access --zoom " + zoom + " - -");
            IOUtils.write(html, p.getOutputStream());
            p.getOutputStream().close();
        }
        //IOUtils.copy(p.getErrorStream(), System.out);
        //p.getErrorStream().close();
        try {
            IOUtils.copy(p.getInputStream(), response.getOutputStream());
        } finally {
            p.getInputStream().close();
            response.getOutputStream().close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
