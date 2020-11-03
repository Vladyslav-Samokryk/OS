package main.java;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Demonstration {
    public void showVariants() {
        System.out.println("Possible variants: ");
        System.out.println("1. f finishes before g with non value");
        System.out.println("2. g finishes before f with non value");
        System.out.println("3. f finishes with zero, g hangs");
        System.out.println("4. g finishes with zero, f hangs");
        System.out.println("5. f finishes with non zero value, f hangs (check cancellation)");
        System.out.println("6. g finishes with non zero value, f hangs (check cancellation)");
    }

    public Integer inputVariant() {
        try {
            int variant;
            Scanner selector = new Scanner(System.in);

            System.out.print("\nEnter number of variant: ");
            variant = selector.nextInt();

            return variant >= 1 && variant <= 6 ? variant : null;
        } catch (InputMismatchException e) {
            return null;
        }
    }
}
