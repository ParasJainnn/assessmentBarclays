package com.TradeStore.register;

import java.io.IOException;
import java.util.Date;  
import java.io.PrintWriter;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/TradeStore")
public class TradeStore extends HttpServlet{
	
	//create the query
	private static final String INSERT_QUERY ="INSERT INTO trade_strore(TradeId,Version,CounterPartyId,BookId,MaturityDate,CreatedDate,Expired) VALUES(?,?,?,?,?,?,?)";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		//get PrintWriter
		PrintWriter pw = res.getWriter();
		//set Content type
		res.setContentType("text/hmtl");
		//read the form values
		
		
		String TradeId = req.getParameter("TradeId");
		String Version = req.getParameter("Version");
		String CounterPartyId = req.getParameter("CounterPartyId");
		String BookId = req.getParameter("BookId");
		String MaturityDate = req.getParameter("MaturityDate");
		String CreatedDate = req.getParameter("CreatedDate");
		String Expired = req.getParameter("Expired");
	
		
		//load the jdbc driver
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//create the connection
		try{
			Connection con = DriverManager.getConnection("jdbc:mysql:///barclaysassesment","root","Paras@08");

			Statement st = con.createStatement();

			String get_query ="SELECT * FROM trade_strore where TradeId="+TradeId +";";

			ResultSet rs = st.executeQuery(get_query);
			
			 // now check if trade id already exist 
			if(rs.next()==true) {
				while (rs.next()) {
					int existversion =rs.getInt("Version");
					int newversion =Integer.parseInt(Version);
					 SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
					
					 Date currentdate = new Date();
					 String newMaturityDate =rs.getString("MaturityDate");
					 
					 Date maturityDate1= (Date) sdformat.parse(newMaturityDate);
				
					 if((maturityDate1.compareTo(currentdate) > 0)) {
						 
						 String update_query1="update  trade_strore set (Expired) VALUES('y')";
							
							executeQuery(update_query1);
					 }
					if(existversion>newversion ||(currentdate.compareTo(maturityDate1) > 0) ) {
						pw.println("rejected the trade bcz of lower version or less maturity date   ");
					}
					else if(existversion==newversion) {
						String update_query="update  trade_strore set (TradeId,Version,CounterPartyId,BookId,MaturityDate,CreatedDate,Expired) VALUES(TradeId,Version,CounterPartyId,BookId,MaturityDate,CreatedDate,Expired)";
				
						executeQuery(update_query);
					}
				
				}
				
				
			}
			// if not exist than insert it into the database 
			else {
		
			PreparedStatement ps = con.prepareStatement(INSERT_QUERY);
			//set the values
			ps.setString(1, TradeId);
			ps.setString(2, Version);
			ps.setString(3, CounterPartyId);
			ps.setString(4, BookId);
			ps.setString(5, MaturityDate);
			ps.setString(6, CreatedDate);
			ps.setString(7, Expired);
			int count = ps.executeUpdate();
		

			
			}
			
			
			//execute the query
			
		}catch(SQLException se) {
			pw.println(se.getMessage());
			se.printStackTrace();
		}catch(Exception e) {
			pw.println(e.getMessage());
			e.printStackTrace();
		}
		
		//close the stream
		pw.close();
	}
	
	private void executeQuery(String update_query) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(req, resp);
	}
}
