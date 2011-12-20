package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sala.patryk.projekt.wypozyczalniavideo.Customer;
import sala.patryk.projekt.wypozyczalniavideo.ItemType;
import sala.patryk.projekt.wypozyczalniavideo.Movie;

public class MovieDBManager {

	private PreparedStatement addMovieToCustomerStmt;
	private PreparedStatement findAllMoviesStmt;
	private static Connection conn;

	public MovieDBManager(Connection conn) throws SQLException {

		this.conn = conn;

		addMovieToCustomerStmt = conn
				.prepareStatement("INSERT INTO MOVIE (director,"
						+ "available,price,title,item_type_id,customer_id) VALUES(?,?,?,?,?,?)");
	
		findAllMoviesStmt = conn.prepareStatement("select * from movie");
	}

	

	public List<Movie> findAllMovies() {

		List<Movie> movies = new ArrayList<Movie>();

		try {
			ResultSet rs = findAllMoviesStmt.executeQuery();

			while (rs.next()) {
				Movie movie = new Movie(rs.getString("title"), ItemType.DVD,
						rs.getString("director"), rs.getFloat("price"));
				movie.setId(rs.getInt("id"));
				movies.add(movie);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return movies;
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
