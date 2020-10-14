// non-operational class..
package automatedBankingSystem;

import java.util.*;

public class BankAccount {

    // generic functions
    static Scanner input = new Scanner(System.in);

    // mapping
    static Map<Integer, String> mapName = new HashMap<>();
    static Map<Integer, String> mapPW = new HashMap<>();
    static Map<Integer, Double> mapBal = new HashMap<>();
    static Map<Integer, Integer> mapRepayCounter = new HashMap<>();
    static Map<Integer, Double> mapDebt = new HashMap<>();
    static Map<Integer, List<Double>> mapHist = new HashMap<>();
    static Map<Integer, Byte> mapDeactivation = new HashMap<>();

    // declare parameters
    int accNum;
    String name;
    String password;
    double accBal;

    // global variables I
    static double deposit;
    static double withdrawal;
    static double transfer;
    static double loan;
    static double repayment;
    static int loanAction;

    // settings
    static final int totalAccs = 10;
    static short debtRepaymentChances = 3;

    // systemic global variables
    static byte sharedProg;
    static byte terminateProg = 0;

    // for tallying accounts
    static int existingAccs = 0;
    static int accountsLeft = totalAccs - existingAccs;

    // for referencing accounts
    static int accNumber;
    static int recipient;

    // constructor function
    protected BankAccount(final int accNum, final String name, final String password, final double accBal) {
        // bank account details
        this.accNum = accNum;
        this.name = name;
        this.password = password;
        this.accBal = accBal;
    }

    // related to new accounts
    protected static boolean checkForAvailableAccs() {
        return accountsLeft > 0;
    }

    protected static int nextAvailableAcc() {
        // counting available accounts that can be opened
        return existingAccs + 1;
    }

    protected static void updateExistingAccs() {
        // reduces the number of available accounts by 1 when a new account is created
        existingAccs++;
        accountsLeft = totalAccs - existingAccs;
    }

    protected static String createNewPassword() {
        // creating a strong password
        System.out.println("Please create a strong password that adheres to the following criteria:");
        System.out.println("> it must not contain white spaces");
        System.out.println("> it must be at least 8 characters long");
        System.out.println("> it must have at least 1 uppercase and at least 1 lowercase letter");
        System.out.println("You have 3 attempts.");
        System.out.print("\n" + "Password: ");
        String password = input.next().trim();
        // checking for flaws in the password
        for (int i = 0; true; i++) {
            if ((password.length() < 8) || (password.toLowerCase().compareTo(password) == 0)
             || (password.toUpperCase().compareTo(password) == 0)) {
                for (int j = 0; j < password.length(); j++) {
                    if (password.charAt(j) == ' ') {
                        System.out.println("Your password cannot contain white spaces");
                        break;
                    }
                }
                if (password.length() < 8) {
                    System.out.println("Your password must be at least 8 characters long!");
                }
                if (password.toLowerCase().equals(password)) {
                    System.out.println("Your password must have at least one uppercase letter!");
                }
                if (password.toUpperCase().equals(password)) {
                    System.out.println("Your password must have at least one lowercase letter!");
                }
                if (i == 2) {
                    System.out.println("You have exhausted all your attempts. This session has been terminated.");
                    if (sharedProg == 1)
                        terminateProg = 2;
                    else
                        terminateProg = 1;
                    break;
                }
                System.out.println(' ');
            } else {
                break;
            }
            if (i < 2) {
                System.out.print("Password: ");
                password = input.next().trim();
            }
        }
        if (terminateProg == 0) {
            // confirming password
            System.out.print("Confirm password: ");
            String confirmPW = input.next().trim();
            for (int i = 0; true; i++) {
                if (confirmPW.equals(password)) {
                    break;
                } else {
                    System.out.println("Your passwords do not match! Try again!" + "\n");
                }
                if (i == 2) {
                    System.out.println("You have exhausted all your attempts! This session has been terminated.");
                    if (sharedProg == 1)
                        terminateProg = 2;
                    else
                        terminateProg = 1;
                    break;
                }
                System.out.print("Confirm password: ");
                confirmPW = input.next().trim();
            }
        }
        return password;
    }

    protected static void createNewAcc() {
        double accBal;
        input.nextLine();
        System.out.println("What is your name?");
        final String name = input.nextLine().trim();
        final BankAccount newUser = new BankAccount(0, name, "", 0);
        newUser.accNum = nextAvailableAcc();
        // connecting user's info
        newUser.password = createNewPassword();
        mapName.put(newUser.accNum, newUser.name);
        mapPW.put(newUser.accNum, newUser.password);
        mapBal.put(newUser.accNum, newUser.accBal);
        mapRepayCounter.put(newUser.accNum, 0);
        mapDebt.put(newUser.accNum, 0.0);
        mapDeactivation.put(newUser.accNum, (byte) 0);
        final List<Double> history = new ArrayList<>(0);
        mapHist.put(newUser.accNum, history);
        // list of account details displayed only once during sign-up
        if (terminateProg == 0) {
            // optional initial deposit
            System.out.println("\n" + "Do you want to deposit any money into your account now?");
            System.out.print('>');
            final String initialDep = input.next().trim().toLowerCase();
            if (initialDep.equals("yes")) {
                System.out.println("How much do you wish to deposit into your account?");
                System.out.print("Initial deposit: $");
                try {
                    final double deposit = input.nextDouble();
                    if (deposit < 0) {
                        System.out.println("You cannot deposit $" + deposit + '!');
                    } else {
                        accBal = deposit;
                        mapBal.put(newUser.accNum, accBal);
                        updateHist(newUser.accNum, 2);
                        updateHist(newUser.accNum, accBal);
                    }
                } catch (final InputMismatchException e) {
                    System.out.println("You may only deposit a sum of money!");
                }
            }
            // review account details
            System.out.println("\n" + "These are your account details:");
            System.out.println("Name: " + name);
            System.out.println("Account number: " + newUser.accNum);
            System.out.print("Password: ");
            for (int i = 0; i < newUser.password.length(); i++) {
                System.out.print('*');
            }
            System.out.println();
            System.out.println("Account balance: $" + mapBal.get(newUser.accNum));
            BankAccount.updateExistingAccs();
        }
    }

    // ------------------------------------------------------------------------------------------------

    // related to existing accounts

    // actions

    // action 1
    protected static void checkAccBal(final int accNumber) {
        System.out.println("Your account balance is $" + mapBal.get(accNumber) + '.');
        if (mapRepayCounter.get(accNumber) > 0) {
            System.out.println("You have an existing debt of $" + mapDebt.get(accNumber) + '.');
        }
    }

    // action 2
    protected static void depositMoney(final int accNumber) {
        double accBal;
        System.out.println("How much do you wish to deposit into your account?");
        System.out.print("Deposit: $");
        try {
            deposit = input.nextDouble();
            if (deposit < 0) {
                System.out.println("You cannot deposit $" + deposit + '!');
                terminateProg = 1;
            } else {
                accBal = mapBal.get(accNumber);
                accBal += deposit;
                mapBal.put(accNumber, accBal);
                System.out.println("Your account balance is $" + accBal + '.');
            }
        } catch (final InputMismatchException e) {
            System.out.println("You may only deposit a sum of money!");
            terminateProg = 2;
        }
    }

    // action 3
    protected static void withdrawMoney(final int accNumber) {
        double accBal;
        System.out.println("How much do you wish to withdraw from your account?");
        System.out.print("Withdrawal: $");
        try {
            withdrawal = input.nextDouble();
            if (withdrawal < 0) {
                System.out.println("You cannot withdraw $" + withdrawal + '!');
                terminateProg = 2;
            } else if (withdrawal > mapBal.get(accNumber)) {
                System.out.println("You do not have enough funds in your account to withdraw $" + withdrawal + '!');
                terminateProg = 2;
            } else {
                accBal = mapBal.get(accNumber);
                accBal -= withdrawal;
                mapBal.put(accNumber, accBal);
                System.out.println("Your account balance is $" + accBal + '.');
            }
        } catch (final InputMismatchException e) {
            System.out.println("You may only withdraw a sum of money!");
            terminateProg = 2;
        }
    }

    // action 4
    protected static void transferMoney(final int accNumber) {
        double accBal;
        System.out.println("Are you sure you want to make a transfer?");
        System.out.print('>');
        final String confirmTransfer = input.next().trim().toLowerCase();
        if (confirmTransfer.equals("yes")) {
            System.out.println("Please enter the account number you wish to transfer the money to.");
            System.out.print("Account number of recipient: ");
            recipient = input.nextInt();
            System.out.print("Sum to transfer: $");
            try {
                transfer = input.nextDouble();
                System.out.println(
                        "Are you sure you want to transfer $" + transfer + " to " + mapName.get(recipient) + '?');
                System.out.print('>');
                final String proceedTransfer = input.next().trim().toLowerCase();
                if (proceedTransfer.equals("yes")) {
                    // check if donor has sufficient funds in their account first
                    if (transfer > mapBal.get(accNumber)) {
                        System.out.println("You do not have enough money to transfer $" + transfer + '!');
                        terminateProg = 2;
                    } else if (transfer > 0) {
                        // deducting sum from donor's account
                        accBal = mapBal.get(accNumber);
                        accBal -= transfer;
                        mapBal.put(accNumber, accBal);
                        // adding sum to recipient's account
                        accBal = mapBal.get(recipient);
                        accBal += transfer;
                        mapBal.put(recipient, accBal);
                        // display donor's account balance
                        checkAccBal(accNumber);
                    } else {
                        System.out.println("You cannot transfer $" + transfer + '!');
                        terminateProg = 2;
                    }
                } else
                    terminateProg = 2;
            } catch (final InputMismatchException e) {
                System.out.println("You may only transfer a sum of money!");
                terminateProg = 2;
            }
        }
    }

    // action 5
    protected static int loan(final int accNumber) {
        double accBal;
        if (mapDebt.get(accNumber) == 0) {
            loanAction = 1;
            System.out.println("You do not have any existing debt.");
            System.out.println("If you proceed to take a loan, you are obliged to repay the loan by your third entry;");
            System.out.println("Otherwise, your account will be locked until you do so.");
            System.out.println("Are you sure you still want to take a loan?");
            System.out.print('>');
            final String takeLoan = input.next().trim().toLowerCase();
            if (takeLoan.equals("yes")) {
                try {
                    System.out.print("Loan: $");
                    loan = input.nextDouble();
                    if (loan < 0) {
                        System.out.println("You cannot take a loan of $" + loan + '!');
                        terminateProg = 2;
                    } else {
                        accBal = mapBal.get(accNumber);
                        accBal += loan;
                        mapBal.put(accNumber, accBal);
                        mapDebt.put(accNumber, loan);
                        // start countdown to repayment of loan
                        mapRepayCounter.put(accNumber, (mapRepayCounter.get(accNumber) + 1));
                        // updated account details
                        System.out.println("Your account balance is $" + mapBal.get(accNumber) + '.');
                        System.out.println("You have a debt of $" + mapDebt.get(accNumber) + '.');
                    }
                } catch (final InputMismatchException e) {
                    System.out.println("You may only take a loan of a sum of money!");
                    terminateProg = 2;
                }
            } else
                terminateProg = 2;
        } else {
            loanAction = 2;
            if (sharedProg != 1) {
                System.out.println("You have a existing debt.");
                System.out.println("You cannot take another loan until you have repaid your first debt.");
                System.out.println("Are you repaying your loan now?");
                System.out.print('>');
            }
            final String repayLoan = input.next().trim().toLowerCase();
            if (repayLoan.equals("yes")) {
                System.out.println("Are you repaying by (1) deposit or by (2) your account balance?");
                System.out.print('>');
                final byte loanRepayMethod = input.nextByte();
                System.out.println("How much do you wish to repay?");
                System.out.print('$');
                try {
                    repayment = input.nextDouble();
                    if (repayment < 0) {
                        System.out.println("You cannot repay $" + repayment + '!');
                        terminateProg = 2;
                    } else if (repayment >= 0 && repayment < mapDebt.get(accNumber)) {
                        System.out.println("You have repaid some($" + repayment + ") of your debt.");
                        System.out.println("Your debt repayment period has been extended.");
                        mapDebt.put(accNumber, (mapDebt.get(accNumber) - repayment));
                        if (loanRepayMethod == 2)
                            mapBal.put(accNumber, (mapBal.get(accNumber) - repayment));
                        if (loanRepayMethod == 1 || loanRepayMethod == 2) {
                            mapRepayCounter.put(accNumber, (mapRepayCounter.get(accNumber) - 1));
                            checkAccBal(accNumber);
                        }
                    } else if (repayment == mapDebt.get(accNumber)) {
                        System.out.println("Your debt of $" + mapDebt.get(accNumber) + " has been repaid in full!");
                        System.out.println("Your account access has been restored!");
                        if (loanRepayMethod == 2)
                            mapBal.put(accNumber, (mapBal.get(accNumber) - repayment));
                        if (loanRepayMethod == 1 || loanRepayMethod == 2)
                            mapRepayCounter.put(accNumber, 0);
                    } else {
                        System.out.println("You are repaying more than you owe.");
                        System.out.println("Your debt of $" + mapDebt.get(accNumber)
                                + " has been repaid in full and the remaining $" + (repayment - mapDebt.get(accNumber))
                                + " will be transferred to your account.");
                        System.out.println("Your account access has been restored!");
                        if (loanRepayMethod == 1)
                            mapBal.put(accNumber, (mapBal.get(accNumber) + repayment - mapDebt.get(accNumber)));
                        else if (loanRepayMethod == 2)
                            mapBal.put(accNumber, (mapBal.get(accNumber) - mapDebt.get(accNumber)));
                        if (loanRepayMethod == 1 || loanRepayMethod == 2) {
                            mapDebt.put(accNumber, 0.0);
                            mapRepayCounter.put(accNumber, 0);
                        }
                    }
                } catch (final InputMismatchException e) {
                    System.out.println("You may only repay a sum of money!");
                    terminateProg = 2;
                }
            } else
                terminateProg = 2;
        }
        return loanAction;
    }

    // action 6
    protected static void changeAccPW(final int accNumber) {
        sharedProg = 1;
        System.out.println("Are you sure you want to change your password?");
        System.out.print('>');
        final String changePW = input.next().trim().toLowerCase();
        if (changePW.equals("yes")) {
            System.out.println("Please enter your original password.");
            System.out.print("Original password: ");
            final String originalPW = input.next();
            if (originalPW.equals(mapPW.get(accNumber))) {
                mapPW.put(accNumber, createNewPassword());
                if (terminateProg != 0) {
                    mapPW.put(accNumber, originalPW);
                    terminateProg = 2;
                } else
                    System.out.println("Your password has been changed successfully!");
            } else {
                System.out.println("Your password is incorrect.");
                terminateProg = 2;
            }
        }
    }

    // action 7
    protected static void reviewAccHist(final int accNumber) {
        // historical meanings
        final Map<Double, String> mapHistMeanings = new HashMap<>();
        mapHistMeanings.put(0.0, "/discontinued...");
        mapHistMeanings.put(1.0, "checked account balance...");
        mapHistMeanings.put(2.0, "deposited $");
        mapHistMeanings.put(3.0, "withdrew $");
        mapHistMeanings.put(4.0, "transferred $");
        mapHistMeanings.put(4.5, "received $");
        mapHistMeanings.put(5.1, "took $");
        mapHistMeanings.put(5.2, "repaid $");
        mapHistMeanings.put(6.0, "changed password...");
        mapHistMeanings.put(7.0, "reviewed account history...");
        mapHistMeanings.put(8.0, "/exited...");
        if (mapHist.get(accNumber).size() == 0)
            System.out.println("This account has no prior history.");
        else {
            for (int i = mapHist.get(accNumber).size() - 1; i > 0; i -= 2) {
                final double pastHist = mapHist.get(accNumber).get(i);
                if (pastHist == 2 || pastHist == 3 || pastHist == 4)
                    System.out.println(mapHistMeanings.get(pastHist) + mapHist.get(accNumber).get(i - 1) + "...");
                else if (pastHist == 4.5)
                    System.out.println(
                            mapHistMeanings.get(pastHist) + mapHist.get(accNumber).get(i - 1) + " from a transfer...");
                else if (pastHist == 5.1)
                    System.out
                            .println(mapHistMeanings.get(pastHist) + mapHist.get(accNumber).get(i - 1) + " as loan...");
                else if (pastHist == 5.2)
                    System.out
                            .println(mapHistMeanings.get(pastHist) + mapHist.get(accNumber).get(i - 1) + " of loan...");
                else
                    System.out.println(mapHistMeanings.get(pastHist));
            }
        }
    }

    // action 8
    protected static void endSession() {
        System.out.println("This session has ended.");
        terminateProg = 1;
    }

    // action 9
    protected static void deactivateAcc(final int accNumber) {
        System.out.println("All your data will be erased irreversibly.");
        System.out.println("All system preferences will be reset to their default settings.");
        System.out.println("Are you sure you still want to deactivate your account?");
        System.out.print('>');
        final String deactivate = input.next().trim().toLowerCase();
        if (deactivate.equals("yes")) {
            mapName.put(accNumber, null);
            mapPW.put(accNumber, null);
            mapBal.put(accNumber, 0.0);
            mapDebt.put(accNumber, 0.0);
            mapRepayCounter.put(accNumber, 0);
            final List<Double> resetHist = new ArrayList<>();
            mapHist.put(accNumber, resetHist);
            mapDeactivation.put(accNumber, (byte) 1);
        }
    }

    // display actions
    protected static void displayActions(final int accNumber) {
        System.out.println("\n" + "Hello " + mapName.get(accNumber) + '!');
        System.out.println("Please select one of the following actions: ");
        System.out.println("1 > Check account balance");
        System.out.println("2 > Deposit money");
        System.out.println("3 > Withdraw money");
        System.out.println("4 > Make a transfer");
        if (mapDebt.get(accNumber) == 0)
            System.out.println("5 > Take a loan");
        else
            System.out.println("5 > Repay loan");
        System.out.println("6 > Change account password");
        System.out.println("7 > Review account history");
        System.out.println("8 > End session and quit");
        System.out.println("9 > Deactivate account");
    }

    // perform actions
    protected static void performAction(final int accNumber, final int action) {
        // actions 1 to 9 that can be performed on the opened account
        switch (action) {
            case 1:
                checkAccBal(accNumber);
                break;
            case 2:
                depositMoney(accNumber);
                break;
            case 3:
                withdrawMoney(accNumber);
                break;
            case 4:
                transferMoney(accNumber);
                break;
            case 5:
                loanAction = loan(accNumber);
                break;
            case 6:
                changeAccPW(accNumber);
                break;
            case 7:
                reviewAccHist(accNumber);
                break;
            case 8:
                endSession();
                break;
            case 9:
                deactivateAcc(accNumber);
                endSession();
                break;
            default:
                System.out.println("Please enter a valid integer from 1 to 9!");
                System.out.println("This session has ended.");
                terminateProg = 1;
                break;
        }
        if (action != 5 && terminateProg != 2) {
            updateHist(accNumber, action);
            if (action == 2)
                updateHist(accNumber, deposit);
            else if (action == 3)
                updateHist(accNumber, withdrawal);
            else if (action == 4) {
                updateHist(accNumber, transfer);
                updateHist(recipient, 4.5);
                updateHist(recipient, transfer);
            } else
                updateHist(accNumber, 0);
        } else if (terminateProg != 2) {
            if (loanAction == 1) {
                updateHist(accNumber, 5.1);
                updateHist(accNumber, loan);
            } else if (loanAction == 2) {
                updateHist(accNumber, 5.2);
                updateHist(accNumber, repayment);
            }
        }
    }

    // other relevant methods
    protected static boolean hasDebt(final int accNumber) {
        return (mapRepayCounter.get(accNumber) > 0);
    }

    protected static void updateHist(final int accNumber, final double action) {
        mapHist.get(accNumber).add(0, action);
        mapHist.put(accNumber, mapHist.get(accNumber));
    }

    protected static void openExistingAcc() {
        try {
            input.nextLine();
            System.out.print("Account number: ");
            accNumber = input.nextInt();
            if (accNumber > existingAccs) {
                System.out.println("This account has not yet been activated!");
                System.out.println("Please create a new account if you do not yet have one!");
            } else {
                if (mapDeactivation.get(accNumber) != 1) {
                    if (mapRepayCounter.get(accNumber) <= debtRepaymentChances) {
                        if (hasDebt(accNumber)) {
                            mapRepayCounter.put(accNumber, (mapRepayCounter.get(accNumber) + 1));
                        }
                        System.out.print("Password: ");
                        final String passWord = input.next();
                        if (passWord.equals(mapPW.get(accNumber))) {
                            while (true) {
                                displayActions(accNumber);
                                try {
                                    input.nextLine();
                                    System.out.print("Action: ");
                                    final int action = input.nextInt();
                                    System.out.println();
                                    performAction(accNumber, action);
                                    if (terminateProg == 1) {
                                        terminateProg = 0;
                                        break;
                                    }
                                    if (action != 9) {
                                        System.out.println("\n" + "Do you wish to continue?");
                                        System.out.print('>');
                                        final String continueActions = input.next().trim().toLowerCase();
                                        if (continueActions.equals("no") || !continueActions.equals("yes")) {
                                            System.out.println();
                                            System.out.println("This session has ended.");
                                            updateHist(accNumber, 0);
                                            updateHist(accNumber, 0);
                                            break;
                                        }
                                    }
                                } catch (final InputMismatchException e) {
                                    System.out.println("Please only enter 1, 2, 3, 4, 5, 6, 7, 8 or 9!");
                                }
                            }
                        } else {
                            System.out.println("Your password is incorrect. This session has been terminated.");
                        }
                    } else {
                        System.out.println("You currently owe a debt of $" + mapDebt.get(accNumber) + " to abc bank.");
                        System.out.println("You may either repay it or get locked out of your account till you do so.");
                        System.out.println("Are you repaying it now?");
                        System.out.print('>');
                        sharedProg = 1;
                        loan(accNumber);
                        if (terminateProg == 2)
                            System.out.println("Your account is locked until you repay your debt in full!");
                    }
                } else {
                    System.out.println("This account has been deactivated.");
                    System.out.println("You cannot access account " + accNumber + " anymore.");
                }
            }
        } catch (final InputMismatchException e) {
            System.out.println("Please enter a valid account number!");
        }
    }
}