import util.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContactsManagerApplication {

    //sets the static filepath
    static Path filepath = Paths.get("data", "contacts.txt");

    //adds contact to the contacts List and writes it to the filepath
    public static void addContact(List<Contact> contacts, Contact newContact) throws IOException {
        contacts.add(newContact);
        Files.write(filepath, ioOut(contacts).getBytes());
    }

    //reads contacts from the filepath and writes them as contacts to the contacts List
    private static List<Contact> readContacts() {
        try {
            List<String> tempContactList = Files.readAllLines(filepath);
            List<Contact> contactsList = new ArrayList<>();
            for (int i = 1; i < tempContactList.size(); i++) {
                String[] thisContact = tempContactList.get(i).split(",");
                Contact contact = new Contact(thisContact[0], thisContact[1]);
                contactsList.add(contact);
            }
            return contactsList;
        } catch (IOException ioe) {
            System.out.println("Could not read file.");
            return null;
        }
    }

    //string builder for the IO string that is written to the filepath
    private static String ioOut(List<Contact> contacts) {
        StringBuilder output = new StringBuilder("Name:,Phone:\n");
        for (Contact contact : contacts) {
            output.append(contact.getName()).append(",");
            output.append(contact.getNumber()).append("\n");
        }
        return output.toString();
    }

    //deletes the input contact
    private static int removeContact(List<Contact> contacts, String target) {

        int counter = 0;
        int index = -1;
        for (Contact contact : contacts) {
            if (contact.getName().equalsIgnoreCase(target)) {
                index = counter;
            }
            counter++;
        }
        return index;
    }

    //prints the CLI menu to terminal
    private static int menuSelection() {
        Input input = new Input();
        System.out.println("\nPlease select an option:\n");
        System.out.println("\t1. View contacts");
        System.out.println("\t2. Add a new contact");
        System.out.println("\t3. Search a contact by name");
        System.out.println("\t4. Delete an existing contact");
        System.out.println("\t5. Exit");

        return input.getInt(1, 5, "\nPlease make your selection: ");
    }

    //prints the contacts List to terminal
    private static void printContactList(List<Contact> contacts) {
        if (contacts.size() == 0) {
            System.out.println("\nYou have no contacts...Please add a new contact.");
        } else {
            System.out.println("\nHere are your contacts:\n");
            int i = 0;
            System.out.printf("\t%s  %-18s | %-15s |\n", "#", "Name: ", "Phone #:");
            System.out.println("\t-----------------------------------------");
            for (Contact contact : contacts) {
                i++;
                System.out.printf("\t%d) %-18s | %-15s |\n", i, contact.getName(), formatPhoneNumber(contact.getNumber()));
            }
        }
    }

    //input and validation method used by addContact method
    private static void addNewContact(List<Contact> contacts) throws IOException {
        Input input = new Input();
        boolean confirm;
        String target;

        System.out.println();
        boolean userFound = false;
        int counter = 0;
        target = input.getString("Contact Name: ");

        for (Contact contact : contacts) {
            if (contact.getName().equalsIgnoreCase(target)) {
                userFound = true;
                break;
            }
            counter++;
        }

        if (userFound) {
            System.out.printf("\nThere's already a contact named %s.\n", target.toUpperCase());
            confirm = input.yesNo("Do you want to overwrite it? (Yes/No)");
            if (confirm) {
                contacts.get(counter).setNumber(input.getString("New Number (No dashes): "));
                Files.write(filepath, ioOut(contacts).getBytes());
                System.out.println("\nContact updated\n");
                System.out.println("\tName: " + contacts.get(counter).getName());
                System.out.println("\tNew Phone#: " + formatPhoneNumber(contacts.get(counter).getNumber()));
            } else {
                addNewContact(contacts);
            }
        } else {
            Contact newContact = new Contact(target, input.getString("Contact Number (No dashes): "));
            addContact(contacts, newContact);
        }
    }

    private static void searchContacts(List<Contact> contacts){
        Input input = new Input();

        System.out.println();
        boolean userFound = false;
        int counter = 0;
        String target = input.getString("Contact Name: ");

        for (Contact contact : contacts) {
            if (contact.getName().equalsIgnoreCase(target)) {
                userFound = true;
                break;
            }
            counter++;
        }

        if (userFound) {
            System.out.println("Name: " + target);
            System.out.println("Phone#: " + formatPhoneNumber(contacts.get(counter).getNumber()));
        } else {
            System.out.printf("\nNo contact found with the name %s.\n", target.toUpperCase());
        }
    }

    private static void removeExistingContact(List<Contact> contacts){
        Input input = new Input();
        boolean confirm;
        String target;

        target = input.getString("What is the contact's name?: ");
        int indexToRemove = removeContact(contacts, target);
        if (indexToRemove == -1) {
            System.out.println("That person was not found.");
        } else {
            confirm = input.yesNo("!!!  ARE YOU SURE YOU WANT TO DELETE THIS CONTACT?  !!!");
            if (confirm) {
                contacts.remove(indexToRemove);
                System.out.printf("\nContact: %s was removed from your list.\n", target.toUpperCase());
            }
        }
    }

    private static String formatPhoneNumber(String phoneNumber){
        String[] arr = phoneNumber.split("");
        boolean result= Arrays.asList(arr).contains("-");

        StringBuilder formattedNumber = new StringBuilder();
        if (phoneNumber.length() == 7){
            for(int i=0;i<phoneNumber.length();i++){
                formattedNumber.append(arr[i]);
                if (i == 2){
                    formattedNumber.append("-");
                }
            }
        }
        if (phoneNumber.length() == 10 || !result){
            for(int i=0;i<phoneNumber.length();i++){
                formattedNumber.append(arr[i]);
                if (i == 2 || i == 5){
                    formattedNumber.append("-");
                }
            }
        }
        return formattedNumber.toString();
    }

    public static void main(String[] args) throws IOException {
        boolean confirm = true;
        List<Contact> contacts;
        contacts = readContacts();
        if (contacts == null) {
            contacts = new ArrayList<>();
        }

        System.out.println("\nContacts I/O - created by Bryce and Jeff.");
        do {
            int selection = menuSelection();

            switch (selection) {
                case 1:
                    printContactList(contacts); break;
                case 2:
                    addNewContact(contacts); break;
                case 3:
                    searchContacts(contacts); break;
                case 4:
                    removeExistingContact(contacts); break;
                case 5:
                    confirm = false; break;
            }
        } while (confirm);
        System.out.println("\nGoodbye, and have a nice day!");
        Files.write(filepath, ioOut(contacts).getBytes());
    }
}
