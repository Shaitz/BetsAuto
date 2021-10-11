package utility;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import configuration.ConfigXML;
import domain.Event;
import domain.Question;
import domain.User;
import enums.QuestionTypes;
import exceptions.QuestionAlreadyExist;

public class TestUtilityDataAccess {
	protected  EntityManager  db;
	protected  EntityManagerFactory emf;

	ConfigXML  c = ConfigXML.getInstance();

	public TestUtilityDataAccess()  {		
		System.out.println("Creating TestDataAccess instance");

		open();		
	}
	
	public void open(){
		
		System.out.println("Opening TestDataAccess instance ");

		String fileName=c.getDataBaseFilename();
		
		if (c.isDataAccessLocal()) {
			  emf = Persistence.createEntityManagerFactory("objectdb:"+fileName);
			  db = emf.createEntityManager();
		} else {
			Map<String, String> properties = new HashMap<String, String>();
			  properties.put("javax.persistence.jdbc.user", c.getDataBaseUser());
			  properties.put("javax.persistence.jdbc.password", c.getDataBasePassword());

			  emf = Persistence.createEntityManagerFactory("objectdb://"+c.getDataAccessNode()+":"+c.getDataAccessPort()+"/"+fileName, properties);

			  db = emf.createEntityManager();
    	   }
		
	}
	public void close(){
		db.close();
		System.out.println("DataBase closed");
	}

	public boolean removeEvent(Event ev) {
		System.out.println(">> DataAccessTest: removeEvent");
		Event e = db.find(Event.class, ev.getEventNumber());
		if (e!=null) {
			db.getTransaction().begin();
			db.remove(e);
			db.getTransaction().commit();
			return true;
		} else 
		return false;
    }
		
		public Event addEventWithQuestion(String desc, Date d, String question, float qty) {
			System.out.println(">> DataAccessTest: addEvent");
			Event ev=null;
				db.getTransaction().begin();
				try {
				    ev=new Event(desc,d);
				    ev.addQuestion(question,  qty);
					db.persist(ev);
					db.getTransaction().commit();
				}
				catch (Exception e){
					e.printStackTrace();
				}
				return ev;
	    }
		
		public Vector<Event> getEvents(Date date) {
			System.out.println(">> DataAccess: getEvents");
			Vector<Event> res = new Vector<Event>();	
			TypedQuery<Event> query = db.createQuery("SELECT ev FROM Event ev WHERE ev.eventDate=?1",Event.class);   
			query.setParameter(1, date);
			List<Event> events = query.getResultList();
		 	 for (Event ev:events){
		 	   System.out.println(ev.toString());		 
			   res.add(ev);
			  }
		 	return res;
		}
		
		public boolean existQuestion(Event event, String question) {
			System.out.println(">> DataAccess: existQuestion=> event= "+event+" question= "+question);
			Event ev = db.find(Event.class, event.getEventNumber());
			return ev.doesQuestionExist(question);
			
		}
		
		
		public User getUserWithUsernamePassword(String username, String password){
			User ret;
			List<User> checkList = db.createQuery("SELECT u FROM User u WHERE u.username = \"" + username + "\" and u.password = \"" + password + "\"", User.class).getResultList();
			try {
				ret = checkList.get(0);
			}
			catch(Exception e) {
				ret = null;
			}
			return ret;
		}
		
		public User createUser(String username, String password, String eMail) {
			User nU = null;
			if(getUserWithUsernamePassword(username, password) == null)
				if(getUserWithEMail(eMail) == null) {
					nU = new User(username, password, eMail);
					db.getTransaction().begin();
					db.persist(nU);
					db.getTransaction().commit();
				}
			return nU;
		}
		
		public User getUserWithEMail(String eMail) {
			User ret;
			List<User> checkList = db.createQuery("SELECT u FROM User u WHERE u.eMail = \"" + eMail + "\"", User.class).getResultList();
			try {
				ret = checkList.get(0);
			}
			catch (Exception e) {
				ret = null;
			}
			return ret;
		}
		
		public boolean removeUser(User u) {
			System.out.println(">> DataAccessTest: removeUser");
			User us = this.getUserWithUsernamePassword(u.getUsername(), u.getPassword());
			if (us!=null) {
				db.getTransaction().begin();
				db.remove(us);
				db.getTransaction().commit();
				return true;
			} else 
			return false;
	    }
		
		public double addMoneyToUser(int id, double amount) {
			var user = this.getUserByID(id);
			if(user == null)
			{
				return -1;
			}
			db.getTransaction().begin();
			double ret = user.increaseCurrency(amount);
			db.getTransaction().commit();
			return ret;
		}
		
		public User getUserByID(Integer id) {
			User ret;
			List<User> checkList = db.createQuery("SELECT u FROM User u WHERE u.id = " + id, User.class).getResultList();
			try {
				ret = checkList.get(0);
			}
			catch (Exception e) {
				ret = null;
			}
			return ret;
		}
		
		public boolean placeBet(User user, Question question, double amount, String answer) {
			User userToChange = this.getUserWithUsernamePassword(user.getUsername(), user.getPassword());
			var q = this.getQuestion(question);
			db.getTransaction().begin();
			boolean ret = userToChange.placeBet(question, amount, answer);
			q.addPool(amount);
			db.getTransaction().commit();
			return ret;
		}
		
		public Question getQuestion(Question q) 
		{
			Question ret = db.createQuery("SELECT q FROM Question q WHERE q.questionNumber = " + q.getQuestionNumber(), Question.class).getSingleResult();
			if(ret.getAnswers() != null)
				if(ret.getAnswers().iterator().hasNext()) {
					@SuppressWarnings("unused")
					String just4Use = ret.getAnswers().iterator().next();
				}
			return ret;
		}
}

