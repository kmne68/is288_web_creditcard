<%-- 
    Document   : History
    Created on : Oct 10, 2017, 9:13:29 PM
    Author     : kmne6
--%>

<%@page import="business.CreditCard"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Account Log</title>
    </head>
    <body>
        <h1>Account Log Entries for: ${card.accountId}</h1>        
        <table>
            <tr>
                <th>Date/Time</th>
                <th>Description</th>
            </tr>
                <%
                    ArrayList<String> transactionList = (ArrayList<String>) request.getAttribute("log");
                    for(int i = 0; i < transactionList.size(); i++) {
                %>
            <tr>
                <td><%= transactionList.get(i) %></td>
            </tr>
            <% } %>
        </table>
        
    </body>
</html>
