package services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import sala.patryk.projekt.wypozyczalniavideo.Customer;
import sala.patryk.projekt.wypozyczalniavideo.InvalidMoneyAmountValue;

public class CustomerDBManager extends DBManager {

	private Statement stmt;

	private PreparedStatement addcustomerStmt;
	private PreparedStatement getcustomersStmt;
	private PreparedStatement findByNameStmt;
	private PreparedStatement deleteCustomerStmt;

	public CustomerDBManager() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		try {
			
			findByNameStmt = conn.prepareStatement("SELECT * FROM CUSTOMER WHERE name=?");

			addcustomerStmt = conn.prepareStatement(""
					+ "INSERT INTO customer (name, money) VALUES (?,?)");

			getcustomersStmt = conn.prepareStatement("SELECT * FROM customer");

			deleteCustomerStmt = conn.prepareStatement("DELETE FROM customer");

		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	public void addCustomer(Customer p) {
		try {
			addcustomerStmt.setString(1, p.getName());
			addcustomerStmt.setFloat(2, p.getCash());
			addcustomerStmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Customer findCustomerByName(String customerName){
		List<Customer> customers = new ArrayList<Customer>();

		try {
			findByNameStmt.setString(1, customerName);
			ResultSet rs = findByNameStmt.executeQuery();

			while (rs.next()) {
				Customer customer = new Customer(rs.getString("name"),
						rs.getFloat(("money")));
				customer.setId(rs.getLong("id"));

				return customer;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidMoneyAmountValue e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

		return null;
	}

	public List<Customer> getAllcustomers() {
		List<Customer> customers = new ArrayList<Customer>();

		try {
			ResultSet rs = getcustomersStmt.executeQuery();

			while (rs.next()) {
				Customer customer = new Customer(rs.getString("name"),
						rs.getFloat(("money")));
				customer.setId(rs.getLong("id"));

				customers.add(customer);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidMoneyAmountValue e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return customers;
	}

	public void deleteAllPerson() {
		try {
			deleteCustomerStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
