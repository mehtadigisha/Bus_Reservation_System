import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

class Passenger {
    private String id;
    private String name;
    private int age;
    private ArrayList<Integer> seatNumbers;

    public Passenger(String name, int age, ArrayList<Integer> seatNumbers) {
        this.id = generateAlphanumericId();
        this.name = name;
        this.age = age;
        this.seatNumbers = seatNumbers;
    }

    private String generateAlphanumericId() {
        int length = new Random().nextInt(2) + 7;
        StringBuilder id = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < length; i++) {
            id.append(chars.charAt(new Random().nextInt(chars.length())));
        }
        return id.toString();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public ArrayList<Integer> getSeatNumbers() {
        return seatNumbers;
    }
}

class Ticket {
    private Passenger passenger;
    private String busNumber;
    private LocalDateTime bookingDateTime;

    public Ticket(Passenger passenger, String busNumber) {
        this.passenger = passenger;
        this.busNumber = busNumber;
        this.bookingDateTime = LocalDateTime.now();
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public LocalDateTime getBookingDateTime() {
        return bookingDateTime;
    }

    public void saveToFile() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        FileWriter writer = new FileWriter("bookings.txt", true);
        writer.write("Date and Time: " + bookingDateTime.format(formatter) +
                ", Bus Name: " + busNumber + ", Passenger ID: " + passenger.getId() +
                ", Name: " + passenger.getName() + ", Age: " + passenger.getAge() +
                ", Seats: " + passenger.getSeatNumbers().toString() + "\n");
        writer.close();
    }

    public static void removeFromFile(String passengerId) throws IOException {
        File inputFile = new File("bookings.txt");
        File tempFile = new File("bookings_temp.txt");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.contains("Passenger ID: " + passengerId)) {
                writer.write(line + System.lineSeparator());
            }
        }
        writer.close();
        reader.close();

        if (inputFile.delete()) {
            tempFile.renameTo(inputFile);
        }
    }
}

class Bus {
    private String busNumber;
    private int capacity;
    private ArrayList<Integer> bookedSeats;
    private double pricePerSeat;

    public Bus(String busNumber, int capacity, double pricePerSeat) {
        this.busNumber = busNumber;
        this.capacity = capacity;
        this.pricePerSeat = pricePerSeat;
        this.bookedSeats = new ArrayList<>();
    }

    public double getPricePerSeat() {
        return pricePerSeat;
    }

    public void setPricePerSeat(double pricePerSeat) {
        this.pricePerSeat = pricePerSeat;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isSeatAvailable(int seatNumber) {
        return !bookedSeats.contains(seatNumber);
    }

    public boolean bookSeat(int seatNumber) {
        if (seatNumber > 0 && seatNumber <= capacity && isSeatAvailable(seatNumber)) {
            bookedSeats.add(seatNumber);
            return true;
        }
        return false;
    }

    public boolean cancelSeat(int seatNumber) {
        return bookedSeats.remove(Integer.valueOf(seatNumber));
    }

    public ArrayList<Integer> getBookedSeats() {
        return bookedSeats;
    }

    public int getAvailableSeats() {
        return capacity - bookedSeats.size();
    }
}

class Supervisor {
    public void verifyBooking(Passenger passenger, Bus bus) throws InterruptedException {
        System.out.println("\nSupervisor is verifying the booking...");
        TimeUnit.SECONDS.sleep(2);
        System.out.println("Booking verified by Supervisor for " + passenger.getName() +
                " (ID: " + passenger.getId() + ") on bus " + bus.getBusNumber() + ".");
    }

    public void verifyCancellation(Passenger passenger, Bus bus) throws InterruptedException {
        System.out.println("\nSupervisor is verifying the cancellation...");
        TimeUnit.SECONDS.sleep(2);
        System.out.println("Cancellation verified by Supervisor for " + passenger.getName() +
                " (ID: " + passenger.getId() + ") on bus " + bus.getBusNumber() + ".");
        System.out.println("Thank you...\nYour payment will be return within 2-3 working days");
    }
}

class ReservationSystem {
    private ArrayList<Bus> buses;
    private HashMap<String, Passenger> passengers;
    private Supervisor supervisor;
    public static double total;

    public ReservationSystem() {
        buses = new ArrayList<>();
        passengers = new HashMap<>();
        supervisor = new Supervisor();
    }

    public void addBus(Bus bus) {
        buses.add(bus);
    }

    public Bus findBus(String busNumber) {
        for (Bus bus : buses) {
            if (bus.getBusNumber().equalsIgnoreCase(busNumber)) {
                return bus;
            }
        }
        return null;
    }

    public void displayBuses() {
        System.out.println("\n--- Available Buses ---");
        for (Bus bus : buses) {
            System.out.println("Bus Name: " + bus.getBusNumber() +
                    ", Price: " + bus.getPricePerSeat() + ", Capacity: " + bus.getCapacity() +
                    ", Available Seats: " + bus.getAvailableSeats());
        }
    }

    public void bookTicketWithPayment() throws InterruptedException, IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Bus Name:");
        String busNumber = scanner.nextLine();

        Bus bus = findBus(busNumber);
        if (bus == null) {
            System.out.println("Bus not found!");
            return;
        }

        if (bus.getAvailableSeats() == 0) {
            System.out.println("Sorry, no seats available on this bus.");
            return;
        }

        System.out.println("Available Seats: " + bus.getAvailableSeats());
        System.out.println("Enter Seat Numbers (comma-separated):");
        String seatInput = scanner.nextLine();
        String[] seatStrings = seatInput.split(",");
        ArrayList<Integer> seatNumbers = new ArrayList<>();

        for (String seatStr : seatStrings) {
            int seatNumber = Integer.parseInt(seatStr.trim());
            if (bus.bookSeat(seatNumber)) {
                seatNumbers.add(seatNumber);
            } else {
                System.out.println("Seat " + seatNumber + " is already booked or invalid.");
            }
        }

        if (!seatNumbers.isEmpty()) {
            System.out.println("Enter Passenger Name:");
            String name = scanner.nextLine();

            System.out.println("Enter Passenger Age:");
            int age = scanner.nextInt();
            scanner.nextLine();

            Passenger passenger = new Passenger(name, age, seatNumbers);
            passengers.put(passenger.getId(), passenger);

            Ticket ticket = new Ticket(passenger, busNumber);

            System.out.println("Processing ticket booking...");
            TimeUnit.SECONDS.sleep(2);

            double ticketPrice = calculatePrice(busNumber, seatNumbers.size());

            System.out.println("Total Amount: $" + ticketPrice);
            System.out.println("Choose Payment Method:");
            System.out.println("1. Credit Card");
            System.out.println("2. Debit Card");
            System.out.println("3. PayPal (UPI ID or Number)");
            System.out.print("Enter your choice: ");
            int paymentChoice = scanner.nextInt();
            scanner.nextLine();

            switch (paymentChoice) {
                case 1:
                    boolean isCreditCardValid = false;
                    String creditCardNumber = "";
                    String creditCardCvc = "";

                    while (!isCreditCardValid) {
                        System.out.println("Enter Card Number (16 digits): ");
                        creditCardNumber = scanner.nextLine();
                        if (creditCardNumber.length() != 16 || !creditCardNumber.matches("\\d+")) {
                            System.out.println("Invalid Card Number. It must be exactly 16 digits long.");
                            continue;
                        }

                        System.out.println("Enter CVC (3 digits): ");
                        creditCardCvc = scanner.nextLine();
                        if (creditCardCvc.length() != 3 || !creditCardCvc.matches("\\d+")) {
                            System.out.println("Invalid CVC. It must be exactly 3 digits long.");
                            continue;
                        }

                        isCreditCardValid = true;
                        System.out.println("Processing payment with Card Number " + creditCardNumber + "...");
                        try {
                            TimeUnit.SECONDS.sleep(2);
                        } catch (InterruptedException e) {
                            System.out.println("Payment processing interrupted.");
                        }
                        System.out.println("Payment Successful!");
                    }
                    break;

                case 2:
                    boolean isDebitCardValid = false;
                    String debitCardNumber = "";
                    String debitCardCvc = "";

                    while (!isDebitCardValid) {
                        System.out.println("Enter Card Number (16 digits): ");
                        debitCardNumber = scanner.nextLine();
                        if (debitCardNumber.length() != 16 || !debitCardNumber.matches("\\d+")) {
                            System.out.println("Invalid Card Number. It must be exactly 16 digits long.");
                            continue;
                        }

                        System.out.println("Enter CVC (3 digits): ");
                        debitCardCvc = scanner.nextLine();
                        if (debitCardCvc.length() != 3 || !debitCardCvc.matches("\\d+")) {
                            System.out.println("Invalid CVC. It must be exactly 3 digits long.");
                            continue;
                        }

                        isDebitCardValid = true;
                        System.out.println("Processing payment with Debit Card Number " + debitCardNumber + "...");
                        try {
                            TimeUnit.SECONDS.sleep(2);
                        } catch (InterruptedException e) {
                            System.out.println("Payment processing interrupted.");
                        }
                        System.out.println("Payment Successful!");
                    }
                    break;

                case 3:
                    boolean isPayPalValid = false;
                    String upiId = "";

                    while (!isPayPalValid) {
                        System.out.println("Enter UPI ID: ");
                        upiId = scanner.nextLine();
                        if (upiId.isEmpty()) {
                            System.out.println("Invalid UPI ID. It cannot be empty.");
                            continue;
                        }
                        isPayPalValid = true;
                        System.out.println("Processing payment with UPI ID " + upiId + "...");
                        try {
                            TimeUnit.SECONDS.sleep(2);
                        } catch (InterruptedException e) {
                            System.out.println("Payment processing interrupted.");
                        }
                        System.out.println("Payment Successful!");
                    }
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
            supervisor.verifyBooking(passenger, bus);

            ticket.saveToFile();

            System.out.println("Ticket(s) booked successfully for Seat(s) " + seatNumbers +
                    " with Passenger ID: " + passenger.getId() + "!");
        } else {
            System.out.println("No valid seats were booked.");
        }
    }

    private double calculatePrice(String busNumber, int seatCount) {
        double pricePerSeat;

        switch (busNumber) {
            case "Patel Tours and travels":
                pricePerSeat = 900.0;
                break;
            case "patel tours and travels":
                pricePerSeat = 900.0;
                break;
            case "Falcon Travels":
                pricePerSeat = 875.0;
                break;
            case "falcon travels":
                pricePerSeat = 875.0;
                break;
            case "GSRTC":
                pricePerSeat = 685.0;
                break;
            case "gsrtc":
                pricePerSeat = 685.0;
                break;
            default:
                pricePerSeat = 0.0;
                break;
        }
        total = pricePerSeat * seatCount;
        return total;
    }

    public void cancelBooking() throws InterruptedException, IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Bus Name:");
        String busNumber = scanner.nextLine();
        Bus bus = findBus(busNumber);
        if (bus == null) {
            System.out.println("Bus not found!");
            return;
        }

        System.out.println("Enter Passenger ID:");
        String passengerId = scanner.nextLine();

        Passenger passenger = passengers.get(passengerId);
        if (passenger == null) {
            System.out.println("Passenger not found!");
            return;
        }

        for (int seatNumber : passenger.getSeatNumbers()) {
            bus.cancelSeat(seatNumber);
        }

        supervisor.verifyCancellation(passenger, bus);

        Ticket.removeFromFile(passengerId);
        passengers.remove(passengerId);

        System.out.println("Booking canceled successfully for Passenger ID: " + passengerId + ".");
    }
}

public class BusTicketReservationSystem {
    public static void main(String[] args) throws InterruptedException, IOException {
        ReservationSystem reservationSystem = new ReservationSystem();

        reservationSystem.addBus(new Bus("Patel Tours and travels", 30, 900.0));
        reservationSystem.addBus(new Bus("Falcon travels", 25, 875.0));
        reservationSystem.addBus(new Bus("GSRTC", 40, 685.0));

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Bus Ticket Reservation System ---");
            System.out.println("1. Display Buses");
            System.out.println("2. Book Ticket");
            System.out.println("3. Cancel Booking");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    reservationSystem.displayBuses();
                    break;
                case 2:
                    reservationSystem.bookTicketWithPayment();
                    break;
                case 3:
                    reservationSystem.cancelBooking();
                    break;
                case 4:
                    System.out.println("Exiting system...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}