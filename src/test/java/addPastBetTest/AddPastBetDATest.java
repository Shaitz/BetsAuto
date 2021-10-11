package addPastBetTest;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;
import configuration.ConfigXML;
import dataAccess.DataAccess;
import domain.Bet;
import domain.Event;
import domain.Question;
import domain.User;
import utility.TestUtilityDataAccess;

class AddPastBetDATest {

	static DataAccess sut = new DataAccess(ConfigXML.getInstance().getDataBaseOpenMode().equals("initialize"));
	//static TestUtilityDataAccess testDA = new TestUtilityDataAccess();
	static TestUtilityDataAccess testDA = new TestUtilityDataAccess();

	private Event ev;

	@Test
	// sut.addPastBet: Usuario es null
	void test1a() {

		// configure the state of the system (create object in the dabatase)
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date oneDate;
		try {
		oneDate = sdf.parse("05/10/2022");
		String eventText = "Event Text";
		User u = null;
		
		testDA.open();
		ev = testDA.addEventWithQuestion(eventText, oneDate, "otra", 10.0f);
		testDA.close();
		
		Question q = new Question(eventText, 10.0f, ev);
		Bet bet = new Bet(q, 5, 2, "Test");
		
		// invoke System Under Test (sut) and Assert
		assertThrows(RuntimeException.class, () -> sut.addPastBet(u, bet, 5));
		} catch (ParseException e) {
			fail("It should be correct: check the date format");
		}
		// Remove the created objects in the database (cascade removing)
		testDA.open();
		boolean b = testDA.removeEvent(ev);
		System.out.println("Removed event " + b);
		testDA.close();

	}
	
	@Test
	// sut.addPastBet: Bet es null
	void test1b() {

		// configure the state of the system (create object in the dabatase)
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date oneDate;
		try {
		oneDate = sdf.parse("05/10/2022");
		String eventText = "Event Text";
		User u = new User("TestTest", "123456789", "email@email.test");;
		
		testDA.open();
		ev = testDA.addEventWithQuestion(eventText, oneDate, "otra", 10.0f);
		testDA.close();
		
		Bet bet = null;
		
		// invoke System Under Test (sut) and Assert
		assertThrows(RuntimeException.class, () -> sut.addPastBet(u, bet, 5));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			fail("It should be correct: check the date format");
		}
		// Remove the created objects in the database (cascade removing)
		testDA.open();
		boolean b = testDA.removeEvent(ev);
		System.out.println("Removed event " + b);
		testDA.close();

	}
	
	@Test
	// sut.addPastBet: BenefitUser es menor que cero
	void test1c() {

		// configure the state of the system (create object in the dabatase)
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date oneDate;
		try {
		oneDate = sdf.parse("05/10/2022");
		String eventText = "Event Text";
		User u = new User("TestTest", "123456789", "email@email.test");;
		
		testDA.open();
		ev = testDA.addEventWithQuestion(eventText, oneDate, "otra", 10.0f);
		testDA.close();
		
		Question q = new Question(eventText, 10.0f, ev);
		Bet bet = new Bet(q, 5, 2, "Test");
		
		// invoke System Under Test (sut) and Assert
		assertThrows(RuntimeException.class, () -> sut.addPastBet(u, bet, -5));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			fail("It should be correct: check the date format");
		}
		// Remove the created objects in the database (cascade removing)
		testDA.open();
		boolean b = testDA.removeEvent(ev);
		System.out.println("Removed event " + b);
		testDA.close();

	}

	@Test
	// sut.addPastBet: Usuario no en BD
	void test2() {

		// configure the state of the system (create object in the dabatase)
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date oneDate;
		try {
		oneDate = sdf.parse("05/10/2022");
		String eventText = "Event Text";
		
		testDA.open();
		ev = testDA.addEventWithQuestion(eventText, oneDate, "otra", 10.0f);
		testDA.close();
		
		Question q = new Question(eventText, 10.0f, ev);
		Bet bet = new Bet(q, 5, 2, "Test");
		User u = new User("TestTestTest", "1234567899", "email@email.email");
		testDA.open();
		User us = testDA.getUserWithUsernamePassword(u.getUsername(), u.getPassword());
		testDA.close();
		
		// invoke System Under Test (sut) and Assert
		assertNull(us);
		assertThrows(RuntimeException.class, () -> sut.addPastBet(u, bet, 5));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			fail("It should be correct: check the date format");
		}
		// Remove the created objects in the database (cascade removing)
		testDA.open();
		boolean b = testDA.removeEvent(ev);
		System.out.println("Removed event " + b);
		testDA.close();

	}
	
	@Test
	// sut.addPastBet: Usuario no tiene bets
	void test3() {

		// configure the state of the system (create object in the dabatase)
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date oneDate;
		User u = new User("TestTest", "123456789", "email@email.test");
		testDA.open();
		User user = testDA.createUser(u.getUsername(), u.getPassword(), u.getMail());
		User u1 = testDA.getUserWithUsernamePassword(u.getUsername(), u.getPassword());
		testDA.close();
		try {
		
		oneDate = sdf.parse("05/10/2022");
		String eventText = "Event Text";
		
		testDA.open();
		ev = testDA.addEventWithQuestion(eventText, oneDate, "otra", 10.0f);
		testDA.close();
		
		Question q = new Question(eventText, 10.0f, ev);
		Bet bet = new Bet(q, 5, 2, "Test");

		// invoke System Under Test (sut) and Assert
		assertNotNull(u1);
		assertThrows(RuntimeException.class, () -> sut.addPastBet(user, bet, 5));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			fail("It should be correct: check the date format");
		}
		// Remove the created objects in the database (cascade removing)
		testDA.open();
		boolean b = testDA.removeEvent(ev);
		boolean b2 = testDA.removeUser(user);
		System.out.println("Removed event " + b + b2);
		testDA.close();

	}
	
	@Test
	// sut.addPastBet: Usuario tiene bets pero no ha apostado en bet
	void test4() {

		// configure the state of the system (create object in the dabatase)
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date oneDate;
		User u = new User("TestTest", "123456789", "email@email.test");
		testDA.open();
		testDA.createUser(u.getUsername(), u.getPassword(), u.getMail());
		User u1 = testDA.getUserWithUsernamePassword(u.getUsername(), u.getPassword());
		testDA.close();
		try {
			
		oneDate = sdf.parse("05/10/2022");
		String eventText = "Event Text";
		String queryText2 = "Query Text2";

		testDA.open();
		ev = testDA.addEventWithQuestion(eventText, oneDate, "otra", 10.0f);
		Event ev2 = testDA.addEventWithQuestion(eventText, oneDate, queryText2, 10.0f);
		testDA.close();
	
		Question q2 = ev2.getQuestions().get(0);
		
		//Bet bet = new Bet(q, 5, 2, "Test");
		
		testDA.open();
		testDA.addMoneyToUser(u1.getId(), 2000000);
		testDA.placeBet(u1, q2, 120, "1");
		User u2 = testDA.getUserWithUsernamePassword(u1.getUsername(), u1.getPassword());
		testDA.close();
		// invoke System Under Test (sut) and Assert
		assertNotNull(u2);
		assertNotEquals(u2.getBets().size(), 0);
		assertThrows(RuntimeException.class, () -> sut.addPastBet(u1, u1.getBets().get(0), 5));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			fail("It should be correct: check the date format");
		}

		// Remove the created objects in the database (cascade removing)
		testDA.open();
		boolean b = testDA.removeEvent(ev);
		boolean b2 = testDA.removeUser(u1);
		System.out.println("Removed event " + b + b2);
		testDA.close();
		
	}
	
	@Test
	// sut.addPastBet: Usuario tiene bets y ha acertado en la apuesta
	void test5() {

		// configure the state of the system (create object in the dabatase)
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date oneDate;
		User u = new User("TestTest", "123456789", "email@email.test");
		try {
			
		oneDate = sdf.parse("05/10/2022");
		String eventText = "Event Text";
		
		testDA.open();
		ev = testDA.addEventWithQuestion(eventText, oneDate, "otra", 10.0f);
		testDA.close();
		
		Question q = ev.getQuestions().get(0);
		testDA.open();
		testDA.createUser(u.getUsername(), u.getPassword(), u.getMail());
		User u1 = testDA.getUserWithUsernamePassword(u.getUsername(), u.getPassword());
		testDA.addMoneyToUser(u1.getId(), 2000000);
		testDA.placeBet(u1, q, 120, "1");
		User u2 = testDA.getUserWithUsernamePassword(u1.getUsername(), u1.getPassword());
		testDA.close();
		// invoke System Under Test (sut) and Assert
		assertNotNull(u2);
		assertNotEquals(u2.getBets().size(), 0);
		
		sut.addPastBet(u2, u2.getBets().get(0), 5);
		testDA.open();
		User u3 = testDA.getUserWithUsernamePassword(u1.getUsername(), u1.getPassword());
		testDA.close();
		
		assertNotEquals(u3.getPastBets().size(), 0);
		assertEquals(u3.getBets().size(), 0);
		
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			fail("It should be correct: check the date format");
		}
		// Remove the created objects in the database (cascade removing)
		testDA.open();
		boolean b = testDA.removeEvent(ev);
		boolean b2 = testDA.removeUser(u);
		System.out.println("Removed event " + b + b2);
		testDA.close();
		
	}
}