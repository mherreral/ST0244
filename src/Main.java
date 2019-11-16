
/**
 * The class Main is made for run the tests.
 **/
class Main {

    public static void main(String[] args) {
        Unify unify;

        if (args.length == 0) {
            for (int i = 1; i <= 5; i++) {
                System.out.println("Unify with cs" + (i) + ".txt\n");
                unify = new Unify("./test/cs" + (i) + ".txt");
            }
        } else {
            unify = new Unify(args[0]);
        }


    }
}
