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

public class MovieDBManager extends DBManager {

	private PreparedStatement addMovieToCustomerStmt;
	private PreparedStatement findAllMoviesStmt;
	private PreparedStatement findMovieByIdStmt;
	private PreparedStatement createNewMovieStmt;

	public MovieDBManager() {

		try {
			addMovieToCustomerStmt = conn
					.prepareStatement("INSERT INTO movie(director,"
							+ "available,price,title,item_type_id,customer_id) VALUES(?,?,?,?,?,?)");

			createNewMovieStmt = conn
					.prepareStatement("INSERT INTO movie(director,"
							+ "available,price,title,item_type_id,customer_id) VALUES(?,?,?,?,?,null)");

			findAllMoviesStmt = conn.prepareStatement("select * from movie");
			findMovieByIdStmt = conn
					.prepareStatement("select * from movie where id=?");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Movie findMovieById(long id) {
		Movie movie = null;
		try {
			findMovieByIdStmt.setLong(1, id);
			ResultSet rs = findMovieByIdStmt.executeQuery();

			while (rs.next()) {
				movie = new Movie(rs.getString("title"), ItemType.DVD,
						rs.getString("director"), rs.getFloat("price"));
				movie.setId(rs.getInt("id"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return movie;
	}
	
	public int deleteAllMovies(){
		try {
			return conn.prepareStatement("TRUNCATE TABLE movie").executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
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

	public void createNewMovie(Movie movie) throws SQLException {
		addMovieToCustomerStmt.setString(4, movie.getTitle());
		addMovieToCustomerStmt.setBoolean(2, true);
		addMovieToCustomerStmt.setString(1, movie.getDirector());
		addMovieToCustomerStmt.setFloat(3, movie.getPrice());
		addMovieToCustomerStmt.setFloat(5, 1);
		addMovieToCustomerStmt.setFloat(6, 0);
		addMovieToCustomerStmt.execute();
	}
}
