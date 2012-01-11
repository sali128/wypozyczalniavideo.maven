package sala.patryk.projekt.wypozyczalniavideo;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import services.HistoryDBManager;

public class Customer {
	private long id;
	private Logger logger = Logger.getLogger(Customer.class);
	private final String name;
	private float cash;
	private List<Movie> myVideoList;

	public Customer(String name, float money) throws InvalidMoneyAmountValue {
		myVideoList = new ArrayList<Movie>();
		this.name = name;
		if (money < 0) {
			throw new InvalidMoneyAmountValue();
		}
		this.cash = money;
	}

	
	public String getName() {
		return name;
	}
	
	public void showHistory() {
		List<History> historyForCustomer = new HistoryDBManager().getHistoryForCustomer(this);
		for (History history : historyForCustomer) {
			System.out.println(this.getName() + " wypozyczyl " + history.getMovie() + " w dniu " + history.getTimestamp());
		}
	}


	public void showAllMyRentedMovies() {
		logger.info("Lista filmow klienta o nazwisku: " + name);
		for (Movie movie : myVideoList) {
			logger.info(movie.toString());
		}
	}

	public void takeMovieAndCreateHistoryLog(Movie movie) {
		myVideoList.add(movie);

		// add history information
		new HistoryDBManager().addHistory(movie, this);
	}

	public Movie returnMovie(String title) {
		for (Movie movie : myVideoList) {
			if (movie.getTitle().equals(title))
				return movie;
		}
		return null;
	}

	public float payMoney(float priceToPay) throws NoMoneyException {
		if (cash - priceToPay >= 0) { // chce zaplacic jesli mam pieniadze
			cash = cash - priceToPay;
			return priceToPay;
		}
		throw new NoMoneyException(); // ale jak nie mam pieniedzy to nie place
	}



	@Override
	public String toString() {
		return "Customer [id=" + id + ", name=" + name
				+ ", cash=" + cash + ", myVideoList=" + myVideoList + "]";
	}


	public float getCash() {
		return cash;
	}


	public void setCash(float cash) {
		this.cash = cash;
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}

}
