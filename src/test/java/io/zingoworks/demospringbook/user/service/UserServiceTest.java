package io.zingoworks.demospringbook.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.zingoworks.demospringbook.DemoSpringbookApplication;
import io.zingoworks.demospringbook.user.dao.UserDao;
import io.zingoworks.demospringbook.user.domain.Level;
import io.zingoworks.demospringbook.user.domain.User;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootTest(classes = DemoSpringbookApplication.class)
class UserServiceTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserLevelUpgradePolicy userLevelUpgradePolicy;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    @Qualifier(value = "dummyMailSender")
    private MailSender mailSender;

    private List<User> users;

    @BeforeEach
    void setUp() {
        users = Arrays.asList(
            new User("a", "에이", "p1", Level.BASIC, UserServiceImpl.MIN_LOGIN_SEQUENCE_FOR_SILVER - 1, 0),
            new User("b", "비", "p2", Level.BASIC, UserServiceImpl.MIN_LOGIN_SEQUENCE_FOR_SILVER, 0),
            new User("c", "시", "p3", Level.SILVER, 60, UserServiceImpl.MIN_RECOMMEND_FOR_GOLD - 1),
            new User("d", "디", "p4", Level.SILVER, 60, UserServiceImpl.MIN_RECOMMEND_FOR_GOLD),
            new User("e", "이", "p5", Level.GOLD, 100, Integer.MAX_VALUE)
        );
    }

    @Test
    void bean() {
        assertThat(this.userServiceImpl).isNotNull();
    }

    @Test
    void add() {
        userDao.deleteAll();

        User userWithGoldLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userServiceImpl.add(userWithGoldLevel);
        userServiceImpl.add(userWithoutLevel);

        User userWithGoldLevelRead = userDao.get(userWithGoldLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithGoldLevelRead.getLevel()).isEqualTo(userWithGoldLevel.getLevel());
        assertThat(userWithoutLevelRead.getLevel()).isEqualTo(userWithoutLevel.getLevel());
    }

//	@Test
//	void upgradeLevels() {
//		userDao.deleteAll();
//		for (User user : users) {
//			userDao.add(user);
//		}
//
//		userService.setMailSender(this.mailSender);
//		userService.upgradeLevels();
//
//		checkLevelUpgraded(users.get(0), false);
//		checkLevelUpgraded(users.get(1), true);
//		checkLevelUpgraded(users.get(2), false);
//		checkLevelUpgraded(users.get(3), true);
//		checkLevelUpgraded(users.get(4), false);
//	}

    @DirtiesContext
    @Test
    void upgradeLevels() {
        userDao.deleteAll();
        for (User user : users) {
            userDao.add(user);
        }

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);

        List<String> requests = mockMailSender.getRequests();
        assertThat(requests.size()).isEqualTo(2);
        assertThat(requests.get(0)).isEqualTo(users.get(1).getName());
        assertThat(requests.get(1)).isEqualTo(users.get(3).getName());
    }

    @Test
    void mockUpgradeLevels() {
        UserServiceImpl userServiceImpl = new UserServiceImpl();
        userServiceImpl.setUserLevelUpgradePolicy(new DefaultUserLevelUpgradePolicy());

        UserDao mockUserDao = Mockito.mock(UserDao.class);
        Mockito.when(mockUserDao.getAll()).thenReturn(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MailSender mockMailSender = Mockito.mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        Mockito.verify(mockUserDao, Mockito.times(2)).update(ArgumentMatchers.any(User.class));
        Mockito.verify(mockUserDao, Mockito.times(2)).update(ArgumentMatchers.any(User.class));
        Mockito.verify(mockUserDao).update(users.get(1));
        assertThat(users.get(1).getLevel()).isEqualTo(Level.SILVER);
        Mockito.verify(mockUserDao).update(users.get(3));
        assertThat(users.get(3).getLevel()).isEqualTo(Level.GOLD);
    }

//    @Test
//    void upgradeAllOrNothing() {
//        TestUserService testUserService = new TestUserService(users.get(3).getId());
//        testUserService.setUserDao(this.userDao);
//        testUserService.setUserLevelUpgradePolicy(this.userLevelUpgradePolicy);
//        testUserService.setMailSender(this.mailSender);
//
//        UserServiceTx txUserService = new UserServiceTx();
//        txUserService.setTransactionManager(transactionManager);
//        txUserService.setUserService(testUserService);
//
//        userDao.deleteAll();
//        for (User user : users) {
//            userDao.add(user);
//        }
//
//        try {
//            txUserService.upgradeLevels();
//            Assertions.fail("TestUserServiceException expected");
//        } catch (TestUserServiceException e) {
//
//        }
//
//        checkLevelUpgraded(users.get(1), false);
//    }

//    @Test
//    void upgradeAllOrNothing() {
//        TestUserService testUserService = new TestUserService(users.get(3).getId());
//        testUserService.setUserDao(this.userDao);
//        testUserService.setUserLevelUpgradePolicy(this.userLevelUpgradePolicy);
//        testUserService.setMailSender(this.mailSender);
//
//        TransactionHandler txHandler = new TransactionHandler();
//        txHandler.setTarget(testUserService);
//        txHandler.setTransactionManager(transactionManager);
//        txHandler.setPattern("upgradeLevels");
//
//        UserService txUserService = (UserService) Proxy.newProxyInstance(
//            getClass().getClassLoader(),
//            new Class[]{UserService.class},
//            txHandler
//        );
//
//        userDao.deleteAll();
//        for (User user : users) {
//            userDao.add(user);
//        }
//
//        try {
//            txUserService.upgradeLevels();
//            Assertions.fail("TestUserServiceException expected");
//        } catch (TestUserServiceException e) {
//
//        }
//
//        checkLevelUpgraded(users.get(1), false);
//    }

//    @DirtiesContext
//    @Test
//    void upgradeAllOrNothing() throws Exception {
//        TestUserService testUserService = new TestUserService(users.get(3).getId());
//        testUserService.setUserDao(this.userDao);
//        testUserService.setUserLevelUpgradePolicy(this.userLevelUpgradePolicy);
//        testUserService.setMailSender(this.mailSender);
//
//        TxProxyFactoryBean txProxyFactoryBean = context.getBean(
//            "&userService",
//            TxProxyFactoryBean.class
//        );
//
//        txProxyFactoryBean.setTarget(testUserService);
//        UserService txUserService = (UserService) txProxyFactoryBean.getObject();
//
//        userDao.deleteAll();
//        for (User user : users) {
//            userDao.add(user);
//        }
//
//        try {
//            txUserService.upgradeLevels();
//            Assertions.fail("TestUserServiceException expected");
//        } catch (TestUserServiceException e) {
//
//        }
//
//        checkLevelUpgraded(users.get(1), false);
//    }

    @DirtiesContext
    @Test
    void upgradeAllOrNothing() throws Exception {
        TestUserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        testUserService.setUserLevelUpgradePolicy(this.userLevelUpgradePolicy);
        testUserService.setMailSender(this.mailSender);

        ProxyFactoryBean txProxyFactoryBean = context.getBean(
            "&userService",
            ProxyFactoryBean.class
        );

        txProxyFactoryBean.setTarget(testUserService);
        UserService txUserService = (UserService) txProxyFactoryBean.getObject();

        userDao.deleteAll();
        for (User user : users) {
            userDao.add(user);
        }

        try {
            txUserService.upgradeLevels();
            Assertions.fail("TestUserServiceException expected");
        } catch (TestUserServiceException e) {

        }

        checkLevelUpgraded(users.get(1), false);
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());

        if (upgraded) {
            assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel().getNext());
        } else {
            assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel());
        }
    }

    @AllArgsConstructor
    static class TestUserService extends UserServiceImpl {

        private String id;

        @Override
        protected void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) {
                throw new TestUserServiceException();
            }
            super.upgradeLevel(user);
        }
    }

    static class TestUserServiceException extends RuntimeException {

    }
}
