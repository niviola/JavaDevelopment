/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.data.search;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;

/**
 *
 * @author Viktor-VM
 */
//@WebServlet(name = "InternetDataSearch", urlPatterns = {"/InternetDataSearch"})
@WebServlet(
        urlPatterns = "/InternetDataSearch",
        initParams
        = {
            // @WebInitParam(name = "inputText", value = "java программист разработчик требования резюме")
            @WebInitParam(name = "inputText", value = "java programmer resume")
        }
)
public class InternetDataSearch extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Cookie[] cookies = request.getCookies(); // step 1: request from client to our web service
        String field, usercookies = "";
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie c = cookies[i];
                field = c.getName() + "=" + c.getValue();
                usercookies += "; " + field;
                int here = 0;
            }
        }
        if (usercookies.length() > 2) {
            usercookies = usercookies.substring(2);
        }

//        Cookie cookie = new Cookie("testCookie", "12345");
//                cookie.setMaxAge(3600 * 24 * 14);
//                response.addCookie(cookie);
        request.setCharacterEncoding("UTF-8");
        String inputText
                = //request.getParameter("inputText")
                getServletConfig().getInitParameter("inputText");
        WebSearch websearch = new WebSearch();
        //String data = websearch.readURL(inputText);
        // 
        websearch.setDirectoryPath(getDirectoryPath(1));
        websearch.setDirectoryPathData(getDirectoryPath(2));
        websearch.setCookie(usercookies);                          // end step 1
        websearch.setQuery(inputText);

        //ArrayList<SearchResult>
        AllResults allresults = websearch.getSearchPage();//inputText

        String mailcookies = websearch.getCookie();
        String[] arraycookies = mailcookies.split("; ");
        for (int i = 0; i < arraycookies.length; i++) {
            String[] ai = arraycookies[i].split("=");
            if (ai.length > 1) {
                Cookie cookie = new Cookie(ai[0], ai[1]);
                cookie.setMaxAge(3600 * 24 * 14);
                response.addCookie(cookie);
            }
        }

        String urlPath = request.getRequestURL().toString();
        urlPath = urlPath.substring(0, urlPath.lastIndexOf("/"));

        // get a Session
        HttpSession session = request.getSession();
        session.setAttribute("alldata", allresults);
        session.setAttribute("query", inputText);

        // redirect a user to a certain page 
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/output.jsp");
        dispatcher.forward(request, response);

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet WordSearch</title>");
            out.println("</head>");
            out.println("<body>");

            List<SearchResult> data = allresults.getList();
            Iterator<SearchResult> iterator = data.iterator();
            while (iterator.hasNext()) {
                SearchResult search = iterator.next();
                out.println("<a href=\"" + search.getLink() + "\" target=\"_blank\">"
                        + search.getTitle() + "</a>"
                        + "<br>"
                        + search.getDescription() + "<br>"
                        //                        + "<a href=\"" + urlPath + "/myfolder/" + search.getTitle() + ".html\" target=\"_blank\">"
                        //                        + "Click here" + "</a>"
                        + "<br><br>");
                Map map = search.getResultsMap();
                Iterator iterator1 = map.keySet().iterator();
                while (iterator1.hasNext()) {
                    Object key = iterator1.next();
                    out.println(key + "<br>");
                    out.println(map.get(key) + "<br><br>");
                }
            }
            out.println("</body>");

            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private String getDirectoryPath(int step) {

        String pathseparator;
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            pathseparator = "\\";
        } else {
            pathseparator = "/";
        }
        String path = getServletContext().getRealPath(""); // root directory
        if (step == 1) {
            path += pathseparator + "searchfolder";
        } else {
            path += pathseparator + "datafolder";
        }

        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir();
        } else if (step == 1) {
            //cleanFolder(folder);
            recurseDeleteDir(folder);
            folder.mkdir();
        }

        return path + pathseparator;
    }

    private void cleanFolder(File dir) {

        if (dir.exists()) {
            File[] flist = dir.listFiles();
            for (int i = 0; i < flist.length; i++) {
                int test = 0;
                if (!flist[i].isDirectory()) {
                    flist[i].delete();
                }
            }

        }

    }

    private boolean recurseDeleteDir(File fdir) {
        boolean deleted;
        if (fdir.isDirectory()) {
            String[] children = fdir.list();//list()- список имен файлов в директории
            for (int i = 0; i < children.length; i++) {
                deleted = recurseDeleteDir(new File(fdir, children[i]));
                int test = 0;
                if (!deleted) {
                    return false;
                }
            }
        }

        //deleted = fdir.delete();
        return fdir.delete();
    }

}
