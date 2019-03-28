package files;

public class FileTest {

    public static void main(String[] args) {
    final int NUM_FACTS = 100;
		for(int i = 0; i < NUM_FACTS; i++)
			System.out.println( i + "! is " + factorial(i));
    }

}
