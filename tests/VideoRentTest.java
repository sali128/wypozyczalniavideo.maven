package sala.patryk.projekt.wypozyczalniavideo.tests;

import java.sql.SQLException;
import java.util.List;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sala.patryk.projekt.wypozyczalniavideo.Customer;
import sala.patryk.projekt.wypozyczalniavideo.History;
import sala.patryk.projekt.wypozyczalniavideo.InvalidMoneyAmountValue;
import sala.patryk.projekt.wypozyczalniavideo.ItemType;
import sala.patryk.projekt.wypozyczalniavideo.Movie;
import sala.patryk.projekt.wypozyczalniavideo.NoMoneyException;
import sala.patryk.projekt.wypozyczalniavideo.VideoRental;
import services.CustomerDBManager;
import services.HistoryDBManager;
import services.MovieDBManager;

public class VideoRentTest {

	private static final Movie gladiator = new Movie("Gladiator", ItemType.DVD,
			"R. Scott", 29.99F);
	private static final Movie smurfs = new Movie("Smerfy", ItemType.TAPE,
			"Peyo", 14.99F);
	private static final Movie kubusRevenge = new Movie(
			"Kubus Puchatek: Zemsta Prosiaczka", ItemType.DVD, "Jan Nowak",
			12.99F);
	private static final Movie kubusPowrot = new Movie(
			"Kubus Puchatek: Powrot", ItemType.DVD, "Jan Nowak", 29.99F);
	private static final Movie kubusPuchatek = new Movie("Kubus Puchatek",
			ItemType.TAPE, "Jan Nowak", 9.99F);
	private static final Movie wPustyniIwPuszczy = new Movie(
			"W pustyni i w puszczy", ItemType.TAPE, "Jan Nowak", 19.99F);
	private static final Movie batman = new Movie("Batman", ItemType.DVD,
			"Adam Xsinski", 13);
	private static final Movie drHouseMovie = new Movie("Dr House",
			ItemType.CD, "Pawel Nazwisko", 5);
	private static final Movie dzienSwira = new Movie("Dzien swira",
			ItemType.DVD, "Marek Koterski", 20.1F);
	private VideoRental videoRental;

	@Before
	public void setUp() throws Exception {
		videoRental = new VideoRental();
		videoRental.addNewMovie(wPustyniIwPuszczy);
		videoRental.addNewMovie(batman);
		videoRental.addNewMovie(drHouseMovie);
		videoRental.addNewMovie(kubusPuchatek);
		videoRental.addNewMovie(kubusPowrot);
		videoRental.addNewMovie(kubusRevenge);
		videoRental.addNewMovie(smurfs);
		videoRental.addNewMovie(gladiator);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void showAllMoviesInVieoRentalTest() {
		videoRental.printAllMovies();
	}

	@Test
	public void removeMovieFromVieoRentalTest() {
		Movie smerfy = videoRental.findMovieByTitle("Smerfy");
		videoRental.printAllMovies();
		videoRental.removeMovieFromVideoRental(smerfy);
		videoRental.printAllMovies();
	}

	@Test
	public void historyTableTest() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {

		Customer customer = null;
		CustomerDBManager customerDBManager = new CustomerDBManager();
		MovieDBManager movieDBManager = new MovieDBManager();
		try {
			customer = new Customer("Pawel Kowalski TESTER", 15500);
			customerDBManager.addCustomer(customer);
			customer = new Customer("Adam Xinski TESTER", 1420);
			customerDBManager.addCustomer(customer);

			movieDBManager.createNewMovie(wPustyniIwPuszczy);

			movieDBManager.createNewMovie(new Movie(
					"W pustyni i w puszczy: ZEMSTA NELL", ItemType.DVD,
					"Jan Nowak", 25.99F));
		} catch (InvalidMoneyAmountValue e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// znajdz dwoch ostatnich klietow w bazie
		List<Customer> allcustomers = customerDBManager.getAllcustomers();
		Customer customer1 = allcustomers.get(allcustomers.size() - 1);
		Customer customer2 = allcustomers.get(allcustomers.size() - 2);
		// znajdz dwa ostatnie filmy w bazie
		List<Movie> findAllMovies = new MovieDBManager().findAllMovies();
		Movie movie1 = findAllMovies.get(findAllMovies.size() - 1);
		Movie movie2 = findAllMovies.get(findAllMovies.size() - 2);

		// wypozycz filmy
		customer1.takeMovieAndCreateHistoryLog(movie1);
		customer1.takeMovieAndCreateHistoryLog(movie2);

		customer2.takeMovieAndCreateHistoryLog(movie2);
		customer2.takeMovieAndCreateHistoryLog(movie1);
		customer2.takeMovieAndCreateHistoryLog(movie2);

		// pokaz historie dla klienta
		customer1.showHistory();
		customer2.showHistory();

	}

	@Test
	public void manyToManyRelationTest() {
		System.out
				.println("\n\n\n********************** TEST LACZENIA WIELE DO WIELU NA PRZYKLADZIE TABELKI HISTORY **********************\n");
		try {
			HistoryDBManager historyDBManager = new HistoryDBManager();
			MovieDBManager movieDBManager = new MovieDBManager();
			CustomerDBManager customerDBManager = new CustomerDBManager();

			Customer customer = new Customer("Adam Kowalski", 600);
			customerDBManager.addCustomer(customer);
			Customer customerSAVEDinDB = customerDBManager
					.findCustomerByName("Adam Kowalski");

			historyDBManager.deleteHistoryForCustomer(customerSAVEDinDB);
			// clear MOVIES table

			int deleteAllMoviesStatus = movieDBManager.deleteAllMovies();
			Assert.assertTrue(
					"Blad, nie mozna usunac wszystkich filmow z tabeli MOVIE, STATUS="
							+ deleteAllMoviesStatus, deleteAllMoviesStatus == 1);
			// add movies to DB
			movieDBManager.createNewMovie(dzienSwira);
			movieDBManager.createNewMovie(gladiator);
			movieDBManager.createNewMovie(kubusRevenge);
			movieDBManager.createNewMovie(kubusPowrot);
			movieDBManager.createNewMovie(smurfs);
			movieDBManager.createNewMovie(wPustyniIwPuszczy);

			List<Movie> allMoviesInDatabase = movieDBManager.findAllMovies();

			// wypozyczymy film kilka razy i zweryfikujemy czy jest to
			// odnotowane w historii
			for (Movie movie : allMoviesInDatabase) {
				movie.setAvailable(true);
				videoRental.rentMovie(customerSAVEDinDB, movie);
			}

			int numberOfRentedMovies = allMoviesInDatabase.size();

			customerSAVEDinDB.showHistory();

			List<History> historyForCustomer = historyDBManager
					.getHistoryForCustomer(customerSAVEDinDB);

			Assert.assertNotNull("BLAD ! brak historii dla kienta",
					historyDBManager);
			Assert.assertTrue(
					" BLAD! ilosc wpisow w tabelce HISTORY nie zgadza sie z iloscia wypozyczonych fimow",
					historyForCustomer.size() == numberOfRentedMovies);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidMoneyAmountValue e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoMoneyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out
				.println("\n\n\n********************** KONIEC **********************\n");
	}

	@Test
	public void testTRANSAKCJI() {
		// DBMANAGER

		Customer c = null;
		Customer c2 = null;
		try {
			c = new Customer("Michal XABASDAS", 200);
			c2 = new Customer("Pawel ASDASD", 100);
		} catch (InvalidMoneyAmountValue e) {
			e.printStackTrace();
		}
		try {

			CustomerDBManager db = new CustomerDBManager();

			MovieDBManager mdb = new MovieDBManager();

			// TRANSACTIONS

			int beforeTransaction = db.getAllcustomers().size();

			db.startTransaction();
			db.addCustomer(c);
			db.addCustomer(c2);
			// nic nie jest jeszcze zapisane w DB

			db.rolbackTransaction(); // wycofanie transackcji

			int afterTransactionRolback = db.getAllcustomers().size();
			Assert.assertTrue("Wynik przed transakcja i po jest rozny, BLAD!",
					beforeTransaction == afterTransactionRolback);

			List<Customer> allcustomers = db.getAllcustomers();

			System.out.println("Lista Klientow po wycofaniu transakcji");
			for (Customer customer : allcustomers) {
				System.out.println(customer);
			}
			beforeTransaction = db.getAllcustomers().size();
			db.startTransaction();

			db.addCustomer(c);
			db.addCustomer(c2);

			db.commitTransaction();

			allcustomers = db.getAllcustomers();

			afterTransactionRolback = db.getAllcustomers().size();
			Assert.assertTrue(
					"Wynik przed transakcja i po jest TAKI SAM, BLAD!",
					beforeTransaction < afterTransactionRolback);

			System.out.println("Lista Klientow po zakomitowaniu transakcji");
			for (Customer customer : allcustomers) {
				System.out.println(customer);
			}

			Customer customer = allcustomers.get(0);

			mdb.addMovieToCustomer(customer, gladiator);
			List<Movie> movies = mdb.findAllMovies();

			for (Movie movie : movies) {
				System.out.println(movie);
			}

			for (Customer cc : db.getAllcustomers()) {
				System.out.println(cc);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void rentMovieTest() {
		Customer customer = null;
		try {
			customer = new Customer("Pawel Kowalski", 200);
		} catch (InvalidMoneyAmountValue e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Movie houseMovie = videoRental.findMovieByTitle("Dr House");

		try {
			videoRental.rentMovie(customer, houseMovie);
			System.out
					.println("Sprawdzanie czy wypozyczony film ma FLAGE AVAILABLE=FALSE");
			boolean available = houseMovie.isAvailable();
			Assert.assertFalse(
					"BLAD! film mimo wypozyczenia ma flage dostepnosci = true",
					available);
			System.out.println("Udalo sie wypozyczyc film "
					+ houseMovie.getTitle());
		} catch (NoMoneyException e) {
			e.printStackTrace();
			Assert.fail("Wystapil wyjatek w czasie proby wypozyczenia filmu: "
					+ e.getMessage());
		}

	}

	@Test
	public void settingPriceTest() {
		float newPrice = 555;
		videoRental.setNewPriceForMovie("Dr House", newPrice);
		Movie movie = videoRental.findMovieByTitle("Dr House");
		Assert.assertEquals(
				"BLAD, Nie udalo sie ustawic nowej ceny dla filmu ",
				movie.getPrice(), newPrice);

	}

	@Test
	public void findByDirectorTest() {
		System.out.println("Wyszukiwanie po rezyserze...");
		List<Movie> resultList = videoRental.findMovieByDirector("Jan Nowak");
		Assert.assertNotNull(
				"Blad, lista zwrocona z metody wyszukujacej po rezyserze zwrocila NULL",
				resultList);
		Assert.assertTrue(
				"Blad, lista zwrocona z metody wyszukujacej po rezyserze jest pusta",
				resultList.size() > 0);
		if (resultList.size() > 0) {
			for (int i = 0; i < resultList.size(); i++) {
				System.out
						.println("Znaleziono " + resultList.get(i).toString());
			}
		}
	}

	@Test
	public void findByDataCarrierTypeTest() {
		System.out.println("Wyszukiwanie po typie nosnika...");
		List<Movie> resultList = videoRental.findMovieByType("DVD");
		Assert.assertNotNull(
				"Blad, lista zwrocona z metody wyszukujacej po rodzaju nosnika zwrocila NULL",
				resultList);
		Assert.assertTrue(
				"Blad, lista zwrocona z metody wyszukujacej po rodzaju nosnika jest pusta",
				resultList.size() > 0);
		if (resultList.size() > 0) {
			for (int i = 0; i < resultList.size(); i++) {
				System.out
						.println("Znaleziono " + resultList.get(i).toString());
			}
		}
	}

	@Test
	public void findByTitleTest() {
		Movie smerfy = videoRental.findMovieByTitle("Smerfy");
		Assert.assertNotNull(
				"Blad, nie znaleziono filmu Smerfy szukajac po tytule", smerfy);
		if (smerfy != null)
			System.out.println("Znaleziono film Smerfy wyszujujac po tytule");
	}

	@Test(expected = InvalidMoneyAmountValue.class)
	public void createCustomerWithNegativeAmountOfMoneyTest()
			throws InvalidMoneyAmountValue {
		System.out.println("Test wyjatku InvalidMoneyAmountValue ");
		Customer customer1 = new Customer("Pawel Kowalski", -200);
	}

	@Test(expected = NoMoneyException.class)
	public void clientHasNoMoneyToPayTest() throws InvalidMoneyAmountValue,
			NoMoneyException {
		System.out.println("Test wyjatku NoMoneyException ");
		Customer customer1 = new Customer("Pawel Kowalski", 1);
		Movie smerfy = videoRental.findMovieByTitle("Smerfy");
		Assert.assertNotNull("nie znaleziono filmu Smerfy", smerfy);
		System.out.println("Znaleziono film Smerfy");
		videoRental.rentMovie(customer1, smerfy);
	}
}
