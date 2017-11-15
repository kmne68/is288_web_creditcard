/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import business.CreditCard;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author kmne6
 */
@WebServlet(name = "AccountActionServlet", urlPatterns = {"/AccountAction"})
public class AccountActionServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        int acctno = 0;
        // double creditLimit = 0;
        double balanceDue = 0;
        double availableCredit = 0;
        String URL = "/CardTrans.jsp";
        String msg = "";
        String emsg = "";
        ArrayList<String> log;
        double charge = 0;

        CreditCard card;
        //double charge = 0;

        try {
            String path = getServletContext().getRealPath("/WEB-INF/") + "\\";

            String action = request.getParameter("actiontype");

            card = (CreditCard) request.getSession().getAttribute("card");

            if (card == null && !action.equalsIgnoreCase("NEW") && !action.equalsIgnoreCase("EXISTING")) {
                msg = "No active account<br>";
            } else {
                if (action.equalsIgnoreCase("NEW")) {
                    // open new account
                    card = new CreditCard(path); // send them to folder for new accounts
                    if (card.getErrorStatus()) {
                        msg += "Open error: " + card.getErrorMessage() + "<br>";
                    } else {
                        msg += card.getActionMsg() + "TEST ACTION MESSAGE<br>";
                    }
                }

                
                if (card == null && (action.equalsIgnoreCase("existing"))) {

                    try {
                        // add try/catch
                        acctno = Integer.parseInt(request.getParameter("account"));
                        card = new CreditCard(acctno, path);
                    } catch (Exception e) {
                        emsg += card.getErrorMessage() + " " + e;
                    //    request.setAttribute("msg", msg);
                    }
                    double creditLimit = card.getCreditLimit();
                    try {
               /*         request.setAttribute("climit", creditLimit);
                        request.setAttribute("msg", acctno);
                        request.setAttribute("test", "<br>" + acctno);
               */     } catch (Exception e) {
                        msg += "Card data unavailable " + e;
                    //    request.setAttribute("msg", msg);
                    }
                }

                //    request.getSession().setAttribute("card", card);
                if (card != null && (action.equalsIgnoreCase("charge"))) {
                    try {
                        charge = Double.parseDouble(request.getParameter("cAmt"));
                        msg += request.getParameter("cDesc");
                        if (charge <= 0) {
                            emsg += "Charge must be a positive value<br>";
                        } else {
                            card.setCharge(charge, msg);
                            msg += "-- debit recorded.";
                        }

                    } catch (Exception e) {
                        emsg += "Illegal value.<br>";
                    }
                }

                if (card != null && (action.equalsIgnoreCase("payment"))) {
                    try {
                        double payment = Double.parseDouble(request.getParameter("pAmt"));
                        if (payment <= 0 || card.getOutstandingBal() == 0) {
                            emsg += "Payment must be a positive value and cannot exceed your outstanding balance<br>";
                        } else {
                            card.setPayment(payment);
                            msg += "Payment received.";
                        }

                    } catch (Exception e) {
                        emsg += "Illegal value.<br>";
                    }
                }

                if (card != null && (action.equalsIgnoreCase("increase"))) {
                    try {
                        double increase = Double.parseDouble(request.getParameter("cIncrease"));
                        if (increase <= 0) {
                            emsg += "A credit increase must be a positive value. <br>";
                        } else {
                            card.setCreditIncrease(increase);
                            msg += "Your credit limit has been increased.";
                        }

                    } catch (Exception e) {
                        emsg += "Illegal value.<br>";
                    }
                }

                
                if (card != null && (action.equalsIgnoreCase("interest"))) {
                    try {
                        double rate = Double.parseDouble(request.getParameter("iRate"));
                        if (rate <= 0) {
                            emsg += "The interest rate must be greater than 0.<br>";
                        } else {
                            card.setInterestCharge(rate);
                            msg += "An interest charge of has been applied to the account.";
                        }
                    } catch (Exception e) {
                        emsg += "Illegal  value.<br>";
                    }
                }
                

                if (card != null && (action.equalsIgnoreCase("history"))) {
                    URL = "/History.jsp";

                    try {
                        //    path = getServletContext().getRealPath("/build/web/WEB-INF/CCL" + String.valueOf(card.getAccountId() + ".txt") );
                        log = card.getCreditHistory();

                        request.setAttribute("log", log);
                    } catch (Exception e) {
                        emsg = "History processing error: " + e;
                        URL = "/CardTrans.jsp";
                     //   request.setAttribute("emsg", emsg);
                    }
                }

                request.getSession().setAttribute("card", card);    // update the session object "card" was "path"

                Cookie acct = new Cookie("acct", String.valueOf(card.getAccountId()));

                acct.setMaxAge(60 * 2); // 60 seconds times 2
                acct.setPath("/");      // specify where the cookie is availalbe, we've selected root
                response.addCookie(acct);   // goes back to the browser which manages the cookie

            } // end else
        } catch (Exception e) {
            msg = "Error" + e.getMessage();
        }

        request.setAttribute("msg", msg);
        request.setAttribute("emsg", emsg);
        RequestDispatcher disp = getServletContext().getRequestDispatcher(URL);
        disp.forward(request, response);

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
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
     * Handles the HTTP <code>POST</code> method.
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
