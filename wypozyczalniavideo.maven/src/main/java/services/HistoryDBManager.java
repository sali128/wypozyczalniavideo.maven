package services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sala.patryk.projekt.wypozyczalniavideo.Customer;
import sala.patryk.projekt.wypozyczalniavideo.History;
import sala.patryk.projekt.wypozyczalniavideo.Movie;

public class HistoryDBManager extends DBManager {

	private PreparedStatement addMovieToCustomerStmt;
	private PreparedStatement findHistoryForCustomerStmt;
	private PreparedStatement addHistoryForUserStmt;

	public HistoryDBManager() {

		try {
			findHistoryForCustomerStmt = conn
					.prepareStatement("select * from history where customer_id=?");
			addHistoryForUserStmt = conn
					.prepareStatement("insert into history(customer_id,movie_id,timestamp) values(?,?,NOW())");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addHistory(Movie movie, Customer customer) {
		try {
			addHistoryForUserStmt.setLong(1, customer.getId());
			addHistoryForUserStmt.setLong(2, movie.getId());
			addHistoryForUserStmt.execute(); 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void deleteHistoryForCustomer(Customer customer){
		try {
			PreparedStatement statement = conn.prepareStatement("delete from history where customer_id=?");
			statement.setLong(1, customer.getId());
			statement.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<History> getHistoryForCustomer(Customer customer) {
		List<History> histories = new ArrayList<History>();
		try {
			findHistoryForCustomerStmt.setLong(1, customer.getId());
			ResultSet rs = findHistoryForCustomerStmt.executeQuery();

			while (rs.next()) {
				History history = new History();
				history.setCustomerId(rs.getLong("customer_id"));
				history.setMovieId(rs.getLong("movie_id"));
				history.setTimestamp(rs.getTimestamp("timestamp"));
				histories.add(history);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return histories;
	}

	public void addMovieToCustomer(Customer customer, Movie movie)
			throws SQLException {
		long id = customer.getId();
		addMovieToCustomerStmt.setString(4, movie.getTitle());
		addMovieToCustomerStmt.setBoolean(2, true);
		addMovieToCustomerStmt.setString(1, movie.getDirector());
		addMovieToCustomerStmt.setFloat(3, movie.getPrice());
		addMovieToCustomerStmt.setFloat(6, id);
		addMovieToCustomerStmt.setFloat(5, 1);

		addMovieToCustomerStmt.execute();
	}
}
