package files;

public class FileTest {

    public static void main(String[] args) {
    
        show(world);
        System.out.println();
        world = nextGen(world);
        show(world);
        Scanner s = new Scanner(System.in);
        while(s.nextLine().length() == 0){
            System.out.println();
            world = nextGen(world);
            show(world);
            
        }
    }

}
