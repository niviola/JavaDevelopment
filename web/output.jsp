<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Page</title>
    </head>
    <body>

        <h4> Time now: <%= new java.util.Date()%> </h4>  
        <hr>
        <br><br>
        <form action="InternetDataSearch">
            <input type="text" name="query" value="${query}" />
            <input type="submit" value="Start" />
        </form>

        <br><br>

        <c:if test="${not empty alldata.getList()}">
            <select>
                <c:forEach var="data" items="${alldata.getList()}">
                    <option>
                        <c:out value="${data.getTitle()}"/>
                    </option>
                </c:forEach>
            </select>
        </c:if>

        <br><br>
        <c:choose>
            <c:when test="${empty alldata}">
                No data yet...
            </c:when>
            <c:otherwise>     

                <ul>
                    <c:forEach var="data" items="${alldata.getList()}">
                        <c:url value="${data.getLink()}" var="url" />
                        <li><a href="${url}" target=\"_blank\">${data.getTitle()}</a></li>           
                        </c:forEach>
                </ul>
            </c:otherwise>
        </c:choose>

    </body>
</html>

