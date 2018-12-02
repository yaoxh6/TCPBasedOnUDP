import java.util.Scanner;
class test{
    public static void main(String [] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("inputyourname");
        String name = sc.nextLine();
        System.out.println("inputyourage");
        int age = sc.nextInt();
        System.out.println("inputyoursalary");
        float salary = sc.nextFloat();
        System.out.println("this is your information");
        System.out.println("name: "+name+"\n"+"age: "+age+"\n"+"salary: "+salary);
    }
}