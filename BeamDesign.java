import java.util.Scanner;
public class BeamDesign {
     // Calculate the distance from the neutral axis to the outermost fiber
    static double y=0.0;
    // Method to calculate moment of inertia for different shapes
    public static double RMomentOfInertia() {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter Breadth:");
        double b=sc.nextDouble();
        System.out.println("Enter Height:");
        double h=sc.nextDouble();
        y=h/2;
        return (b * Math.pow(h, 3)) / 12.0;
    }

    public static double CMomentOfInertia() {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter Diameter:");
        double d=sc.nextDouble();
        y=d/2;
        return (Math.PI * Math.pow(d, 4)) / 64.0;
    }

    public static double rectangleMomentOfInertia(double width, double height) {
        return (width * Math.pow(height, 3)) / 12.0;
    }

    // Method to calculate the moment of inertia for an I-section
    public static double IMomentOfInertia() {
        
        // Input dimensions for the I-section
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter Flange Width");
        double flangeWidth = sc.nextDouble();;  // in mm
        System.out.println("Enter Flange Height");
        double flangeHeight = sc.nextDouble();  // in mm
        System.out.println("Enter Web Width");
        double webWidth = sc.nextDouble();;      // in mm
        System.out.println("Enter Web Height");
        double webHeight = sc.nextDouble();;    // in mm
        
        // Total height of the I-section
        double totalHeight = 2 * flangeHeight + webHeight;
        y=totalHeight/2;
        // Calculate the centroid position from the bottom
        double centroid = (flangeHeight * flangeWidth * flangeHeight / 2 + webHeight * webWidth * (flangeHeight + webHeight / 2)) 
                        / (flangeWidth * flangeHeight * 2 + webWidth * webHeight);

        // Moment of inertia of the top flange about its own centroid
        double FlangeI = rectangleMomentOfInertia(flangeWidth, flangeHeight);

        // Parallel axis theorem to shift the top flange moment of inertia to the I-section centroid
        double FlangeIShifted = FlangeI + flangeWidth * flangeHeight * Math.pow(totalHeight - centroid - flangeHeight / 2, 2);

        
        // Moment of inertia of the web about its own centroid
        double webI = rectangleMomentOfInertia(webWidth, webHeight);

        // Parallel axis theorem to shift the web moment of inertia to the I-section centroid
        double webIShifted = webI + webWidth * webHeight * Math.pow(centroid - flangeHeight - webHeight / 2, 2);

        // Total moment of inertia
        return 2*FlangeIShifted + webIShifted;
    }

    // Method to calculate the maximum bending moment
    public static double calculateBendingMoment(String beamType, String loadType, double load, double length, double point) {
        if (beamType.equalsIgnoreCase("cantilever")) {
            switch (loadType.toLowerCase()) {
                case "point load":
                    return load * (length - point);
                case "uniform load":
                    return (load * Math.pow(length, 2)) / 2.0;
                case "distributed":
                    return (load * Math.pow(length, 2)) / 8.0;
            }
        } else if (beamType.equalsIgnoreCase("simply supported")) {
            switch (loadType.toLowerCase()) {
                case "point load":
                    return (load * point * (length - point)) / length;
                case "uniform load":
                    return (load * Math.pow(length, 2)) / 8.0;
                case "distributed":
                    return (load * Math.pow(length, 2)) / 8.0;
            }
        }
        return 0;
    }

    // Method to calculate bending stress
    public static double calculateBendingStress(double moment, double momentOfInertia, double y) {
        return (moment * y) / momentOfInertia;
    }

    // Method to check failure using Maximum Normal Stress Theory
    public static boolean checkFailureByNormalStress(double bendingStress, double yieldStrength, double factorOfSafety) {
        return bendingStress > (yieldStrength / factorOfSafety);
    }

    // Method to check failure using Maximum Shear Stress Theory
    public static boolean checkFailureByShearStress(double bendingStress, double yieldStrength, double factorOfSafety) {
        return bendingStress > (yieldStrength / (2 * factorOfSafety));
    }

    // Suggest renovations if the beam fails
    public static void suggestRenovations(String shape) {
        System.out.println("Suggested renovations:");
        switch (shape.toLowerCase()) {
            case "rectangular":
                System.out.println("- Increase the height of the beam.");
                break;
            case "i beam":
                System.out.println("- Increase the flange width or height.");
                break;
            case "circular":
                System.out.println("- Increase the diameter of the beam.");
                break;
        }
        System.out.println("- Decrease the Load.");
        System.out.println("- Choose a Material with better Yield Strength.");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Input type of beam, shape of beam, and load type
        System.out.println("Enter type of beam (Cantilever/Simply Supported): ");
        String beamType = scanner.nextLine();

        System.out.println("Enter shape of beam (Rectangular/I Beam/Circular): ");
        String shape = scanner.nextLine();

        System.out.println("Enter type of load (Point Load/Uniform Load/Distributed): ");
        String loadType = scanner.nextLine();

      //Calculate moment of inertia for the chosen beam shape
        double momentOfInertia = 0;
        if (shape.equalsIgnoreCase("rectangular")) {
            momentOfInertia=RMomentOfInertia();
        } else if (shape.equalsIgnoreCase("circular")) {
            momentOfInertia=CMomentOfInertia();
        }
        else if(shape.equalsIgnoreCase("i beam")){
            momentOfInertia=IMomentOfInertia();
        }
        else{
            System.out.println("Wrong Input");
        }
        // Input loading and material properties

        System.out.println("Enter maximum load applied in N: ");
        double load = scanner.nextDouble();

        System.out.println("Enter length of the beam in mm: ");
        double length = scanner.nextDouble();

        System.out.println("Enter point of application of load (for point load) in mm: ");
        double point = scanner.nextDouble();

        System.out.println("Enter yield strength of material in N/mm^2: ");
        double yieldStrength = scanner.nextDouble();

        System.out.println("Enter factor of safety: ");
        double factorOfSafety = scanner.nextDouble();

        // Calculate maximum bending moment based on the beam and load type
        double bendingMoment = calculateBendingMoment(beamType, loadType, load, length, point);

        // Calculate the bending stress
        double bendingStress = calculateBendingStress(bendingMoment, momentOfInertia, y);

        System.out.println("Calculated Bending Stress: " + bendingStress + " N/mm^2");

        // Ask user which failure theory to apply
        System.out.println("Choose failure theory (1. Maximum Normal Stress, 2. Maximum Shear Stress): ");
        int failureTheory = scanner.nextInt();

        boolean isFailed = false;
        if (failureTheory == 1) {
            isFailed = checkFailureByNormalStress(bendingStress, yieldStrength, factorOfSafety);
        } else if (failureTheory == 2) {
            isFailed = checkFailureByShearStress(bendingStress, yieldStrength, factorOfSafety);
        }

        System.out.println();
        System.out.println();
        System.out.println();
        if (isFailed) {
            System.out.println("Beam might fail under the given conditions.");
            suggestRenovations(shape);
        } else {
            System.out.println("Beam is safe under the given conditions.");
        }

        scanner.close();
    }
}