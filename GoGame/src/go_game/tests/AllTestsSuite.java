package go_game.tests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	BoardTest.class,
	ServerClientTest.class
})
public class AllTestsSuite {

}
