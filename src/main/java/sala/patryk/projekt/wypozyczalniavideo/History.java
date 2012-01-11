package sala.patryk.projekt.wypozyczalniavideo;

import java.sql.Timestamp;

import services.MovieDBManager;

/**
 * @author maestr0
 * 
 */
public class History {
	private long customerId;
	private long movieId;
	private Timestamp timestamp;

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public long getMovieId() {
		return movieId;
	}

	public void setMovieId(long movieId) {
		this.movieId = movieId;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public Movie getMovie() {
		Movie movieById = new MovieDBManager().findMovieById(movieId);
		return movieById;
	}

	@Override
	public String toString() {
		return "History [customerId=" + customerId + ", videoId=" + movieId
				+ ", timestamp=" + timestamp + "]";
	}

}
