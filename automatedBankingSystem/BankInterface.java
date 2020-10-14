// requires BankAccount.java to operate...
package automatedBankingSystem;

import java.util.Scanner;

import static automatedBankingSystem.BankAccount.*;

public class BankInterface {

    public static void main(final String[] args) {

        final String terminationCode = "%";
        final Scanner input = new Scanner(System.in);

        while (true) {
            // user interface
            sharedProg = 0;
            terminateProg = 0;
            System.out.println("\n" + "Hello! Welcome to abc banking interface!");
            System.out.println("Do you have an existing account?");
            try {
                System.out.print('>');
                final String hasAcc = input.next().trim().toLowerCase();
                // creating a new account for the user
                if (hasAcc.equals("no")) {
                    if (BankAccount.checkForAvailableAccs()) {
                        BankAccount.createNewAcc();
                    } else {
                        System.out.println("Sorry, there are no more remaining accounts. Please try again next time.");
                    }
                }
                // opening an existing account
                else if (hasAcc.equals("yes")) {
                    BankAccount.openExistingAcc();
                } else if (hasAcc.equals(terminationCode)) {
                    break;
                } else {
                    System.out.println("\n" + "Please enter either \"yes\" or \"no\"!");
                }
            } catch (final NullPointerException none) {
                System.out.println("Account does not exist!");
            }
        }
        input.close();
    }
}