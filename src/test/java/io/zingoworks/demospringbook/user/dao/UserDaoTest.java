package io.zingoworks.demospringbook.user.dao;

import io.zingoworks.demospringbook.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

//@ExtendWith(SpringExtension.class) // replace junit4 @Runwith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "/test-applicationContext.xml")
class UserDaoTest {

//	@Autowired
//	private ApplicationContext context;
	
	//	@Autowired
	private UserDao dao;
	
	private User user1;
	private User user2;
	private User user3;
	
	@BeforeEach
	void setUp() {
		DataSource dataSource = new SingleConnectionDataSource(
				"jdbc:mysql://localhost/springbook", "root", "1234", true
		);
		
		JdbcContext jdbcContext = new JdbcContext();
		jdbcContext.setDataSource(dataSource);
		
		this.dao = new UserDao();
		dao.setJdbcContext(jdbcContext);
		dao.setDataSource(dataSource); //FIXME for backward compatibility
		
		this.user1 = new User("1", "one", "1234");
		this.user2 = new User("2", "two", "1234");
		this.user3 = new User("3", "three", "1234");
	}
	
	@Test
	void addAndGet() throws SQLException {
		dao.deleteAllNew();
		assertThat(dao.getCount()).isEqualTo(0);
		
		dao.add(user1);
		dao.add(user2);
		assertThat(dao.getCount()).isEqualTo(2);
		
		User userget1 = dao.get(user1.getId());
		assertThat(userget1.getName()).isEqualTo(user1.getName());
		assertThat(userget1.getPassword()).isEqualTo(user1.getPassword());
		
		User userget2 = dao.get(user2.getId());
		assertThat(userget2.getName()).isEqualTo(user2.getName());
		assertThat(userget2.getPassword()).isEqualTo(user2.getPassword());
	}
	
	@Test
	void getUserFailure() throws SQLException {
		dao.deleteAllNew();
		assertThat(dao.getCount()).isEqualTo(0);
		
		assertThrows(EmptyResultDataAccessException.class, () -> dao.get("unknown_id"));
	}
	
	@Test
	void count() throws SQLException {
		dao.deleteAllNew();
		assertThat(dao.getCount()).isEqualTo(0);
		
		dao.add(user1);
		assertThat(dao.getCount()).isEqualTo(1);
		
		dao.add(user2);
		assertThat(dao.getCount()).isEqualTo(2);
		
		dao.add(user3);
		assertThat(dao.getCount()).isEqualTo(3);
	}
}
